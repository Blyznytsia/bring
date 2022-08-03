package org.blyznytsia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Helper {

  public static String resolveBeanName(Class<?> targetClass) {
    return targetClass.getSimpleName().substring(0, 1).toLowerCase()
        + targetClass.getSimpleName().substring(1);
  }
}
