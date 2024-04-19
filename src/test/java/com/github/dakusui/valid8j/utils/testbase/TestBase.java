package com.github.dakusui.valid8j.utils.testbase;

import com.github.dakusui.valid8j.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.assertFailsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

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

  public static class ForAssertionEnabledVM extends TestBase {
    @BeforeClass
    public static void setUpBeforeAll() {
      assumeThat(assertFailsWith(false), is(true));
    }
  }
}
