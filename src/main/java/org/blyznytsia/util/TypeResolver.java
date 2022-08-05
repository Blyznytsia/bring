package org.blyznytsia.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TypeResolver {

  public Object parseToType(String propertyValue, Class<?> type) {

    if (type == String.class) {
      return propertyValue;
    } else if (type == int.class || type == Integer.class) {
      return Integer.parseInt(propertyValue);
    } else if (type == long.class || type == Long.class) {
      return Long.parseLong(propertyValue);
    } else if (type == double.class || type == Double.class) {
      return Double.parseDouble(propertyValue);
    }

    throw new ClassCastException(
        String.format("Can't cast value \"%s\" to %s class", propertyValue, type.getSimpleName()));
  }
}
