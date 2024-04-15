package com.github.dakusui.valid8j.examples;

import com.github.dakusui.valid8j.metamor.MetamorExampleFailing;
import com.github.dakusui.valid8j.metamor.MetamorExamplePassing;
import com.github.dakusui.valid8j.utils.metatest.Metatest;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import org.junit.Test;

public class ThincrestExamplesTest extends TestBase {
  @Test
  public void testThincrestExample() {
    Metatest.verifyTestClass(ThincrestExample.class);
  }

  @Test
  public void testMetarmorExamplePassing() {
    Metatest.verifyTestClass(MetamorExamplePassing.class);
  }

  @Test
  public void testMetarmorExampleFailing() {
    Metatest.verifyTestClass(MetamorExampleFailing.class);
  }
}
