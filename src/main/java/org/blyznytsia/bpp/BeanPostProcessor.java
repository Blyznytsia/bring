package org.blyznytsia.bpp;

import org.blyznytsia.context.ApplicationContext;

public interface BeanPostProcessor {

  void configure(Object bean, ApplicationContext context);
}
