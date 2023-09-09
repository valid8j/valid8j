package com.github.dakusui.thincrest.metamor;

import com.github.dakusui.thincrest.TestAssertions;
import com.github.dakusui.valid8j_pcond.core.Evaluator;
import com.github.dakusui.valid8j_pcond.core.printable.PrintableFunction;
import com.github.dakusui.valid8j_pcond.forms.Functions;
import com.github.dakusui.valid8j_pcond.forms.Predicates;
import com.github.dakusui.valid8j_pcond.forms.Printables;
import com.github.dakusui.valid8j_pcond.validator.Validator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.valid8j_pcond.forms.Functions.identity;
import static com.github.dakusui.valid8j_pcond.forms.Predicates.isEqualTo;
import static com.github.dakusui.valid8j_pcond.forms.Predicates.transform;
import static java.util.Arrays.asList;

public class MetamorSandbox {

  @BeforeClass
  public static void beforeAll() {
    Validator.reconfigure(b -> b.reportComposer(new MetamorphicReportComposer()));
  }

  @Test
  public void main() {
    TestAssertions.assertThat("XHello", transform(createFunction()).check(createPredicate()));
  }

  @Test
  public void testMetamorphicTest4a() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactoryWithPreformer.Builder<Double, Double, Double, IoPair<Double, Double>, Double>()
            .fut(Printables.function(() -> "Math::sin", Math::sin))
            .addInputResolvers(
                b -> b.function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - 0.0001)
                    .build())
            .preformer("output", IoPair::output)
            .reduce("[0] - [1]", (Dataset<Double> ds) -> ds.get(0) - ds.get(1))
            .check(Predicates.equalTo(0.0))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1a() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactoryWithPreformer.Builder<Double, Double, Double, Double, Double>()
            .fut(Printables.function(() -> "Math::sin", Math::sin))
            .addInputResolvers(
                b -> b.function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - 0.0001)
                    .build())
            .preformer("output", IoPair::output)
            .reduce("[0] - [1]", (Dataset<Double> ds) -> ds.get(0) - ds.get(1))
            .check(Predicates.equalTo(0.0))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1b() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactory.Builder<Double, Double, Double, Double>().fut(Printables.function(() -> "Math::sin", Math::sin))
            .inputResolverSequenceFactory(
                new InputResolver.Sequence.Factory.Builder<Double, Double, Double>("input", "x")
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - 0.0001)
                    .build())
            .transformer(Printables.function(() -> "[0] - [1]", (Dataset<IoPair<Double, Double>> ds) -> ds.get(0).output() - ds.get(1).output()))
            .checker(Predicates.equalTo(0.0))
            .build()
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1c() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactoryWithPreformer.Builder<Double, Double, Double, Double, Double>().fut(Printables.function(() -> "Math::sin", Math::sin))
            .inputResolverSequenceFactory(
                new InputResolver.Sequence.Factory.Builder<Double, Double, Double>("input", "x")
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - 0.0001)
                    .build())
            .preformer(Printables.function("output", IoPair::output))
            .propositionFactory(Proposition.Factory.create((Dataset<Double> ds) -> Objects.equals(ds.get(0), ds.get(1)), args -> MessageFormat.format("{0}={1}", args), i -> "x[" + i + "]", 2))
            .build().toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1d() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactory.Builder<Double, Double, Double, Double>().fut(Printables.function(() -> "Math::sin", Math::sin))
            .inputResolverSequenceFactory(
                new InputResolver.Sequence.Factory.Builder<Double, Double, Double>("input", "x")
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - 0.0001)
                    .build())
            .proposition("output({0})=output({1})", (Dataset<IoPair<Double, Double>> ds) -> Objects.equals(ds.get(0).output(), ds.get(1).output()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1e() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactory.Builder<Double, Double, Double, Double>().fut(Printables.function(() -> "Math::sin", x -> Math.sin(x + 0.0001)))
            .sourceVariableName("X")
            .inputVariableName("IN")
            .ioVariableName("IO")
            .outputVariableName("OUT")
            .addInputResolvers(
                b -> b
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x)
                    .build())
            .proposition("{0}.output()={1}.output()", (Dataset<IoPair<Double, Double>> ds) -> Objects.equals(ds.get(0).output(), ds.get(1).output()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1f() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactoryWithPreformer.Builder<Double, Double, Double, Double, Double>().fut(Printables.function(() -> "Math::sin", x -> Math.sin(x + 0.0001)))
            .sourceVariableName("X")
            .inputVariableName("IN")
            .ioVariableName("IO")
            .outputVariableName("OUT")
            .addInputResolvers(
                b -> b
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x)
                    .build())
            .preformer("output", IoPair::output)
            .proposition("{0}={1}", (Dataset<Double> ds) -> Objects.equals(ds.get(0), ds.get(1)))
            .toMetamorphicTestPredicate());
  }


  @Test
  public void testMetamorphicTest1g() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactory.Builder<Double, Double, Double, Double>()
            .fut(Printables.function(() -> "Math::sin", x -> Math.sin(x + 0.0001 /* error */)))
            .addInputResolvers(
                b -> b
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x)
                    .build())
            .preformer("output", IoPair::output)
            .proposition("{0}={1}", (Dataset<Double> ds) -> Objects.equals(ds.get(0), ds.get(1)))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1h() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + 0.0001 /* error */))
            .sourceValueType(double.class)
            .addInputResolver((x) -> String.format("%s", x), x -> x)
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .outputOnly()
            .proposition("{0}={1}", (Dataset<Double> ds) -> Objects.equals(ds.get(0), ds.get(1)))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1ha() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + 0.0001 /* error */))
            .sourceValueType(double.class)
            .addInputResolver((x) -> String.format("%s", x), x -> x)
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .preformer(Functions.<IoPair<Double, Double>>identity().andThen(Printables.function("output", IoPair::output)))
            .proposition("{0}={1}", (Dataset<Double> ds) -> Objects.equals(ds.get(0), ds.get(1)))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1i() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + 0.0001 /* error */))
            .outputVariableName("<OUT>")
            .makeInputResolversEndomorphic()
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .outputOnly()
            .proposition("{0}={1}", (Dataset<Double> ds) -> Objects.equals(ds.get(0), ds.get(1)))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest2a() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + 0.0001 /* error */))
            .makeInputResolversEndomorphic()
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .outputOnly()
            .proposition("{0}={1}", (Dataset<Double> ds) -> Objects.equals(ds.get(0), ds.get(1)))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest3a() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + 0.0001 /* error */))
            .makeInputResolversEndomorphic()
            .addInputResolver((x) -> String.format("π/2ー%s", x), x -> Math.PI / 2 - x)
            .outputOnly()
            .preform("^2", x -> x * x)
            .preform("+1", x -> x + 1)
            .reduce("sum", ds -> ds.stream().mapToDouble(x -> x))
            .check(isEqualTo(1))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testString() {
    TestAssertions.assertThat(
        "helloWorld",
        transform(
            identity()
                .andThen(Printables.function("HI", o -> Objects.toString(o).toUpperCase()))
                .andThen(identity())
                .andThen(identity())
                .andThen(identity()))
            .check(isEqualTo("hello"))
    );
  }

  private static PrintableFunction<String, String> functionToUpperCase() {
    return ((PrintableFunction<String, String>) Printables.function("toUpperCase", (String s) -> {
      System.out.println("s:" + s);
      return s.toUpperCase();
    })).makeTrivial();
  }

  private static Function<String, String> createFunction() {
    class Ret implements Function<String, String>/*, Evaluator.Explainable*/ {
      @Override
      public String apply(String s) {
        return s.substring(1);
      }
    }
    return Printables.function("substring[1]", new Ret());
  }

  @Test
  public void testStream() {
    TestAssertions.assertThat(
        asList("Hello", "world"),
        transform(Functions.<String>stream()
            .andThen(stream -> stream.map(String::toLowerCase)))
            .check(Predicates.anyMatch(Predicates.endsWith("X"))));
  }

  private static Predicate<String> createPredicate() {
    class Ret implements Predicate<String>, Evaluator.Explainable {

      @Override
      public Object explainOutputExpectation() {
        return (Evaluator.Snapshottable) () -> "isEqualTo('hello')";
      }

      @Override
      public Object explainActual(Object o) {
        return (Evaluator.Snapshottable) () -> "butWas:'" + o + "'";
      }

      @Override
      public boolean test(String s) {
        return "hello".equals(s);
      }
    }
    return Printables.predicate("isEqualTo('hello')", new Ret());
  }


  @AfterClass
  public static void afterAll() {
    Validator.resetToDefault();
  }
}
