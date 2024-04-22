package com.github.valid8j.examples.test;

import com.github.valid8j.examples.classic.UTExample;
import com.github.valid8j.utils.metatest.Metatest;
import com.github.valid8j.utils.testbase.TestBase;
import org.junit.Test;

public class JUnitBasedExamplesTest extends TestBase {
  @Test
  public void testExampleUT() {
    Metatest.verifyTestClass(UTExample.class);
  }
}
