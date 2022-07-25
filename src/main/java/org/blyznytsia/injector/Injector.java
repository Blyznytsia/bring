package org.blyznytsia.injector;

import java.util.List;
import java.util.Map;
import org.blyznytsia.model.BeanDefinition;

public interface Injector {

  <T> T injectBean(BeanDefinition beanDefinition);

  Map<BeanDefinition, Object> injectBeans(List<BeanDefinition> beanDefinitions);
}
