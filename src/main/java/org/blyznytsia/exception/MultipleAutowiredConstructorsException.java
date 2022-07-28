package org.blyznytsia.exception;

public class MultipleAutowiredConstructorsException extends RuntimeException {

  public MultipleAutowiredConstructorsException(String message) {
    super(message);
  }
}
