package com.github.dakusui.valid8j.examples.thincrest.sut;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
