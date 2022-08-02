package org.blyznytsia.factory;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.context.DependencyTreeOrder;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.processor.BeanPostProcessor;

@Slf4j
public class BeanObjectFactory implements ObjectFactory {

  private final ApplicationContext context;
  private final List<BeanPostProcessor> postProcessors = new ArrayList<>();
  private DependencyTreeOrder dependencyTreeOrder;
  private List<BeanDefinition> beanDefinitions;
  private Map<String, BeanDefinition> beanNameDefinitions;

  public BeanObjectFactory(ApplicationContext context) {
    this.context = context;
    initPostProcessors();
  }

  public void initiateContext(List<BeanDefinition> beanDefinitions) {
    this.beanDefinitions = beanDefinitions;
    this.beanNameDefinitions =
        beanDefinitions.stream().collect(toMap(BeanDefinition::getName, Function.identity()));

    dependencyTreeOrder = new DependencyTreeOrder(this.beanDefinitions);
    List<String> sortedBeans = dependencyTreeOrder.buildSortedBeans();
    createBeans(sortedBeans);
  }

  public void createBeans(List<String> beans) {
    beans.stream().map(beanName -> beanNameDefinitions.get(beanName)).forEach(this::createBean);
  }

  @SneakyThrows
  public Object createBean(BeanDefinition beanDefinition) {
    var bean = beanDefinition.getType().getDeclaredConstructor().newInstance();
    log.debug("Creating of bean {}", beanDefinition.getName());
    context.getContainer().put(beanDefinition, configure(bean, beanDefinition));
    return bean;
  }

  @SneakyThrows
  private void initPostProcessors() {
    var postProcessorClasses = context.getReflections().getSubTypesOf(BeanPostProcessor.class);

    for (var postProcessorClass : postProcessorClasses) {
      postProcessors.add(postProcessorClass.getDeclaredConstructor().newInstance());
    }
  }

  public Object configure(Object bean, BeanDefinition beanDefinition) {
    for (var postProcessor : postProcessors) {
      bean = postProcessor.configure(bean, beanDefinition, context);
    }
    return bean;
  }
}
