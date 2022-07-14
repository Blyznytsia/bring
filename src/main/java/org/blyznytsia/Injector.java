package org.blyznytsia;

import org.blyznytsia.model.BeanDefinition;

import java.util.List;
import java.util.Map;

public interface Injector {

    <T> T injectBean(BeanDefinition<T> beanDefinition);

    Map<BeanDefinition<?>, Object> injectBeans(List<BeanDefinition<?>> beanDefinitions);

}
