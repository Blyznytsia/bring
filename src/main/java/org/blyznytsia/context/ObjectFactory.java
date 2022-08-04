package org.blyznytsia.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.bpp.BeanPostProcessor;
import org.blyznytsia.exception.BeanConfigurationException;
import org.blyznytsia.exception.NoDefaultConstructorException;
import org.blyznytsia.model.BeanDefinition;

/**
 * Creates beans and fill out the context container
 *
 * @see org.blyznytsia.context.ApplicationContext
 * @see BeanPostProcessor
 */
@Slf4j
public class ObjectFactory {

  /** Provide access to context container */
  private final ApplicationContext context;
  /** List of already initialized bean post processors */
  private final List<BeanPostProcessor> postProcessors = new ArrayList<>();

  /**
   * Creates object factory and initializes bean post processors
   *
   * @param context {@link ApplicationContext}
   */
  public ObjectFactory(ApplicationContext context) {
    this.context = context;
    initPostProcessors();
  }

  /**
   * Creates beans from bean definitions and fill out the context container
   *
   * <pre>
   * <b>Steps:</b>
   * 1. Create copy of bean definitions list
   * 2. Process bean definitions with no dependencies
   * 3. Process other bean definitions
   * </pre>
   *
   * @param beanDefinitions bean definitions from scanners
   */
  public void initiateContext(Set<BeanDefinition> beanDefinitions) {
    log.info("Initializing context for {} beanDefinitions", beanDefinitions.size());
    var set = new HashSet<>(beanDefinitions);
    processEmptyFirst(set);
    processOthers(set);
    log.info("Context was successfully initialized with {} beans", beanDefinitions.size());
  }

  /**
   * Process bean definitions with no dependencies
   *
   * @param set {@link Set<BeanDefinition>}
   */
  private void processEmptyFirst(Set<BeanDefinition> set) {
    log.debug("Processing beanDefinitions with no dependencies");
    var toBeCreated =
        set.stream().filter(el -> el.getDependsOnBeans().isEmpty()).collect(Collectors.toSet());
    log.debug("Found {} beanDefinitions with no dependencies", toBeCreated.size());

    set.removeAll(toBeCreated);
    toBeCreated.forEach(this::createBean);
  }

  /**
   * Process bean definitions with dependencies
   *
   * @param set {@link Set<BeanDefinition>}
   */
  private void processOthers(Set<BeanDefinition> set) {
    log.debug("Processing {} beanDefinitions with dependencies", set.size());
    while (!set.isEmpty()) {
      var toBeCreated =
          set.stream()
              .filter(el -> context.getContainer().keySet().containsAll(el.getDependsOnBeans()))
              .collect(Collectors.toSet());
      log.debug("Found {} beanDefinitions with dependencies", toBeCreated.size());

      set.removeAll(toBeCreated);
      toBeCreated.forEach(this::createBean);
    }
  }

  /**
   * Creates and configures bean
   *
   * @param beanDefinition {@link BeanDefinition}
   */
  @SneakyThrows
  private void createBean(BeanDefinition beanDefinition) {
    log.debug("Creating of bean {}", beanDefinition.getName());
    Object bean;

    try {
      bean = beanDefinition.getType().getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      var msg = String.format("No default constructor in %s", beanDefinition.getType());
      log.error(msg);
      throw new BeanConfigurationException(
          "Exception during bean creation", new NoDefaultConstructorException(msg, e));
    }

    context.getContainer().put(beanDefinition.getName(), configure(bean, beanDefinition));
  }

  /** Initializes post processors */
  @SneakyThrows
  private void initPostProcessors() {
    log.debug("Initializing of post processors");
    var postProcessorClasses = context.getReflections().getSubTypesOf(BeanPostProcessor.class);
    log.debug("Found {} post processors", postProcessorClasses.size());

    for (var postProcessorClass : postProcessorClasses) {
      postProcessors.add(postProcessorClass.getDeclaredConstructor().newInstance());
    }
  }

  /**
   * Configures bean with post processors
   *
   * @param bean bean instance
   * @param beanDefinition {@link BeanDefinition}
   * @return configured bean
   */
  private Object configure(Object bean, BeanDefinition beanDefinition) {
    log.debug("Configuring {} bean", beanDefinition.getName());
    for (var postProcessor : postProcessors) {
      bean = postProcessor.configure(bean, context);
    }

    return bean;
  }
}
