package org.blyznytsia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Default proxy annotation works with {@link org.blyznytsia.bpp.ProxyMethodBeanProcessor
 * ProxyMethodBeanProcessor}
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyMethod {}
