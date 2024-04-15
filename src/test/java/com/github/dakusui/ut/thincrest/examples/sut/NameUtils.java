package com.github.dakusui.ut.thincrest.examples.sut;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
