package com.github.valid8j.pcond.validator.exceptions;

/**
 * A default exception intended to be used, when a user-defined requirement is not satisfied.
 */
public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}
