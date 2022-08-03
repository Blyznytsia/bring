package org.blyznytsia.util;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.exception.NotImplementedException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Helper {

  public static String resolveBeanName(Class<?> targetClass) {
    return targetClass.getSimpleName().substring(0, 1).toLowerCase()
        + targetClass.getSimpleName().substring(1);
  }

  public static Optional<Constructor<?>> checkAndGetAutowiredConstructor(Class<?> targetClass)
      throws NotImplementedException {
    var constructors =
        Arrays.stream(targetClass.getDeclaredConstructors())
            .filter(el -> el.isAnnotationPresent(Autowired.class))
            .toList();

    if (constructors.size() > 1) {
      var msg =
          String.format(
              "Ambiguous constructor while autowiring in %s. Requires single annotated constructor, but found %d.",
              targetClass.getName(), constructors.size());
      throw new NotImplementedException(msg);
    }

    return constructors.stream().findFirst();
  }
}
