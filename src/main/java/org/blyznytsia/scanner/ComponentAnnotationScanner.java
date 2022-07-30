package org.blyznytsia.scanner;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Qualifier;
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
  private final Map<? extends Class<?>, BeanDefinition> configDefinitionMap;
  private final String[] packages;

  private final Reflections reflections;

  public ComponentAnnotationScanner(
      Map<? extends Class<?>, BeanDefinition> configDefinitionMap, String... packages) {
    this.reflections = new Reflections((Object[]) packages);
    this.configDefinitionMap = configDefinitionMap;
    this.packages = packages;
  }

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
   * @return {@link java.util.List} of {@link BeanDefinition}
   */
  @Override
  public List<BeanDefinition> scan() {
    log.info("Scanning '{}' package for classes annotated wth @Component", packages);

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
    if (targetClass.isAnnotationPresent(COMPONENT_ANNOTATION)) {
      String name = resolveBeanName(targetClass);
      Constructor<?> constructor = findConstructor(targetClass, name);

      var constructorDependencies = getConstructorDependencies(constructor);
      var fieldDependencies = getAutowiredFieldDependencies(targetClass);

      return BeanDefinition.builder()
          .type(targetClass)
          .name(name)
          .qualifiedName(extractQualifiedName(targetClass))
          .constructor(constructor)
          .requiredDependencies(constructorDependencies)
          .fieldDependencies(fieldDependencies)
          .build();
      //      to resolve bean definition dependencies coming from @Configuration classes
    } else if (configDefinitionMap.containsKey(targetClass)) {
      return configDefinitionMap.get(targetClass);
    } else {
      throw new RuntimeException(
          "%s type isn't annotated with @Component and isn't declared in @Configuration class"
              .formatted(targetClass.getSimpleName()));
    }
  }

  private String extractQualifiedName(AnnotatedElement annotatedElement) {
    return Optional.ofNullable(annotatedElement.getAnnotation(Qualifier.class))
        .map(Qualifier::value)
        .orElse("");
  }

  private Class<?> resolveInterfaceType(Class<?> type, String qualifiedName) {
    if (type.isInterface()) {
      Set<Class<?>> impls = reflections.getSubTypesOf((Class<Object>) type);

      if (impls.size() > 1) {
        boolean haveQualifier =
            impls.stream().allMatch(impl -> impl.isAnnotationPresent(Qualifier.class));
        if (!haveQualifier) {
          throw new RuntimeException(
              "Found multiple implementations %s for %s with no @Qualifier set"
                  .formatted(impls, type));
        }
      }

      return impls.stream()
          .filter(impl -> extractQualifiedName(impl).equals(qualifiedName))
          .findFirst()
          .orElseThrow(
              () ->
                  new RuntimeException(
                      "No implementations %s with matching @Qualifier(%s) found for %s"
                          .formatted(impls, qualifiedName, type)));
    }

    return type;
  }

  private List<BeanDefinition> getAutowiredFieldDependencies(Class<?> targetClass) {
    List<BeanDefinition> definitions = new ArrayList<>();

    for (var field : targetClass.getDeclaredFields()) {
      if (field.isAnnotationPresent(AUTOWIRED_ANNOTATION)) {

        Class<?> fieldType = field.getType();
        if (List.class.isAssignableFrom(fieldType)
            && field.getGenericType() instanceof ParameterizedType parameterizedType) {
          createDefinitionsForParameterizedList(definitions, parameterizedType);
        } else {
          Class<?> implClass = resolveInterfaceType(fieldType, extractQualifiedName(field));
          definitions.add(createBeanDefinition(implClass));
        }
      }
    }
    return definitions;
  }

  private void createDefinitionsForParameterizedList(List<BeanDefinition> definitions, ParameterizedType parameterizedType) {
    Class<?> actualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
    Set<Class<?>> impls = reflections.getSubTypesOf((Class<Object>) actualTypeArgument);
    impls.stream().map(this::createBeanDefinition).forEach(definitions::add);
  }

  /**
   * @param targetClass object of type {@link Class} that is annotated with @{@link Component}
   *     annotation
   * @return @{@link Component#value()} or {@link Class#getSimpleName()} starting with lowercase
   *     letter
   */
  private String resolveBeanName(Class<?> targetClass) {
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
    List<BeanDefinition> definitions = new ArrayList<>();

    for (var param : constructor.getParameters()) {
      Class<?> paramType = param.getType();

      if (List.class.isAssignableFrom(paramType)
          && param.getParameterizedType() instanceof ParameterizedType parameterizedType) {
        createDefinitionsForParameterizedList(definitions, parameterizedType);
      }else {
        Class<?> implClass = resolveInterfaceType(paramType, extractQualifiedName(param));
        BeanDefinition definition = createBeanDefinition(implClass);
        definitions.add(definition);
      }

    }
    return definitions;
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
