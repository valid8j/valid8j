package com.github.dakusui.valid8j.metamor;

public enum Utils {
  ;

  public static void requireState(boolean c, String message) {
    if (!c)
      throw new IllegalStateException(message);
  }
  public static void requireArgument(boolean c, String message) {
    if (!c)
      throw new IllegalArgumentException(message);
  }
}
