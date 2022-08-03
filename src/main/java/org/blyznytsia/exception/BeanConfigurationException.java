package org.blyznytsia.exception;

import org.blyznytsia.context.ApplicationContext;

/**
 * Exception thrown when the validation of a bean definition failed.
 *
 * @see org.blyznytsia.bpp.BeanPostProcessor#configure(Object, ApplicationContext)
 */
public class BeanConfigurationException extends RuntimeException {

  public BeanConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
