package com.github.valid8j.examples.metamor;

import com.github.valid8j.utils.metatest.TestClassExpectation;
import com.github.valid8j.utils.metatest.TestClassExpectation.EnsureJUnitResult;
import com.github.valid8j.utils.metatest.TestClassExpectation.ResultPredicateFactory.*;
import com.github.valid8j.utils.metatest.TestMethodExpectation;
import org.junit.Test;

import static com.github.valid8j.utils.metatest.TestMethodExpectation.Result.FAILURE;

@TestClassExpectation(value = {
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "13"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "13")
})
public class MetamorExampleFailing extends MetamorExampleBase{
  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest4a() {
    super.testMetamorphicTest4a();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1a() {
    super.testMetamorphicTest1a();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1b() {
    super.testMetamorphicTest1b();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1c() {
    super.testMetamorphicTest1c();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1d() {
    super.testMetamorphicTest1d();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1e() {
    super.testMetamorphicTest1e();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1f() {
    super.testMetamorphicTest1f();
  }


  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1g() {
    super.testMetamorphicTest1g();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1h() {
    super.testMetamorphicTest1h();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1h1() {
    super.testMetamorphicTest1h1();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest1i() {
    super.testMetamorphicTest1i();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest2a() {
    super.testMetamorphicTest2a();
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void testMetamorphicTest3a() {
    super.testMetamorphicTest3a();
  }

  public double acceptableError() {
    return 0.000;
  }
}
