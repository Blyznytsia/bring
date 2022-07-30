package org.blyznytsia.factory;

import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.bpp.BeanPostProcessor;
import org.blyznytsia.context.AnnotationApplicationContext;
import org.blyznytsia.model.BeanDefinition;
import org.reflections.Reflections;

@Slf4j
public class ObjectFactory {
  private final List<BeanPostProcessor> beanPostProcessors;
  private final AnnotationApplicationContext context;

  public ObjectFactory(AnnotationApplicationContext context) {
    this.context = context;
    this.beanPostProcessors = initPostProcessors();
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

  public Object createBean(BeanDefinition definition) {
    if (context.contains(definition)) {
      return context.getBean(definition.getType());
    }

    log.debug("PROCESSING BEAN WITH NAME: {}", definition.getName());

    Object bean;
    if (definition.isConfigClassDependency()) {
      bean = instantiateConfigBean(definition);
    } else {
      bean = instantiateComponentBean(definition);
    }

    beanPostProcessors.forEach(bpp -> bpp.configure(bean, context));

    return bean;
  }

  @SneakyThrows
  private Object instantiateConfigBean(BeanDefinition definition) {
    var beanMethod = definition.getBeanMethod();
    var methodArgs = definition.getRequiredDependencies().stream().map(this::createBean).toArray();

    Object configInstance = definition.getConfigClass().getDeclaredConstructor().newInstance();
    Object instance = beanMethod.invoke(configInstance, methodArgs);
    log.debug("INSTANTIATED: {}", definition.getName());

    return instance;
  }

  private Object instantiateComponentBean(BeanDefinition definition) {
    try {
      var constructor = definition.getConstructor();
      var constructorArgs =
          definition.getRequiredDependencies().stream().map(this::createBean).toArray();

      Object instance = constructor.newInstance(constructorArgs);
      log.debug("INSTANTIATED: {}", definition.getName());
      return instance;
    } catch (Exception e) {
      throw new RuntimeException(definition.toString(), e);
    }
  }
}
