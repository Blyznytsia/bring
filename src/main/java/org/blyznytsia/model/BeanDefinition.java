package org.blyznytsia.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BeanDefinition {

  @EqualsAndHashCode.Include private Class<?> type;
  @EqualsAndHashCode.Include private String name;
  @EqualsAndHashCode.Include private String qualifiedName;
  @Builder.Default private Scope scope = Scope.SINGLETON;
  private List<BeanDefinition> requiredDependencies;
  private List<BeanDefinition> fieldDependencies;
  // true for beans declared in @Configuration classes
  private boolean configClassDependency;
  private Constructor<?> constructor;

  //  for bean definitions from @Configuration classes
  private Class<?> configClass;
  //  a reference to a method annotated with @Bean
  private Method beanMethod;
  //  stores method name from @Bean(initMethod="")
  private String initMethod;
}
