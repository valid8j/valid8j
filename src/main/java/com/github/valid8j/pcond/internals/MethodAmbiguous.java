package com.github.valid8j.pcond.internals;

public class MethodAmbiguous extends InternalException {
  public MethodAmbiguous(String message, Throwable cause) {
    super(message, cause);
  }

  public MethodAmbiguous(String message) {
    this(message, null);
  }
}
