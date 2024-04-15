package com.github.dakusui.valid8j_pcond.ut.testdata;

public class FailingConstructor {
  public FailingConstructor() {
    throw new IntentionalException("Hello!");
  }
}
