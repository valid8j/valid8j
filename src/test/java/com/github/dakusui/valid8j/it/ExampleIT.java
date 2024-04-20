package com.github.dakusui.valid8j.it;

import com.github.dakusui.valid8j.pcond.validator.ExceptionComposer;
import org.junit.Test;
import org.opentest4j.AssertionFailedError;

import static com.github.dakusui.valid8j.classic.TestAssertions.assertThat;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.isNull;

public class ExampleIT {
  @Test(expected = AssertionFailedError.class)
  public void useOpentest4j() {
    System.setProperty("com.github.dakusui.valid8j.pcond.exceptionComposerForTestFailures", ExceptionComposer.ForTestAssertion.Opentest4J.class.getName());

    assertThat("hello", isNull());
  }
}
