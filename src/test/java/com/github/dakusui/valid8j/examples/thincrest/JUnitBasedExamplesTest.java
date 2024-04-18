package com.github.dakusui.valid8j.examples.thincrest;

import com.github.dakusui.shared.utils.Metatest;
import com.github.dakusui.shared.utils.TestBase;
import org.junit.Test;

public class JUnitBasedExamplesTest extends TestBase {
  @Test
  public void testExampleUT() {
    Metatest.verifyTestClass(UTExample.class);
  }
}
