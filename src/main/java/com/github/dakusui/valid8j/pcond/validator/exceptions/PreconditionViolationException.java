package com.github.dakusui.valid8j.pcond.validator.exceptions;

/**
 * An exception intended to be used, when a pre-condition is not satisfied.
 */
public class PreconditionViolationException extends RuntimeException {
  public PreconditionViolationException(String message) {
    super(message);
  }
}
