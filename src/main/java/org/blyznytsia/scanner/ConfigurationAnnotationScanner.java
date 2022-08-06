package org.blyznytsia.scanner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Bean;
import org.blyznytsia.annotation.Configuration;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.util.BeanDefinitionUtils;
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
   * @param packageName package to be scanned
   * @return a set of created {@link BeanDefinition}
   */
  @Override
  public Set<BeanDefinition> scan(@NonNull String packageName) {
    if (packageName.isBlank()) {
      throw new IllegalArgumentException("Blank package isn't allowed");
    }

    log.info("Scanning '{}' package for classes annotated wth @Configuration", packageName);
    var configurationsClasses =
        new Reflections(packageName).getTypesAnnotatedWith(Configuration.class);
    log.debug("Found configurations classes: {}", configurationsClasses);

    var allDefinitions =
        configurationsClasses.stream()
            .map(this::createDefinitionsForDeclaredBeans)
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(HashSet::new));

    log.debug("Scanning is finished. Found bean definitions: {}", allDefinitions);

    return allDefinitions;
  }

  private Set<BeanDefinition> createDefinitionsForDeclaredBeans(Class<?> configClass) {
    return Arrays.stream(configClass.getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(Bean.class))
        .map(m -> createDefinition(configClass, m))
        .collect(Collectors.toSet());
  }

  private BeanDefinition createDefinition(Class<?> configClass, Method method) {
    return BeanDefinition.builder()
        .configClass(configClass)
        .configClassDependency(true)
        .beanMethod(method)
        .name(resolveBeanNameFromMethod(method))
        .type(method.getReturnType())
        .initMethod(method.getAnnotation(Bean.class).initMethod())
        .fieldDependencies(Collections.emptySet())
        .requiredDependencies(BeanDefinitionUtils.findRequiredDependencies(method))
        .build();
  }

  private String resolveBeanNameFromMethod(Method beanMethod) {
    Bean annotation = beanMethod.getAnnotation(Bean.class);
    String beanName = annotation.value();
    return beanName.isEmpty() ? beanMethod.getName() : beanName;
  }
}
