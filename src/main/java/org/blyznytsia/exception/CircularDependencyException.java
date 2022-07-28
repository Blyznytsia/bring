package org.blyznytsia.exception;

public class CircularDependencyException extends RuntimeException {

  private static final String EXCEPTION_DESCRIPTION =
      "There is a circular dependency between beans in the application context:\n";

  public CircularDependencyException(String message) {
    super(EXCEPTION_DESCRIPTION + message);
  }
}
