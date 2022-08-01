package org.blyznytsia.exception;

public class NoUniqueBeanException extends RuntimeException {

  public NoUniqueBeanException(int size) {
    super(String.format("Required a single bean, but %s were found", size));
  }

  public NoUniqueBeanException(String message) {
    super(message);
  }
}
