package org.blyznytsia.context.data;

import java.io.InputStream;

public interface Convertor {

  default String convert(InputStream is) {
    return "";
  }
}
