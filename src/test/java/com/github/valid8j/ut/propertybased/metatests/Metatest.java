package com.github.valid8j.ut.propertybased.metatests;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.ut.propertybased.utils.TestCase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.IOException;

import static com.github.valid8j.ut.propertybased.utils.TestCaseUtils.exerciseTestCase;

public class Metatest {
  @Test
  public void whenExpectedValueIsReturned_thenPasses() throws Throwable {
    exerciseTestCase(new TestCase.Builder.ForReturnedValue<>(null, Predicates.isNull(), Object.class).build());
  }

  @Test(expected = ComparisonFailure.class)
  public void whenUnexpectedValueIsReturned_thenComparisonFailureIsThrown() throws Throwable {
    exerciseTestCase(new TestCase.Builder.ForReturnedValue<>("notNull", Predicates.isNull(), Object.class).build());
  }


  @Test(expected = AssertionError.class)
  public void whenUnexpectedExceptionIsThrown_thenAssertionErrorIsThrown() throws Throwable {
    exerciseTestCase(
        new TestCase.Builder.ForThrownException<String, IOException>("")
            .predicate(Predicates.isNull())
            .expectedExceptionClass(IOException.class)
            .build());
  }

  @Test
  public void whenExpectedExceptionIsThrown_thenPasses() throws Throwable {
    exerciseTestCase(
        new TestCase.Builder.ForThrownException<String, ComparisonFailure>("")
            .predicate(Predicates.isNull())
            .expectedExceptionClass(ComparisonFailure.class)
            .build());
  }
}
