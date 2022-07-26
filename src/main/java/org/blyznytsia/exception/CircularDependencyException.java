package org.blyznytsia.exception;

public class CircularDependencyException extends RuntimeException {

    private static final String EXCEPTION_DESCRIPTION = "The dependencies of some of the beans in the application context form a cycle:\n";

    public CircularDependencyException(String message) {
        super(EXCEPTION_DESCRIPTION + message);
    }

}
