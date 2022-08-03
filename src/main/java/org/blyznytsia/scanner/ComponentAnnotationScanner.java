package org.blyznytsia.scanner;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.util.Helper;
import org.reflections.Reflections;

/** Scanner for the @{@link Component} annotation. */
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
   * 5. Return {@link java.util.Set} of {@link BeanDefinition}
   * </pre>
   *
   * @param packageName object of type {@link String} that represents packages to scan
   * @return {@link java.util.Set} of {@link BeanDefinition}
   */
  @Override
  public Set<BeanDefinition> scan(@NonNull String packageName) {
    log.info("Scanning '{}' package for classes annotated wth @Component", packageName);

    var reflections = new Reflections(packageName);
    var targetClasses = reflections.getTypesAnnotatedWith(COMPONENT_ANNOTATION);

    log.debug("Found @Component classes: {}", targetClasses);

    var beanDefinitions =
        targetClasses.stream().map(this::createBeanDefinition).collect(Collectors.toSet());
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
        .name(resolveBeanName(targetClass))
        .dependsOnBeans(getAutowiredFields(targetClass))
        .build();
  }

  /**
   * @param targetClass object of type {@link Class} that is annotated with @{@link Component}
   *     annotation
   * @return @{@link Component#value()} or {@link Class#getSimpleName()} starting with lowercase
   *     letter
   */
  private String resolveBeanName(Class<?> targetClass) {
    var annotationValue = targetClass.getAnnotation(COMPONENT_ANNOTATION).value();
    return annotationValue.isBlank() ? Helper.resolveBeanName(targetClass) : annotationValue;
  }

  /**
   * @param targetClass object of type {@link Class} that is annotated with @{@link Component}
   *     annotation
   * @return {@link java.util.Set} of {@link String} that represents bean dependencies
   */
  private Set<String> getAutowiredFields(Class<?> targetClass) {
    var set =
        Arrays.stream(targetClass.getDeclaredFields())
            .filter(el -> el.isAnnotationPresent(AUTOWIRED_ANNOTATION))
            .map(el -> resolveBeanName(el.getType()))
            .collect(Collectors.toSet());

    Helper.checkAndGetAutowiredConstructor(targetClass)
        .ifPresent(
            constructor -> {
              var arguments =
                  Arrays.stream(constructor.getParameterTypes())
                      .map(Helper::resolveBeanName)
                      .collect(Collectors.toSet());
              set.addAll(arguments);
            });

    return set;
  }
}
