package com.github.valid8j.ut.testdata;

public class FailingConstructor {
  public FailingConstructor() {
    throw new IntentionalException("Hello!");
  }
}
