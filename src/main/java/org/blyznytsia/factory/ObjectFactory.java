package org.blyznytsia.factory;

import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.processor.BeanPostProcessor;

@Slf4j
public class ObjectFactory {

  private final ApplicationContext context;
  private final List<BeanPostProcessor> postProcessors = new ArrayList<>();

  public ObjectFactory(ApplicationContext context) {
    this.context = context;
    initPostProcessors();
  }

  public void initiateContext(List<BeanDefinition> beanDefinitions) {
    log.info("Initializing context for {} beanDefinitions", beanDefinitions.size());
    var list = new ArrayList<>(beanDefinitions);
    processEmptyFirst(list);
    processOthers(list);
    log.info("Context was initialized with {} beans", beanDefinitions.size());
  }

  private void processEmptyFirst(List<BeanDefinition> list) {
    var toBeCreated = list.stream().filter(el -> el.getDependsOnBeans().isEmpty()).toList();
    list.removeAll(toBeCreated);
    toBeCreated.forEach(this::createBean);
  }

  private void processOthers(List<BeanDefinition> list) {
    while (!list.isEmpty()) {
      var toBeCreated =
          list.stream()
              .filter(el -> context.getContainer().keySet().containsAll(el.getDependsOnBeans()))
              .toList();
      list.removeAll(toBeCreated);
      toBeCreated.forEach(this::createBean);
    }
  }

  @SneakyThrows
  private void createBean(BeanDefinition beanDefinition) {
    var bean = beanDefinition.getType().getDeclaredConstructor().newInstance();
    log.debug("Creating of bean {}", beanDefinition.getName());
    context.getContainer().put(beanDefinition.getName(), configure(bean, beanDefinition));
  }

  @SneakyThrows
  private void initPostProcessors() {
    var postProcessorClasses = context.getReflections().getSubTypesOf(BeanPostProcessor.class);

    for (var postProcessorClass : postProcessorClasses) {
      postProcessors.add(postProcessorClass.getDeclaredConstructor().newInstance());
    }
  }

  private Object configure(Object bean, BeanDefinition beanDefinition) {
    for (var postProcessor : postProcessors) {
      bean = postProcessor.configure(bean, beanDefinition, context);
    }
    return bean;
  }
}
