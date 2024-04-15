package com.github.dakusui.ut.thincrest.ut.styles;

/**
 * Temporarily commented out for improving new fluent model.
 */
public class MoreFluentObjectTest {
  /*
  @Test
  public void test_asString() {
    String var = "hello";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().asString().isEqualTo("hello"));
  }

  @Test
  public void test_asLong() {
    long var = 123;
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asLong().then().asLong().equalTo(123L));
  }

  @Test
  public void test_asInteger() {
    int var = 123;
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asInteger().then().asInteger().equalTo(123));
  }

  @Test
  public void test_asShort() {
    short var = 123;
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asShort().then().asShort().equalTo((short) 123));
  }

  @Test
  public void test_asDouble() {
    double var = 123.0;
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asDouble().then().asDouble().equalTo(123.0));
  }

  @Test
  public void test_asFloat() {
    float var = 123.0f;
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asFloat().then().asFloat().equalTo(123.0f));
  }

  @Test
  public void test_asBoolean() {
    boolean var = false;
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asBoolean().then().asBoolean().isEqualTo(false));
  }


  @Test
  public void test_asListOf() {
    List<String> var = asList("hello", "world");
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asListOf((String) value()).then().asListOf((String) value()).isEqualTo(asList("hello", "world")));
  }

  @Test
  public void test_asListOfClass() {
    List<String> var = asList("hello", "world");
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asListOfClass(String.class).then().isEqualTo(asList("hello", "world")));
  }

  @Test
  public void test_asStreamOf() {
    Stream<String> var = Stream.of("hello", "world");
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asStreamOf((String) value()).then().asStreamOf((String) value()).allMatch(isNotNull()));
  }

  @Test
  public void test_asStreamOfClass() {
    Stream<String> var = Stream.of("hello", "world");
    TestFluents.assertStatement(Fluents.objectStatement((Object) var).asStreamOfClass(String.class).then().allMatch(isNotNull()));
  }


  @Test
  public void test_intoLongWith() {
    String var = "123";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().toLongWith(Long::parseLong).equalTo(123L));
  }

  @Test
  public void test_intoIntegerWith() {
    String var = "123";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().toIntegerWith(Integer::parseInt).equalTo(123));
  }

  @Test
  public void test_intoShortWith() {
    String var = "123";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().toShortWith(Short::parseShort).equalTo((short) 123));
  }

  @Test
  public void test_intoDoubleWith() {
    String var = "123.0";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().toDoubleWith(Double::parseDouble).equalTo(123.0));
  }

  @Test
  public void test_intoFloatWith() {
    String var = "123.0f";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().toFloatWith(Float::parseFloat).equalTo(123.0f));
  }

  @Test
  public void test_intoBooleanWith() {
    String var = "false";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().toBooleanWith(Boolean::parseBoolean).isEqualTo(false));
  }

  @Test
  public void test_intoObject() {
    String var = "hello";
    TestFluents.assertStatement(Fluents.stringStatement(var).then().toObjectWith(v -> v).isNotNull());
  }

  @Test
  public void test_intoList() {
    Stream<String> var = Stream.of("hello", "world");
    TestFluents.assertStatement(Fluents.streamStatement(var).then()
        .toListWith(s -> s.collect(Collectors.toList()))
        .isEqualTo(asList("hello", "world")));
  }

  @Test
  public void test_intoStream() {
    List<String> var = asList("hello", "world");
    TestFluents.assertStatement(
        Fluents.listStatement(var)
            .then()
            .toStreamWith(Collection::stream)
            .allMatch(
                and(
                    isNotNull(),
                    isInstanceOf(String.class))));
  }

  @Test
  public void test_isNull() {
    String var = null;
    TestFluents.assertStatement(Fluents.stringStatement(var).then().isNull());
  }

  @Test
  public void test_sameReferenceAs() {
    Object var = new Object();
    TestFluents.assertStatement(Fluents.objectStatement(var).then().isSameReferenceAs(var));
  }

  @Test
  public void test_invoke() {
    Object var = new Object();
    TestFluents.assertStatement(Fluents.objectStatement(var).invoke("toString").then().invoke("toString").asString().contains("Object"));
  }

  @Test
  public void test_invokeStatic() {
    Object var = new Object();
    TestFluents.assertStatement(Fluents.objectStatement(var)
        .invokeStatic(Objects.class, "toString", parameter()).then()
        .invokeStatic(Objects.class, "toString", parameter()).asString().contains("Object"));
  }

   */
}
