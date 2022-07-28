package org.blyznytsia.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a constructor or field as to be autowired by Bring's dependency injection facilities.
 *
 * @see org.blyznytsia.annotation.Component
 * @see org.blyznytsia.annotation.Bean
 * @see org.blyznytsia.annotation.Configuration
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {}
