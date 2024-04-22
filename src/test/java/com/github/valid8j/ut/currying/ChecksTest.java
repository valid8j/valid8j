package com.github.valid8j.ut.currying;

import org.junit.Test;

import static com.github.valid8j.pcond.internals.InternalChecks.isWiderThan;
import static com.github.valid8j.pcond.internals.InternalChecks.isWiderThanOrEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChecksTest {
  @Test
  public void givenWider$whenIsWiderThan() {
    assertTrue(isWiderThan(Long.class, Integer.class));
  }

  @Test
  public void givenNarrower$whenIsWiderThanTest2() {
    assertFalse(isWiderThan(Short.class, Long.class));
  }


  @Test(expected = IllegalArgumentException.class)
  public void givenNonWrapperForFirstArgument$whenIsWiderThan$thenIllegalArgument() {
    assertFalse(isWiderThan(Object.class, Long.class));
  }


  @Test(expected = IllegalArgumentException.class)
  public void givenNonWrapperForSecondArgument$whenIsWiderThan$thenIllegalArgument() {
    assertFalse(isWiderThan(Short.class, Object.class));
  }


  @Test
  public void givenWider$whenIsWiderThanOrEqualToThenTrue() {
    assertTrue(isWiderThanOrEqualTo(Long.class, Integer.class));
  }

  @Test
  public void givenEqual$whenIsWiderThanOrEqualToThenTrue() {
    assertTrue(isWiderThanOrEqualTo(Long.class, Long.class));
  }

  @Test
  public void givenNarrower$whenIsWiderThanOrEqualToThenTrue() {
    assertFalse(isWiderThanOrEqualTo(Short.class, Long.class));
  }


  @Test(expected = IllegalArgumentException.class)
  public void givenNonWrapperForFirstArgument$whenIsWiderThanOrEqualTo$thenIllegalArgument() {
    assertFalse(isWiderThanOrEqualTo(Object.class, Long.class));
  }


  @Test(expected = IllegalArgumentException.class)
  public void givenNonWrapperForSecondArgument$whenIsWiderThanOrEqualTo$thenIllegalArgument() {
    assertFalse(isWiderThanOrEqualTo(Short.class, Object.class));
  }
}
