package org.blyznytsia.processor;

import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.model.BeanDefinition;

public interface BeanPostProcessor {

  default Object configure(Object bean, BeanDefinition beanDefinition, ApplicationContext context) {
    return bean;
  }
}
