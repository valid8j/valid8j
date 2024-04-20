package com.github.dakusui.valid8j.pcond.internals;

public class MethodNotFound extends InternalException {
  public MethodNotFound(String message, Throwable cause) {
    super(message, cause);
  }

  public MethodNotFound(String message) {
    this(message, null);
  }
}
