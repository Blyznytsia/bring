package org.blyznytsia.factory;

import lombok.SneakyThrows;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.processor.BeanPostProcessor;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectFactory {

    private final Map<BeanDefinition, Object> container;
    private final Reflections reflections;
    private final List<BeanPostProcessor> postProcessors = new ArrayList<>();

    public ObjectFactory(Reflections reflections, Map<BeanDefinition, Object> container) {
        this.container = container;
        this.reflections = reflections;
        initPostProcessors();
    }

    @SneakyThrows
    public void createObject(BeanDefinition beanDefinition) {
        var bean = beanDefinition.getType().getDeclaredConstructor().newInstance();
        container.put(beanDefinition, configure(bean, beanDefinition));
    }

    public void createObjects(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(this::createObject);
    }

    @SneakyThrows
    private void initPostProcessors() {
        var postProcessorClasses =
                reflections.getSubTypesOf(BeanPostProcessor.class);

        for (var postProcessorClass : postProcessorClasses) {
            postProcessors.add(postProcessorClass.getDeclaredConstructor().newInstance());
        }
    }

    private Object configure(Object bean, BeanDefinition beanDefinition) {
        for (var postProcessor : postProcessors) {
            bean = postProcessor.configure(bean, beanDefinition, container);
        }

        return bean;
    }
}
