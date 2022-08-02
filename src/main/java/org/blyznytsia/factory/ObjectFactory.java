package org.blyznytsia.factory;

import java.util.List;
import org.blyznytsia.model.BeanDefinition;

public interface ObjectFactory {

  void createBeans(List<String> beans);

  Object createBean(BeanDefinition beanDefinition);

  Object configure(Object bean, BeanDefinition beanDefinition);
}
