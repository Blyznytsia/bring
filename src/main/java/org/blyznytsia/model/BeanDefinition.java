package org.blyznytsia.model;

import java.lang.reflect.Method;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
// TODO: define fields to be used in equals and hashcode
public class BeanDefinition {

  private Class<?> type;
  private String name;
  @Builder.Default private Scope scope = Scope.SINGLETON;
  private Set<String> dependsOnBeans;
  // true for beans declared in @Configuration classes
  private boolean configClassDependency;

  //  for bean definitions from @Configuration classes
  private Class<?> configClass;
  //  a reference to a method annotated with @Bean
  private Method beanMethod;
  //  stores method name from @Bean(initMethod="")
  private String initMethod;
}
