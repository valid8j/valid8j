package com.github.dakusui.valid8j.examples.test;

import com.github.dakusui.valid8j.examples.fluent.Valid8JExample;
import com.github.dakusui.valid8j.examples.metamor.MetamorExampleFailing;
import com.github.dakusui.valid8j.examples.metamor.MetamorExamplePassing;
import com.github.dakusui.valid8j.examples.thincrest.ThincrestExample;
import com.github.dakusui.valid8j.utils.metatest.Metatest;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import org.junit.Test;

public class ExamplesTest extends TestBase {
  @Test
  public void testClassicExample() {
    Metatest.verifyTestClass(ThincrestExample.class);
  }

  @Test
  public void testFluentExample() {
    Metatest.verifyTestClass(Valid8JExample.class);
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
