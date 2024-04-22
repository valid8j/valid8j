package com.github.valid8j.ut.styles;

import com.github.valid8j.pcond.fluent.Statement;
import org.junit.Test;

import static com.github.valid8j.fluent.Expectations.assertStatement;


public class MoreFluentComparableNumberCheckerTest {
  @Test
  public void test_equalTo() {
    double var = 1.0d;
    assertStatement(Statement.doubleValue(var).then().equalTo(1.0d));
  }

  @Test
  public void test_lessThan() {
    double var = 1.0d;
    assertStatement(Statement.doubleValue(var).then().lessThan(2.0d));
  }

  @Test
  public void test_lessThanOrEqualTo() {
    double var = 1.0d;
    assertStatement(Statement.doubleValue(var).then().lessThanOrEqualTo(2.0d));
  }

  @Test
  public void test_greaterThan() {
    double var = 1.0d;
    assertStatement(Statement.doubleValue(var).then().greaterThan(0.0d));
  }

  @Test
  public void test_greaterThanOrEqualTo() {
    double var = 1.0d;
    assertStatement(Statement.doubleValue(var).then().greaterThanOrEqualTo(0.0d));
  }

  @Test
  public void test_Integer() {
    int var = 1;
    assertStatement(Statement.integerValue(var).then().equalTo(1));
  }

  @Test
  public void test_Long() {
    long var = 1;
    assertStatement(Statement.longValue(var).then().equalTo(1L));
  }

  @Test
  public void test_Short() {
    short var = 1;
    assertStatement(Statement.shortValue(var).then().equalTo((short) 1));
  }

  @Test
  public void test_Float() {
    float var = 1.0f;
    assertStatement(Statement.floatValue(var).then().equalTo(1.0f));
  }

  @Test
  public void test_Double() {
    double var = 1.0;
    assertStatement(Statement.doubleValue(var).then().equalTo(1.0));
  }
}
