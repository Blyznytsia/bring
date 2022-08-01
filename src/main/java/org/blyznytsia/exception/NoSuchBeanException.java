package org.blyznytsia.exception;

public class NoSuchBeanException extends RuntimeException {

  public NoSuchBeanException() {
    super("Required a single bean, but 0 were found");
  }

  public NoSuchBeanException(String message) {
    super(message);
  }
}
