package org.blyznytsia.bpp;

import java.util.Map;
import org.blyznytsia.model.BeanDefinition;

public interface BeanPostProcessor {

  void configure(Object bean, Map<BeanDefinition, Object> cache);
}
