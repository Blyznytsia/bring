package org.blyznytsia.exception;

public class NoDefaultConstructorException extends RuntimeException {

  public NoDefaultConstructorException(String message) {
    super(message);
  }
}
