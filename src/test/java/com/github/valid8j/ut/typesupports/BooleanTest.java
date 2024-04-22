package com.github.valid8j.ut.typesupports;

import org.junit.Test;

import static com.github.valid8j.classic.Requires.requireArgument;
import static com.github.valid8j.pcond.forms.Predicates.*;

public class BooleanTest {
  @Test(expected = IllegalArgumentException.class)
  public void testIsTrue() {
    boolean var = false;
    requireArgument(var, isTrue());
    requireArgument(var, and(isFalse(), isTrue()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrapperIsTrue() {
    Boolean var = false;
    requireArgument(var, isTrue());
    requireArgument(var, and(isFalse(), isTrue()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsFalse() {
    boolean var = true;
    requireArgument(var, isFalse());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrapperIsFalse() {
    Boolean var = true;
    requireArgument(var, isFalse());
  }
}
