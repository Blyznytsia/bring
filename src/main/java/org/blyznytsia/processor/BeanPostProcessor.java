package org.blyznytsia.processor;

import org.blyznytsia.model.BeanDefinition;

public interface BeanPostProcessor {

    default Object configure(Object bean, BeanDefinition beanDefinition) {
        return bean;
    }
}
