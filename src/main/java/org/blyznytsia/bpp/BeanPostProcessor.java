package org.blyznytsia.bpp;

import org.blyznytsia.context.ApplicationContext;

/**
 * A hook that allows configuring beans instantiated by {@link
 * org.blyznytsia.processor.BeanFactoryProcessor}.
 */
public interface BeanPostProcessor {

  /**
   * Configures a passed bean according to the logic provided by a concrete implementation of {@link
   * BeanPostProcessor}
   *
   * @param bean bean to be configured
   * @param context application context to access the beans
   */
  default Object configure(Object bean, ApplicationContext context) {
    return bean;
  }
}
