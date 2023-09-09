package com.github.dakusui.valid8j.ut.examples;

import com.github.dakusui.valid8j.examples.Valid8JExample;
import com.github.dakusui.valid8j.utils.metatest.Metatest;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import org.junit.Test;

public class Valid8jExampleTest extends TestBase {
  @Test
  public void testValid8jExample() {
    Metatest.verifyTestClass(Valid8JExample.class);
  }
}
