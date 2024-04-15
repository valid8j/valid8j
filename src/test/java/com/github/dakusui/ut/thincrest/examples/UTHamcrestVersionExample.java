package com.github.dakusui.ut.thincrest.examples;

import com.github.dakusui.shared.utils.TestClassExpectation;
import com.github.dakusui.shared.utils.TestClassExpectation.EnsureJUnitResult;
import com.github.dakusui.shared.utils.TestClassExpectation.ResultPredicateFactory.*;
import com.github.dakusui.shared.utils.TestMethodExpectation;
import com.github.dakusui.ut.thincrest.examples.sut.NameUtils;
import org.junit.Test;

import static com.github.dakusui.shared.utils.TestMethodExpectation.Result.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeThat;

@TestClassExpectation({
    @EnsureJUnitResult(type = WasNotSuccessful.class, args = {}),
    @EnsureJUnitResult(type = RunCountIsEqualTo.class, args = "3"),
    @EnsureJUnitResult(type = IgnoreCountIsEqualTo.class, args = "0"),
    @EnsureJUnitResult(type = AssumptionFailureCountIsEqualTo.class, args = "1"),
    @EnsureJUnitResult(type = SizeOfFailuresIsEqualTo.class, args = "1")
})
public class UTHamcrestVersionExample {
  @TestMethodExpectation(PASSING)
  @Test
  public void shouldPass_testFirstNameOf() {
    String firstName = NameUtils.firstNameOf("Risa Kitajima");
    assertThat(firstName, allOf(not(containsString(" ")), startsWith("R")));
  }

  @TestMethodExpectation(FAILURE)
  @Test
  public void shouldFail_testFirstNameOf() {
    String firstName = NameUtils.firstNameOf("Yoshihiko Naito");
    assertThat(firstName, allOf(not(containsString(" ")), startsWith("N")));
  }

  @TestMethodExpectation(ASSUMPTION_FAILURE)
  @Test
  public void shouldBeIgnored_testFirstNameOf() {
    String firstName = NameUtils.firstNameOf("Yoshihiko Naito");
    assumeThat(firstName, allOf(not(containsString(" ")), startsWith("N")));
  }
}
