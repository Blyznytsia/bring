package org.blyznytsia.context;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.bpp.BeanPostProcessor;
import org.blyznytsia.exception.BeanInstantiationException;
import org.blyznytsia.exception.BringException;
import org.blyznytsia.model.BeanDefinition;
import org.reflections.Reflections;

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
   * @param definitions bean definitions from scanners
   */
  public void initiateContext(Set<BeanDefinition> definitions) {
    log.info("Initializing context for {} beanDefinitions", definitions.size());
    definitions = new HashSet<>(definitions);
    processDefinitionsWithNoDependencies(definitions);
    processDefinitionsWithDependencies(definitions);
    log.info("Context was successfully initialized with {} beans", definitions.size());
  }

  /**
   * Process bean definitions with no dependencies
   *
   * @param definitions {@link Set<BeanDefinition>}
   */
  private void processDefinitionsWithNoDependencies(Set<BeanDefinition> definitions) {
    log.debug("Processing beanDefinitions with no dependencies");
    Set<BeanDefinition> toBeCreated =
        definitions.stream().filter(d -> d.getAllDependencies().isEmpty()).collect(toSet());
    log.debug("Found {} beanDefinitions with no dependencies", toBeCreated.size());

    toBeCreated.forEach(this::createBean);
    definitions.removeAll(toBeCreated);
  }

  /**
   * Process bean definitions with dependencies
   *
   * @param definitions {@link Set<BeanDefinition>}
   */
  private void processDefinitionsWithDependencies(Set<BeanDefinition> definitions) {
    log.debug("Processing {} beanDefinitions with dependencies", definitions.size());

    while (!definitions.isEmpty()) {
      var toBeCreated =
          definitions.stream().filter(this::areAllDependenciesCreated).collect(toSet());
      log.debug("Found {} beanDefinitions with dependencies", toBeCreated.size());

      toBeCreated.forEach(this::createBean);
      definitions.removeAll(toBeCreated);
    }
  }

  private boolean areAllDependenciesCreated(BeanDefinition definition) {
    Set<String> allDependencies = definition.getAllDependencies();
    Set<String> existingBeans = context.getContainer().keySet();
    return existingBeans.containsAll(allDependencies);
  }

  /**
   * Creates and configures bean
   *
   * @param definition {@link BeanDefinition}
   */
  @SneakyThrows
  private void createBean(BeanDefinition definition) {
    log.debug("Creating of bean {}", definition.getName());

    Object bean;
    if (definition.isConfigClassDependency()) {
      bean = instantiateConfigBean(definition);
    } else {
      bean = instantiateComponentBean(definition);
    }

    log.debug("Instantiated bean: {}", definition.getName());
    log.debug("Configuring {} bean", definition.getName());
    Object configuredBean = configure(bean);

    context.getContainer().put(definition.getName(), configuredBean);
  }

  private Object instantiateConfigBean(BeanDefinition definition) {
    try {
      var beanMethod = definition.getBeanMethod();
      var methodArgs = findDependencies(definition);
      Object configInstance = definition.getConfigClass().getDeclaredConstructor().newInstance();

      return beanMethod.invoke(configInstance, methodArgs);
    } catch (Exception e) {
      log.error("Failed to create an instance of bean {}", definition);
      throw new BeanInstantiationException(
          "Failed to create an instance of bean %s".formatted(definition), e);
    }
  }

  private Object[] findDependencies(BeanDefinition definition) {
    return definition.getRequiredDependencies().stream()
        .map(className -> context.getContainer().get(className))
        .toArray();
  }

  private Object instantiateComponentBean(BeanDefinition definition) {
    try {
      var constructor = definition.getConstructor();
      var constructorArgs = findDependencies(definition);

      return constructor.newInstance(constructorArgs);
    } catch (Exception e) {
      log.error("Failed to create an instance of bean {}", definition);
      throw new BeanInstantiationException(
          "Failed to create an instance of bean %s".formatted(definition), e);
    }
  }

  /** Initializes post processors */
  private void initPostProcessors() {
    log.debug("Initializing of post processors");
    var postProcessorClasses =
        new Reflections("org.blyznytsia.bpp").getSubTypesOf(BeanPostProcessor.class);
    log.debug("Found {} post processors", postProcessorClasses);

    for (var postProcessorClass : postProcessorClasses) {
      try {
        postProcessors.add(postProcessorClass.getDeclaredConstructor().newInstance());
      } catch (Exception e) {
        log.error(
            "Failed to create bean post-processor of type {}", postProcessorClass.getSimpleName());

        throw new BringException(
            "Failed to create bean post-processor of type " + postProcessorClass.getSimpleName(),
            e);
      }
    }
  }

  /**
   * Configures bean with post processors
   *
   * @param bean bean instance
   * @return configured bean
   */
  private Object configure(Object bean) {
    for (var postProcessor : postProcessors) {
      bean = postProcessor.configure(bean, context);
    }

    return bean;
  }
}
