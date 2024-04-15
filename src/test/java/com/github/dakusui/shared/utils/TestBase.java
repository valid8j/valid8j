package com.github.dakusui.shared.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.assertFailsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

public abstract class TestBase {
  @Before
  public void before() {
    TestUtils.suppressStdOutErrIfUnderPitestOrSurefire();
  }

  @After
  public void after() {
    TestUtils.restoreStdOutErr();
  }

  public static class ForAssertionEnabledVM extends com.github.dakusui.shared.utils.ut.TestBase {
    @BeforeClass
    public static void setUpBeforeAll() {
      assumeThat(assertFailsWith(false), is(true));
    }
  }
}
