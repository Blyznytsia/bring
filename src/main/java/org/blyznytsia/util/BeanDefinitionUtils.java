package org.blyznytsia.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.exception.NoDefaultConstructorException;

@UtilityClass
public class BeanDefinitionUtils {

  public String resolveBeanName(Class<?> targetClass) {
    return targetClass.getSimpleName().substring(0, 1).toLowerCase()
        + targetClass.getSimpleName().substring(1);
  }

  public Constructor<?> findConstructor(Class<?> type) {
    Constructor<?> defaultConstructor = null;
    Constructor<?> annotatedConstructor = null;

    for (var constructor : type.getDeclaredConstructors()) {

      if (constructor.getParameterCount() == 0) {
        defaultConstructor = constructor;
      }

      if (constructor.isAnnotationPresent(Autowired.class)) {
        if (annotatedConstructor != null) {
          throw new RuntimeException(
              "Requires single constructor annotated with @Autowired in %s"
                  .formatted(type.getSimpleName()));
        }

        annotatedConstructor = constructor;
      }
    }

    if (annotatedConstructor == null && defaultConstructor == null) {
      throw new NoDefaultConstructorException(
          "No default constructor found for type %s".formatted(type));
    }

    return annotatedConstructor != null ? annotatedConstructor : defaultConstructor;
  }

  public Set<String> findRequiredDependencies(Executable executable) {
    return Arrays.stream(executable.getParameterTypes())
        .map(BeanDefinitionUtils::resolveBeanName)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
