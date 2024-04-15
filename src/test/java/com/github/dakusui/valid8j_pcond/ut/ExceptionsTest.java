package com.github.dakusui.valid8j_pcond.ut;

import com.github.dakusui.valid8j.pcond.internals.InternalException;
import com.github.dakusui.valid8j.pcond.internals.InternalUtils;
import com.github.dakusui.valid8j_pcond.ut.testdata.IntentionalException;
import org.junit.Test;

public class ExceptionsTest {
  @Test(expected = IntentionalError.class)
  public void testWrapIfNecessary() {
    throw InternalUtils.wrapIfNecessary(new IntentionalError());
  }
  @Test(expected = IntentionalException.class)
  public void testWrapIfNecessaryWithRuntimeException() {
    throw InternalUtils.wrapIfNecessary(new IntentionalException("hi"));
  }

  @Test(expected = InternalException.class)
  public void testWrapIfNecessaryWithCheckedException() {
    throw InternalUtils.wrapIfNecessary(new Exception("hi"));
  }
}
