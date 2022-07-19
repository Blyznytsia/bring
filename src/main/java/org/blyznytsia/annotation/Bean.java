package org.blyznytsia.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method produces a bean to be managed by the Bring container.
 *
 * <p>For example: </p>
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
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

    /**
     * Name of a bean
     */
    String value() default "";
}
