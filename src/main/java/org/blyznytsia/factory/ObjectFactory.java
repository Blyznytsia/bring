package org.blyznytsia.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.bpp.BeanPostProcessor;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.util.DependencyGraph;
import org.reflections.Reflections;

@Slf4j
public class ObjectFactory {

  private final Map<BeanDefinition, Object> cache = new HashMap<>();
  private final List<BeanPostProcessor> beanPostProcessors;

  public ObjectFactory() {
    this.beanPostProcessors = initPostProcessors();
  }

  public Map<BeanDefinition, Object> createBeans(List<BeanDefinition> definitions) {
    DependencyGraph dependencyGraph = new DependencyGraph(definitions);
    dependencyGraph.sort().forEach(this::createBean);
    return cache;
  }

  @SneakyThrows
  private List<BeanPostProcessor> initPostProcessors() {
    Reflections reflections = new Reflections("org.blyznytsia.bpp");
    var targetClasses = reflections.getSubTypesOf(BeanPostProcessor.class);

    List<BeanPostProcessor> processors = new ArrayList<>();
    for (var type : targetClasses) {
      BeanPostProcessor instance = type.getConstructor().newInstance();
      processors.add(instance);
    }

    return processors;
  }

  private void createBean(BeanDefinition definition) {
    if (!cache.containsKey(definition)) {
      log.debug("PROCESSING BEAN WITH NAME: {}", definition.getName());
      Object bean = createComponentBean(definition);
      beanPostProcessors.forEach(bpp -> bpp.configure(bean, cache));
      cache.put(definition, bean);
    }
  }

  private Object createComponentBean(BeanDefinition definition) {
    definition.getRequiredDependencies().forEach(this::createBean);
    return instantiateComponentBean(definition);
  }

  @SneakyThrows
  private Object instantiateComponentBean(BeanDefinition definition) {
    var constructor = definition.getConstructor();
    var constructorArgs = definition.getRequiredDependencies().stream().map(cache::get).toArray();

    Object instance = constructor.newInstance(constructorArgs);
    log.debug("INSTANTIATED: {}", definition);

    return instance;
  }
}
