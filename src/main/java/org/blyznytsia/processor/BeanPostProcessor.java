package org.blyznytsia.processor;

import org.blyznytsia.model.BeanDefinition;

import java.util.Map;

public interface BeanPostProcessor {

    default Object configure(Object bean, BeanDefinition beanDefinition, Map<BeanDefinition, Object> container) {
        return bean;
    }
}
