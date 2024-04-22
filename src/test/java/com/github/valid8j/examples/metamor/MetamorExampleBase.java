package com.github.valid8j.examples.metamor;

import com.github.valid8j.classic.TestAssertions;
import com.github.valid8j.pcond.core.Evaluator;
import com.github.valid8j.pcond.core.printable.PrintableFunction;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.validator.Validator;
import com.github.valid8j.metamor.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class MetamorExampleBase {

  @BeforeClass
  public static void beforeAll() {
    Validator.reconfigure(b -> b.reportComposer(new MetamorphicReportComposer()));
  }

  @Test
  public void testMetamorphicTest4a() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactoryWithPreformer.Builder<Double, Double, Double, IoPair<Double, Double>, Double>()
            .fut(Printables.function(() -> "Math::sin", Math::sin))
            .addInputResolvers(
                b -> b.function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - ERROR)
                    .build())
            .preformer("output", IoPair::output)
            .reduce("[0] - [1]", (Dataset<Double> ds) -> ds.get(0) - ds.get(1))
            .check(isCloseTo(0.0, acceptableError()))
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
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - ERROR)
                    .build())
            .preformer("output", IoPair::output)
            .reduce("[0] - [1]", (Dataset<Double> ds) -> ds.get(0) - ds.get(1))
            .check(isCloseTo(0.0, acceptableError()))
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
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - ERROR)
                    .build())
            .transformer(Printables.function(() -> "[0] - [1]", (Dataset<IoPair<Double, Double>> ds) -> ds.get(0).output() - ds.get(1).output()))
            .checker(isCloseTo(0.0, acceptableError()))
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
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - ERROR)
                    .build())
            .preformer(Printables.function("output", IoPair::output))
            .propositionFactory(Proposition.Factory.create((Dataset<Double> ds) -> areCloseToEachOther(ds.get(0), ds.get(1), acceptableError()), (Object[] args) -> String.format("%s = %s", args), i -> "x[" + i + "]", 2))
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
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x - ERROR)
                    .build())
            .proposition("output({0})=output({1})", (Dataset<IoPair<Double, Double>> ds) -> areCloseToEachOther(ds.get(0).output(), ds.get(1).output(), acceptableError()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1e() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactory.Builder<Double, Double, Double, Double>().fut(Printables.function(() -> "Math::sin", x -> Math.sin(x + ERROR)))
            .sourceVariableName("X")
            .inputVariableName("IN")
            .ioVariableName("IO")
            .outputVariableName("OUT")
            .addInputResolvers(
                b -> b
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x)
                    .build())
            .proposition("{0}.output()={1}.output()", (Dataset<IoPair<Double, Double>> ds) -> areCloseToEachOther(ds.get(0).output(), ds.get(1).output(), acceptableError()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1f() {
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactoryWithPreformer.Builder<Double, Double, Double, Double, Double>().fut(Printables.function(() -> "Math::sin", x -> Math.sin(x + ERROR)))
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
            .proposition("{0}={1}", (Dataset<Double> ds) -> areCloseToEachOther(ds.get(0), ds.get(1), acceptableError()))
            .toMetamorphicTestPredicate());
  }


  @Test
  public void testMetamorphicTest1g() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        new MetamorphicTestCaseFactory.Builder<Double, Double, Double, Double>()
            .fut(Printables.function(() -> "Math::sin", x -> Math.sin(x + ERROR /* error */)))
            .addInputResolvers(
                b -> b
                    .function((x) -> String.format("%s", x), x -> x)
                    .function((x) -> String.format("πー%s", x), x -> Math.PI - x)
                    .build())
            .preformer("output", IoPair::output)
            .proposition("{0}={1}", (Dataset<Double> ds) -> areCloseToEachOther(ds.get(0), ds.get(1), acceptableError()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1h() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + ERROR /* error */))
            .sourceValueType(double.class)
            .addInputResolver((x) -> String.format("%s", x), x -> x)
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .outputOnly()
            .proposition("{0}={1}", (Dataset<Double> ds) -> areCloseToEachOther(ds.get(0), ds.get(1), acceptableError()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1h1() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + ERROR /* error */))
            .sourceValueType(double.class)
            .addInputResolver((x) -> String.format("%s", x), x -> x)
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .preformer(Functions.<IoPair<Double, Double>>identity().andThen(Printables.function("output", IoPair::output)))
            .proposition("{0}-{1}=0", (Dataset<Double> ds) -> areCloseToEachOther(ds.get(0) - ds.get(1), 0, acceptableError()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest1i() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + ERROR /* error */))
            .outputVariableName("<OUT>")
            .makeInputResolversEndomorphic()
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .outputOnly()
            .proposition("{0}={1}", (Dataset<Double> ds) -> areCloseToEachOther(ds.get(0), ds.get(1), acceptableError()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest2a() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + ERROR /* error */))
            .makeInputResolversEndomorphic()
            .addInputResolver((x) -> String.format("πー%s", x), x -> Math.PI - x)
            .outputOnly()
            .proposition("{0}={1}", (Dataset<Double> ds) -> areCloseToEachOther(ds.get(0), ds.get(1), acceptableError()))
            .toMetamorphicTestPredicate());
  }

  @Test
  public void testMetamorphicTest3a() {
    /* error */
    TestAssertions.assertThat(
        1.23,
        MetamorphicTestCaseFactory.forFunctionUnderTest("Math::sin", (Double x) -> Math.sin(x + ERROR /* error */))
            .makeInputResolversEndomorphic()
            .addInputResolver((x) -> String.format("π/2ー%s", x), x -> Math.PI / 2 - x)
            .outputOnly()
            .preform("^2", x -> x * x)
            .reduce("sum", ds -> ds.stream().mapToDouble(x -> x).sum())
            .check(makeAcceptObjectAsParameter(isCloseTo(1.0, acceptableError())))
            .toMetamorphicTestPredicate());
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

  public static final double ERROR = 0.0000;

  public abstract double acceptableError();

  public static Predicate<Double> isCloseTo(double expectedValue, double acceptableError) {
    return Printables.predicate(
        () -> "isCloseTo[" + expectedValue + ", " + acceptableError + "]",
        v -> Math.abs(v - expectedValue) < acceptableError);
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<Object> makeAcceptObjectAsParameter(Predicate<T> p) {
    return Printables.predicate(() -> Objects.toString(p), v -> p.test((T) v));
  }

  public static boolean areCloseToEachOther(double v, double w, double acceptableError) {
    return Math.abs(v - w) < acceptableError;
  }
}
