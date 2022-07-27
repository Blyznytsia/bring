package org.blyznytsia.factory;

import lombok.SneakyThrows;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.processor.BeanPostProcessor;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectFactory {

    private final Reflections reflections;
    private final List<BeanPostProcessor> postProcessors = new ArrayList<>();

    public ObjectFactory(Reflections reflections) {
        this.reflections = reflections;
        initPostProcessors();
    }

    @SneakyThrows
    public Object createObject(BeanDefinition beanDefinition) {
        var bean = beanDefinition.getType().getDeclaredConstructor().newInstance();
        return configure(bean, beanDefinition);
    }

    public Map<BeanDefinition, Object> createObjects(List<BeanDefinition> beanDefinitions) {
        return beanDefinitions.stream()
                .collect(Collectors.toMap(Function.identity(), this::createObject));
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
            bean = postProcessor.configure(bean, beanDefinition);
        }

        return bean;
    }
}
