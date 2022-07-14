package org.blyznytsia.context;

import org.blyznytsia.scanner.BeanScanner;

import java.util.Map;

public class AnnotationApplicationContext implements ApplicationContext {

    private BeanScanner scanner;
    @Override
    public <T> T getBean(Class<T> beanType) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> beanType, String beanName) {
        return null;
    }

    @Override
    public <T> Map<String, Object> getAllBeans(Class<T> beanType) {
        return null;
    }
}
