package com.github.valid8j.ut.styles;

import com.github.valid8j.pcond.fluent.Statement;
import org.junit.Test;

import java.util.List;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.pcond.forms.Predicates.isNotNull;
import static java.util.Arrays.asList;

/**
 * Temporarily commented out for improving new fluent model.
 */
public class MoreFluentListTest {
  @Test
  public void test_elementAt() {
    List<String> var = asList("hello", "world");
    assertStatement(Statement.listValue(var).elementAt(0).then().equalTo("hello"));
  }

  @Test
  public void test_size() {
    List<String> var = asList("hello", "world");
    assertStatement(Statement.listValue(var).size().then().equalTo(2));
  }

  /*
  @Test
  public void test_subList() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listStatement(var).subList(1).then().isEqualTo(singletonList("world")));
  }

  @Test
  public void test_subList$int$int() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listStatement(var).subList(1, 2).then().isEqualTo(singletonList("world")));
  }
   */
  @Test
  public void test_stream() {
    List<String> var = asList("hello", "world");
    assertStatement(Statement.listValue(var).stream().then().allMatch(isNotNull()));
  }

  @Test
  public void test_isEmpty() {
    List<String> var = asList("hello", "world");
    assertStatement(Statement.listValue(var).isEmpty().then().isFalse());
  }
}
