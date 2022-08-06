package org.blyznytsia.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
// TODO: define fields to be used in equals and hashcode
public class BeanDefinition {

  private Class<?> type;
  private String name;
  @Builder.Default private Scope scope = Scope.SINGLETON;
  private Set<String> fieldDependencies;
  // constructor or @Bean method dependencies
  private Set<String> requiredDependencies;
  // true for beans declared in @Configuration classes
  private boolean configClassDependency;
  private Constructor<?> constructor;

  //  for bean definitions from @Configuration classes
  private Class<?> configClass;
  //  a reference to a method annotated with @Bean
  private Method beanMethod;
  //  stores method name from @Bean(initMethod="")
  private String initMethod;

  public Set<String> getAllDependencies() {
    Set<String> dependencies = new HashSet<>();
    dependencies.addAll(fieldDependencies);
    dependencies.addAll(requiredDependencies);
    return dependencies;
  }
}
