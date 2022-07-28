package org.blyznytsia.scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.model.BeanDefinition;
import org.reflections.Reflections;

/**
 * Scanner for the @{@link Component} annotation.
 *
 * @author Oleksandr Vashchenko
 */
@Slf4j
public class ComponentAnnotationScanner implements BeanScanner {

  /** Constant of type {@link Class} that represents @{@link Component} annotation */
  private static final Class<Component> COMPONENT_ANNOTATION = Component.class;

  /** Constant of type {@link Class} that represents @{@link Autowired} annotation */
  private static final Class<Autowired> AUTOWIRED_ANNOTATION = Autowired.class;

  private static final String INVALID_INJECT_MARKED_CONSTRUCTOR_EXCEPTION =
      "Error creating bean with name '%s': Invalid inject-marked constructor: %s. Found constructor with 'required' Autowire annotation already: %s";

  /**
   * Scans packages to find classes annotated with @{@link Component} annotation
   *
   * <pre>
   * <b>Steps:</b>
   * 1. Scan provided package
   * 2. Find all classes annotated with @{@link Component} annotation
   * 3. Create {@link BeanDefinition} based on these classes
   * 4. If the class has fields annotated with @{@link Autowired} annotation, then add dependencies to {@link BeanDefinition#dependsOnBeans}
   * 5. Return {@link java.util.List} of {@link BeanDefinition}
   * </pre>
   *
   * @param packages object of type {@link String} that represents packages to scan
   * @return {@link java.util.List} of {@link BeanDefinition}
   */
  @Override
  public List<BeanDefinition> scan(String... packages) {
    log.info("Scanning '{}' package for classes annotated wth @Component", packages);

    var reflections = new Reflections((Object[]) packages);
    var targetClasses = reflections.getTypesAnnotatedWith(COMPONENT_ANNOTATION);

    log.debug("Found @Component classes: {}", targetClasses);

    var beanDefinitions = targetClasses.stream().map(this::createBeanDefinition).toList();
    log.debug("Created BeanDefinition classes: {}", beanDefinitions);

    return beanDefinitions;
  }

  /**
   * @param targetClass object of type {@link Class} that is annotated with @{@link Component}
   *     annotation
   * @return {@link BeanDefinition}
   */
  private BeanDefinition createBeanDefinition(Class<?> targetClass) {
    String name = getBeanDefinitionName(targetClass);
    Constructor<?> constructor = findConstructor(targetClass, name);

    var constructorDependencies = getConstructorDependencies(constructor);
    var fieldDependencies = getAutowiredFieldDependencies(targetClass);

    return BeanDefinition.builder()
        .type(targetClass)
        .name(name)
        .constructor(constructor)
        .requiredDependencies(constructorDependencies)
        .fieldDependencies(fieldDependencies)
        .build();
  }

  private List<BeanDefinition> getAutowiredFieldDependencies(Class<?> targetClass) {
    return Arrays.stream(targetClass.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Autowired.class))
        .map(Field::getType)
        .map(this::createBeanDefinition)
        .toList();
  }

  /**
   * @param targetClass object of type {@link Class} that is annotated with @{@link Component}
   *     annotation
   * @return @{@link Component#value()} or {@link Class#getSimpleName()} starting with lowercase
   *     letter
   */
  private String getBeanDefinitionName(Class<?> targetClass) {
    var annotationValue = targetClass.getAnnotation(COMPONENT_ANNOTATION).value();
    var simpleName =
        targetClass.getSimpleName().substring(0, 1).toLowerCase()
            + targetClass.getSimpleName().substring(1);

    return annotationValue.isBlank() ? simpleName : annotationValue;
  }

  /**
   * @param constructor object of type {@link Class} that is annotated with @{@link Component}
   *     annotation
   * @return {@link java.util.List} of {@link String} that represents bean dependencies
   */
  private List<BeanDefinition> getConstructorDependencies(Constructor<?> constructor) {
    return Arrays.stream(constructor.getParameters())
        .map(Parameter::getType)
        .map(this::createBeanDefinition)
        .toList();
  }

  private Constructor<?> findConstructor(Class<?> type, String name) {
    Constructor<?> defaultConstructor = null;
    Constructor<?> annotatedConstructor = null;

    for (var constructor : type.getDeclaredConstructors()) {

      if (constructor.getParameterCount() == 0) {
        defaultConstructor = constructor;
      }

      if (constructor.isAnnotationPresent(Autowired.class)) {
        if (annotatedConstructor != null) {
          throw new RuntimeException(
              String.format(
                  INVALID_INJECT_MARKED_CONSTRUCTOR_EXCEPTION,
                  name,
                  annotatedConstructor,
                  constructor));
        }

        annotatedConstructor = constructor;
      }
    }

    if (annotatedConstructor == null && defaultConstructor == null) {
      throw new RuntimeException("No default constructor found for type " + type);
    }

    return annotatedConstructor != null ? annotatedConstructor : defaultConstructor;
  }
}
