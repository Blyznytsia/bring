package org.blyznytsia.injector;

import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.exception.NotImplementedException;
import org.blyznytsia.model.BeanDefinition;

import java.util.List;
import java.util.Map;

public class BeanInjector implements Injector {

    private ApplicationContext context;

    public BeanInjector(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public <T> T injectBean(BeanDefinition beanDefinition) {
        throw new NotImplementedException("Need to implement this method");
    }

    @Override
    public Map<BeanDefinition, Object> injectBeans(List<BeanDefinition> beanDefinitions) {
        throw new NotImplementedException("Need to implement this method");
    }
}
