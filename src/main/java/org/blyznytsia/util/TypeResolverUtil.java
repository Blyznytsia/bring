package org.blyznytsia.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TypeResolverUtil {

  public static Object parseToType(String propertyValue, Class<?> type)
      throws NumberFormatException {

    if (type == String.class) {
      return propertyValue;
    } else if (type == int.class || type == Integer.class) {
      return Integer.parseInt(propertyValue);
    } else if (type == long.class || type == Long.class) {
      return Long.parseLong(propertyValue);
    } else if (type == double.class || type == Double.class) {
      return Double.parseDouble(propertyValue);
    } else if (type == float.class || type == Float.class) {
      return Float.parseFloat(propertyValue);
    } else if (type == byte.class || type == Byte.class) {
      return Byte.parseByte(propertyValue);
    } else if (type == short.class || type == Short.class) {
      return Short.parseShort(propertyValue);
    } else if (type == BigInteger.class) {
      return new BigInteger(propertyValue);
    } else if (type == BigDecimal.class) {
      return new BigDecimal(propertyValue);
    }

    throw new ClassCastException(
        String.format("Can't cast value \"%s\" to %s class", propertyValue, type.getSimpleName()));
  }
}
