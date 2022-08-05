package org.blyznytsia.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.bpp.BeanPostProcessor;
import org.blyznytsia.bpp.ProxyBeanProcessor;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.model.BeanDefinition;

/**
 * Creates beans and fill out the context container
 *
 * @see org.blyznytsia.context.ApplicationContext
 * @see BeanPostProcessor
 */
@Slf4j
public class BeanFactoryProcessor {

  /** Provide access to context container */
  private final ApplicationContext context;
  /** List of already initialized bean post processors */
  private final List<BeanPostProcessor> postProcessors = new ArrayList<>();

  private final List<ProxyBeanProcessor> proxyBeanProcessors = new ArrayList<>();

  /**
   * Creates object factory and initializes bean post processors
   *
   * @param context {@link ApplicationContext}
   */
  public BeanFactoryProcessor(ApplicationContext context) {
    this.context = context;
    initPostProcessors();
    initProxyBeanProcessors();
  }

  @SneakyThrows
  private void initProxyBeanProcessors() {
    Set<Class<? extends ProxyBeanProcessor>> proxies =
        context.getReflections().getSubTypesOf(ProxyBeanProcessor.class);
    for (Class<? extends ProxyBeanProcessor> proxy : proxies) {
      ProxyBeanProcessor proxyBeanProcessor = proxy.getDeclaredConstructor().newInstance();
      proxyBeanProcessors.add(proxyBeanProcessor);
    }
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
  public void initiateContext(List<BeanDefinition> beanDefinitions) {
    log.info("Initializing context for {} beanDefinitions", beanDefinitions.size());
    var list = new ArrayList<>(beanDefinitions);
    processEmptyFirst(list);
    processOthers(list);
    log.info("Context was successfully initialized with {} beans", beanDefinitions.size());
  }

  /**
   * Process bean definitions with no dependencies
   *
   * @param list {@link List<BeanDefinition>}
   */
  private void processEmptyFirst(List<BeanDefinition> list) {
    log.debug("Processing beanDefinitions with no dependencies");
    var toBeCreated = list.stream().filter(el -> el.getDependsOnBeans().isEmpty()).toList();
    log.debug("Found {} beanDefinitions with no dependencies", toBeCreated.size());

    list.removeAll(toBeCreated);
    toBeCreated.forEach(this::createBean);
  }

  /**
   * Process bean definitions with dependencies
   *
   * @param list {@link List<BeanDefinition>}
   */
  private void processOthers(List<BeanDefinition> list) {
    log.debug("Processing {} beanDefinitions with dependencies", list.size());
    while (!list.isEmpty()) {
      var toBeCreated =
          list.stream()
              .filter(el -> context.getContainer().keySet().containsAll(el.getDependsOnBeans()))
              .toList();
      log.debug("Found {} beanDefinitions with dependencies", toBeCreated.size());

      list.removeAll(toBeCreated);
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
    var bean = beanDefinition.getType().getDeclaredConstructor().newInstance();

    Object object = configure(bean, beanDefinition);

    Object proxyObject = proxyObject(object);

    context.getContainer().put(beanDefinition.getName(), proxyObject);
  }

  private Object proxyObject(Object object) {
    Object proxyObject = object;
    for (ProxyBeanProcessor proxyBeanProcessor : proxyBeanProcessors) {
      proxyObject = proxyBeanProcessor.configure(object);
    }

    return proxyObject;
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