package org.blyznytsia.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Fields can be marked by annotation @Values. It indicates that a value from application.properties
 * can be inserted into that fields
 *
 * <p>For example application.properties file contain pair:
 *   host=http://localhost
 *
 * <pre class="code">
 *     @Values("host")
 *     String hostVal;
 * </pre>
 *
 * After bean was created the "hostVal" will contain "http://localhost"
 *
 * @see org.blyznytsia.annotation.Autowired
 * @see org.blyznytsia.annotation.Component
 * @see org.blyznytsia.annotation.Configuration
 * @see org.blyznytsia.annotation.Values
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Values {
    String value();
}
