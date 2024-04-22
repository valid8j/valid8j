package com.github.valid8j.examples.sut;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
