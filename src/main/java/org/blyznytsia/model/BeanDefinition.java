package org.blyznytsia.model;

import java.util.List;

public class BeanDefinition<T> {

    Class<T> beanType;
    String name;
    Scope scope = Scope.SINGLETON;

    List<BeanDefinition<?>> dependOnBeans;

}
