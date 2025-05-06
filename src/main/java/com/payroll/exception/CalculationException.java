package com.payroll.exception;

/**
 * Exception thrown during calculation errors.
 */
public class CalculationException extends RuntimeException {

    public CalculationException(String message) {
        super(message);
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
