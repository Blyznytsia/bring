package org.blyznytsia.processor;

import org.blyznytsia.context.ApplicationContext;

public interface BeanPostProcessor {

    default Object configureBeforeInitialization(Object bean, ApplicationContext applicationContext) {
        return bean;
    }

    default Object configureAfterInitialization(Object bean, ApplicationContext applicationContext) {
        return bean;
    }
}
