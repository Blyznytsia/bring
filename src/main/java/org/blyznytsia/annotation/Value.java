package org.blyznytsia.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Fields can be marked by annotation @Value. It indicates that values from application.properties
 * can be inserted into those fields
 *
 * <p>For example application.properties file contain pair: host=http://localhost
 *
 * <pre class="code">
 *     @Values("host")
 *     String hostVal;
 * </pre>
 *
 * After bean was created the "hostVal" will contain "http://localhost"
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
  String value() default "";
}
