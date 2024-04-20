package com.github.dakusui.valid8j.utils.testbase;

import com.github.dakusui.valid8j.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.assertFailsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

/**
 * A base class for tests.
 * If you extend this class,  it suppresses all output to `stdout` and `stderr` during tests when the class is run under maven.
 * When the class is run from your IDE, the messages you write by `System.out.println` will be shown in output, for instance.
 *
 * Once a test fails, you may sometimes want to know how your test and SUT work through their output without looking into log files or through debugger.
 * This class is useful for that.
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
