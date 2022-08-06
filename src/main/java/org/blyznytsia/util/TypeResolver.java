package org.blyznytsia.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TypeResolver {

  public Object parseToType(String value, Class<?> type) {
    if (type == String.class) {
      return value;
    } else if (type == int.class || type == Integer.class) {
      return Integer.parseInt(value);
    } else if (type == long.class || type == Long.class) {
      return Long.parseLong(value);
    } else if (type == double.class || type == Double.class) {
      return Double.parseDouble(value);
    }

    throw new ClassCastException(
        "Failed to cast %s value to %s type".formatted(value, type.getSimpleName()));
  }
}
