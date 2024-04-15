package com.github.dakusui.ut.thincrest.examples;

import com.github.dakusui.shared.utils.Metatest;
import com.github.dakusui.shared.utils.TestBase;
import org.junit.Test;

public class JUnitBasedExamplesTest extends TestBase {
  @Test
  public void testExampleUT() {
    Metatest.verifyTestClass(UTExample.class);
  }
}
