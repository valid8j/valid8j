package com.github.dakusui.valid8j.utils.testbase;

import com.github.dakusui.valid8j.utils.TestUtils;
import org.junit.After;
import org.junit.Before;

public abstract class TestBase {
  @Before
  public void before() {
    TestUtils.suppressStdOutErrIfUnderPitestOrSurefire();
  }

  @After
  public void after() {
    TestUtils.restoreStdOutErr();
  }
}
