package com.github.valid8j.examples.metamor;

import com.github.valid8j.utils.metatest.TestClassExpectation;
import com.github.valid8j.utils.metatest.TestClassExpectation.EnsureJUnitResult;
import com.github.valid8j.utils.metatest.TestClassExpectation.ResultPredicateFactory.*;

@TestClassExpectation(value = {
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "13"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "0")
})
public class MetamorExamplePassing extends MetamorExampleBase {
  public double acceptableError() {
    return 0.001;
  }
}
