package com.github.dakusui.valid8j_pcond.types;

import com.github.dakusui.valid8j.utils.exceptions.IllegalValueException;
import com.github.dakusui.valid8j.utils.testbase.TestBase;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.valid8j.utils.Validates.validate;
import static com.github.dakusui.valid8j.pcond.fluent.Statement.streamValue;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.containsString;

public class StreamTest extends TestBase {
  @Test
  public void streamTest() {
    Stream<String> value = Stream.of("Hello", "world");
    validate(value, streamValue(value).then().anyMatch(Predicates.isEqualTo("world")).statementPredicate());
  }

  @Test(expected = IllegalValueException.class)
  public void streamTestFailure() {
    Stream<String> value = Stream.of("Hello", "world");
    try {
      validate(value, streamValue(value).then().anyMatch(Predicates.isEqualTo("World")).statementPredicate());
    } catch (IllegalValueException e) {
      e.printStackTrace();
      //MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("anyMatch[isEqualTo[\"World\"]]->true"));
      //MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("anyMatch[isEqualTo[\"World\"]]->false"));
      validate(e.getMessage(), containsString("anyMatch[isEqualTo[\"World\"]]->true"));
      validate(e.getMessage(), containsString("anyMatch[isEqualTo[\"World\"]]->false"));
      throw e;
    }
  }

  @Test
  public void streamTransformerTest() {
    Stream<String> value = Stream.of("Hello", "world");
    validate(value, streamValue(value).then().anyMatch(Predicates.isEqualTo("world")).toPredicate());
  }

  @Test(expected = IllegalValueException.class)
  public void streamTransformerTestFailure() {
    Stream<String> value = Stream.of("Hello", "world");
    validate(value, streamValue(value).then().anyMatch(Predicates.isEqualTo("World")).toPredicate());
  }
}
