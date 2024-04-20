package com.github.dakusui.valid8j.ut.typesupports;

import com.github.dakusui.valid8j.pcond.forms.Predicates;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ObjectTest {
  @Test
  public void givenNull() {
    assertFalse(Predicates.isInstanceOf(String.class).test(null));
  }
}
