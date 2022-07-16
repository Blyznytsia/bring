package org.blyznytsia.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BeanDefinition {

    private Class<?> beanType;
    private String name;
    private Class<?> configClass;
    private String beanClassName;

    private Scope scope = Scope.SINGLETON;

    private List<String> dependsOnBeans;
    private boolean isConfigClass;
}
