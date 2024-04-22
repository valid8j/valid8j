package com.github.valid8j.ut.internal;

import com.github.valid8j.utils.testbase.TestBase;
import com.github.valid8j.pcond.internals.InternalChecks;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InternalChecksTest extends TestBase {
  @Test(expected = IllegalArgumentException.class)
  public void testRequireArgument$fails() {
    String message = "value is not zero";
    try {
      InternalChecks.requireArgument(1, i -> i == 0, () -> message);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertEquals(message, e.getMessage());
      throw e;
    }
  }

  @Test
  public void testRequireArgument() {
    String message = "value is zero";
    int ret = InternalChecks.requireArgument(100, (Integer i) -> i != 0, () -> message);
    assertEquals(100, ret);
  }

  @Test(expected = IllegalStateException.class)
  public void testRequireState$fails() {
    String message = "value is not zero";
    try {
      InternalChecks.requireState(1, i -> i == 0, (i) -> message);
    } catch (IllegalStateException e) {
      assertEquals(message, e.getMessage());
      throw e;
    }
  }

  @Test
  public void testRequireState() {
    String message = "value is zero";
    int ret = InternalChecks.requireState(100, (Integer i) -> i != 0, (i) -> message);
    assertEquals(100, ret);
  }
}
