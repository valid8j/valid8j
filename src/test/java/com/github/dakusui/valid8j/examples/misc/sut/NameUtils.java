package com.github.dakusui.valid8j.examples.misc.sut;

import static com.github.dakusui.valid8j.classic.Assertions.precondition;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.containsString;

public enum NameUtils {
  ;

  public static String firstNameOf(String yourName) {
    assert precondition(yourName, containsString(" ")); // <4>
    return yourName.substring(0, yourName.indexOf(' '));
  }
}
