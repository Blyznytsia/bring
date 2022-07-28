package org.blyznytsia.bpp;

import org.blyznytsia.context.AnnotationApplicationContext;

public interface BeanPostProcessor {

  void configure(Object bean, AnnotationApplicationContext cache);
}
