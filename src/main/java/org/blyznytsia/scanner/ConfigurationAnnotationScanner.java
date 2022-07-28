package org.blyznytsia.scanner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Bean;
import org.blyznytsia.annotation.Configuration;
import org.blyznytsia.model.BeanDefinition;
import org.reflections.Reflections;

/**
 * Classpath scanner responsible for finding Java configuration classes with beans declared in these
 * classes and building {@link BeanDefinition} out of them.
 */
@Slf4j
public class ConfigurationAnnotationScanner implements BeanScanner {

  /**
   * Scans configuration classes annotated with {@link org.blyznytsia.annotation.Component},
   * identifies methods annotated with {@link Bean} within those classes and builds {@link
   * BeanDefinition} out of them.
   *
   * @param packages package to be scanned
   * @return a list of created {@link BeanDefinition}
   */
  @Override
  public List<BeanDefinition> scan(String... packages) {
    if (packages.length == 0) {
      throw new IllegalArgumentException("Package cannot be blank");
    }

    log.info("Scanning '{}' package for classes annotated wth @Configuration", packages);
    var configurationsClasses =
        new Reflections((Object[]) packages).getTypesAnnotatedWith(Configuration.class);
    log.debug("Found configurations classes: {}", configurationsClasses);

    List<BeanDefinition> definitions =
        configurationsClasses.stream()
            .map(this::findDefinitionsForDeclaredBeans)
            .flatMap(Collection::stream)
            .toList();

    log.debug("Scanning is finished. Found bean definitions: {}", definitions);

    return definitions;
  }

  private List<BeanDefinition> findDefinitionsForDeclaredBeans(Class<?> configClass) {
    return Arrays.stream(configClass.getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(Bean.class))
        .map(m -> createDefinition(configClass, m))
        .toList();
  }

  private BeanDefinition createDefinition(Class<?> configClass, Method method) {
    List<BeanDefinition> dependencies = findDependencies(configClass, method);
    return BeanDefinition.builder()
        .configClass(configClass)
        .configClassDependency(true)
        .beanMethod(method)
        .name(resolveBeanNameFromMethod(method))
        .type(method.getReturnType())
        .initMethod(method.getAnnotation(Bean.class).initMethod())
        .requiredDependencies(dependencies)
        .fieldDependencies(Collections.emptyList())
        .build();
  }

  // TODO: move methods for extracting information for BeanDefinition fields to BeanDefinition or
  // helper class.
  // TODO: revisit type for List<String> dependencies ... should be map
  private List<BeanDefinition> findDependencies(Class<?> configClass, Method method) {
    List<BeanDefinition> dependencies = new ArrayList<>();

    List<Class<?>> methodParams = Arrays.stream(method.getParameterTypes()).toList();

    for (Class<?> methodParam : methodParams) {
      List<Method> beanMethods =
          Arrays.stream(configClass.getDeclaredMethods())
              .filter(m -> m.isAnnotationPresent(Bean.class))
              .filter(m -> m.getReturnType().equals(methodParam))
              .toList();

      for (Method beanMethod : beanMethods) {
        BeanDefinition dependency = createDefinition(configClass, beanMethod);
        dependencies.add(dependency);
      }
    }

    return dependencies;
  }

  private String resolveBeanNameFromMethod(Method beanMethod) {
    Bean annotation = beanMethod.getAnnotation(Bean.class);
    String beanName = annotation.value();
    return beanName.isEmpty() ? beanMethod.getName() : beanName;
  }

  private String resolveBeanName(Class<?> type) {
    String className = type.getSimpleName();
    return Character.toLowerCase(className.charAt(0)) + className.substring(1);
  }
}
