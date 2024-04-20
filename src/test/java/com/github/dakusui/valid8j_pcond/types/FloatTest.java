package com.github.dakusui.valid8j_pcond.types;

import com.github.dakusui.valid8j.classic.IllegalValueException;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import org.junit.Test;

import static com.github.dakusui.valid8j.classic.Validates.validateStatement;

public class FloatTest {
  @Test
  public void floatTest() {
    float v = 1.23f;
    validateStatement(Statement.floatValue(v).then().lessThan(1.24f));
  }

  @Test
  public void floatSatisfiesTest() {
    float v = 1.23f;
    validateStatement(Statement.floatValue(v).satisfies().lessThan(1.24f));
  }

  @Test(expected = IllegalValueException.class)
  public void floatTestFail() {
    float v = 1.23f;
    validateStatement(
        Statement.floatValue(v).then().lessThan(1.22f));
  }


  @Test
  public void floatTransformerTest() {
    float v = 1.23f;
    validateStatement(
        Statement.floatValue(v).then().lessThan(1.24f));
  }

  @Test(expected = IllegalValueException.class)
  public void floatTransformerTestFail() {
    float v = 1.23f;
    validateStatement(
        Statement.floatValue(v).then().lessThan(1.22f));
  }

  @Test(expected = IllegalValueException.class)
  public void toFloatTest() {
    String v = "123";
    validateStatement(
        Statement.stringValue(v)
            .toFloat(Float::parseFloat)
            .then()
            .lessThan(122.0f));
  }
}
