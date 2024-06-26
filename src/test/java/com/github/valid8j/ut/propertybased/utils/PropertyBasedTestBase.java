package com.github.valid8j.ut.propertybased.utils;

import com.github.valid8j.utils.testbase.TestBase;
import org.junit.Test;

import static java.util.Objects.requireNonNull;

public abstract class PropertyBasedTestBase extends TestBase {
  private final TestCase<?, ?> testCase;

  public PropertyBasedTestBase(@SuppressWarnings("unused") String testName, TestCase<?, ?> testCase) {
    this.testCase = requireNonNull(testCase);
  }

  @SuppressWarnings("unused")
  @Test
  public void exerciseTestCase() throws Throwable {
    TestCaseUtils.exerciseTestCase(testCase);
  }
}
