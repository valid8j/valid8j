package com.github.dakusui.valid8j_pcond.types;

/**
 * Temporarily commented out for improving new fluent model.
 */
public class DoubleTest {
  /*
  @Test
  public void doubleTest() {
    double v = 1.23d;
    validate(
        v,
        when().asDouble().then().lessThan(1.24d));
  }

  @Test(expected = IllegalValueException.class)
  public void doubleTestFail() {
    double v = 1.23d;
    validate(
        v,
        when().asDouble().then().lessThan(1.22d));
  }

  @Test
  public void doubleTransformerTest() {
    double v = 1.23d;
    validate(
        v,
        when().asObject().asDouble().then().lessThan(1.24d));
  }

  @Test(expected = IllegalValueException.class)
  public void doubleTransformerTestFail() {
    double v = 1.23d;
    validate(
        v,
        when().asObject().asDouble().then().lessThan(1.22d));
  }

  @Test(expected = IllegalValueException.class)
  public void toDoubleTest() {
    String v = "123";
    validate(
        v,
        when().asString().toDouble(Double::parseDouble).then().lessThan(122.0));
  }

   */
}
