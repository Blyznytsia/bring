package org.blyznytsia.injector;

import org.blyznytsia.model.BeanDefinition;

import java.util.List;
import java.util.Map;

public interface Injector {

    <T> T injectBean(BeanDefinition beanDefinition);

    Map<BeanDefinition, Object> injectBeans(List<BeanDefinition> beanDefinitions);

}
