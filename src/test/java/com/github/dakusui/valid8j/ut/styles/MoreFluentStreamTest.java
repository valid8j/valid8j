package com.github.dakusui.valid8j.ut.styles;

import com.github.dakusui.valid8j.pcond.fluent.Statement;
import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.valid8j.fluent.Expectations.assertStatement;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.isEqualTo;
import static com.github.dakusui.valid8j.pcond.forms.Predicates.isNotNull;

public class MoreFluentStreamTest {
  @Test
  public void test_noneMatche() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(Statement.streamValue(var).then().noneMatch(isEqualTo("HELLO")));
  }

  @Test
  public void test_anyMatch() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(Statement.streamValue(var).then().anyMatch(isEqualTo("world")));
  }

  @Test
  public void test_allMatch() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(Statement.streamValue(var).then().allMatch(isNotNull()));
  }
}
