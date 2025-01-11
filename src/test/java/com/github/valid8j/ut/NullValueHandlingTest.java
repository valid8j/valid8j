package com.github.valid8j.ut;

import com.github.valid8j.classic.TestAssertions;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.forms.Predicates;
import org.junit.ComparisonFailure;
import org.junit.Test;

/**
 * // @formatter:off 
 * // @formatter:on 
 */
public class NullValueHandlingTest {
  @Test(expected = ComparisonFailure.class)
  public void givenNull_whenStatementIsExamined_thenProcessedCorrectly() {
    Expectations.assertStatement(Expectations.value((String) null).toBe().equalTo("hello"));
  }


  @Test(expected = ComparisonFailure.class)
  public void givenNull_whenStatementIsExamined_thenProcessedCorrectly2() {
    Expectations.assertStatement(Expectations.value((String) null)
                                             .toBe()
                                             .predicate(Predicates.and(Predicates.isNotNull(), Predicates.equalTo("Hello"))));
  }

  @Test(expected = ComparisonFailure.class)
  public void givenNull_whenStatementIsExamined_thenProcessedCorrectly3() {
    TestAssertions.assertThat((String)null, Predicates.and(Predicates.isNotNull(), Predicates.equalTo("Hello")));
  }
}
