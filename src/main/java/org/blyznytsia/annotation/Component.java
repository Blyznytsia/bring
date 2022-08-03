package org.blyznytsia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "component". Such classes are considered as candidates for
 * auto-detection when using annotation-based configuration and classpath scanning.
 *
 * <p>For example:
 *
 * <pre class="code">
 * &#064;Component
 * public class SomeClass  {
 *      &#064;Autowired
 *      private SomeService service;
 * }
 * </pre>
 *
 * @see org.blyznytsia.annotation.Autowired
 * @see org.blyznytsia.annotation.Bean
 * @see org.blyznytsia.annotation.Configuration
 * @see org.blyznytsia.annotation.Values
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

  String value() default "";
}
