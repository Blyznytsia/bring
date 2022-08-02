package org.blyznytsia.scanner;

import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
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
   * @param packageName object of type {@link String} that represents packages to scan
   * @return {@link java.util.List} of {@link BeanDefinition}
   */
  @Override
  public List<BeanDefinition> scan(@NonNull String packageName) {
    log.info("Scanning '{}' package for classes annotated wth @Component", packageName);

    var reflections = new Reflections(packageName);
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
    return BeanDefinition.builder()
        .type(targetClass)
        .name(getBeanDefinitionName(targetClass))
        .dependsOnBeans(getAutowiredFields(targetClass))
        .build();
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
   * @param targetClass object of type {@link Class} that is annotated with @{@link Component}
   *     annotation
   * @return {@link java.util.List} of {@link String} that represents bean dependencies
   */
  private List<String> getAutowiredFields(Class<?> targetClass) {
    return Arrays.stream(targetClass.getDeclaredFields())
        .filter(el -> el.isAnnotationPresent(AUTOWIRED_ANNOTATION))
        .map(el -> getBeanDefinitionName(el.getType()))
        .toList();
  }
}
