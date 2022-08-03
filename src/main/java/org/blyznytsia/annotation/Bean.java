package org.blyznytsia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method produces a bean to be managed by the Bring container.
 *
 * <p>For example:
 *
 * <pre class="code">
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // instantiate and configure MyBean obj
 *         return obj;
 *     }
 * </pre>
 *
 * @see org.blyznytsia.annotation.Autowired
 * @see org.blyznytsia.annotation.Component
 * @see org.blyznytsia.annotation.Configuration
 * @see org.blyznytsia.annotation.Values
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

  /** Name of a bean */
  String value() default "";

  /**
   * The optional name of a method to call on the bean instance during initialization. Not commonly
   * used, given that the method may be called programmatically directly within the body of a
   * Bean-annotated method.
   */
  String initMethod() default "";
}
