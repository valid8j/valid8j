package com.github.dakusui.valid8j.examples;

import java.util.function.IntPredicate;

import static com.github.dakusui.valid8j.Assertions.precondition;
import static com.github.dakusui.valid8j.Requires.require;
import static com.github.dakusui.valid8j_pcond.forms.Functions.length;
import static com.github.dakusui.valid8j_pcond.forms.Predicates.*;

public class Example {
  public void publicMethod(String message) {
    require(message, and(isNotNull(), transform(length()).check(greaterThan(0))));
    privateMethod(message);
  }

  private void privateMethod(String message) {
    assert precondition(message, and(isNotNull(), transform(length()).check(greaterThan(0))));
    System.out.println(message);
  }


  public static void main(String... args) {
    ///new Example().publicMethod(null);
    System.out.println((IntPredicate)(int x) -> 4 < x && x < 15);
  }
}