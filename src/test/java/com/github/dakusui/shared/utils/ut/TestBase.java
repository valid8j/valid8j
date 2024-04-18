package com.github.dakusui.shared.utils.ut;

import com.github.dakusui.valid8j.utils.TestUtils;
import org.junit.After;
import org.junit.Before;

/**
 * Created by hiroshi.ukai on 8/26/17.
 */
public class TestBase {
  @Before
  public void before() {
    TestUtils.suppressStdOutErrIfUnderPitestOrSurefire();
  }
  
  @After
  public void after() {
    TestUtils.restoreStdOutErr();
  }
}
