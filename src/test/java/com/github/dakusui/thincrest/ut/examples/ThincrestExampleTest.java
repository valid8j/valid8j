package com.github.dakusui.thincrest.ut.examples;

import com.github.dakusui.thincrest.examples.ThincrestExample;
import com.github.dakusui.thincrest.utils.metatest.Metatest;
import com.github.dakusui.thincrest.utils.testbase.TestBase;
import org.junit.Test;

public class ThincrestExampleTest extends TestBase {
  @Test
  public void testThincrestExample() {
    Metatest.verifyTestClass(ThincrestExample.class);
  }
}
