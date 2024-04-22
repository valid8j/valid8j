package com.github.valid8j.pcond.core.refl;

public interface Parameter {
  Parameter INSTANCE = create();

  static Parameter create() {
    return new Parameter() {
      @Override
      public String toString() {
        return "";
      }
    };
  }
}
