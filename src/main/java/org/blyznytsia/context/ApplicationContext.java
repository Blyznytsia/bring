package org.blyznytsia.context;


import java.util.Map;

public interface ApplicationContext {

    <T> T getBean(Class<T> beanType);

    <T> T getBean(Class<T> beanType, String beanName);

    <T> Map<String, Object> getAllBeans(Class<T> beanType);
}
