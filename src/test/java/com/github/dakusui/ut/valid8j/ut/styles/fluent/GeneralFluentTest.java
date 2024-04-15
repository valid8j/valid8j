package com.github.dakusui.ut.valid8j.ut.styles.fluent;

import com.github.dakusui.shared.ReportParser;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.valid8j.metamor.MetamorphicReportComposer;
import com.github.dakusui.valid8j.pcond.fluent.Statement;
import com.github.dakusui.valid8j.pcond.forms.Predicates;
import com.github.dakusui.valid8j.pcond.forms.Printables;
import com.github.dakusui.valid8j.pcond.validator.ReportComposer;
import com.github.dakusui.valid8j.pcond.validator.Validator;
import com.github.dakusui.valid8j_pcond.propertybased.utils.TestCase;
import com.github.dakusui.valid8j_pcond.propertybased.utils.TestCaseUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.forms.Functions.parameter;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@RunWith(Parameterized.class)
public class GeneralFluentTest extends TestBase {
  @BeforeClass
  public static void beforeAll() {
    Validator.reconfigure(b -> b.reportComposer(new ReportComposer.Default()));
  }

  static class TestSuite<T> {
    final List<Function<T, Predicate<T>>> statementFactories;
    final List<T>                         passingValues;
    final List<T>                         failingValues;
    
    TestSuite(Function<T, Predicate<T>> statementFactory, List<T> passingValues, List<T> failingValues) {
      this(singletonList(statementFactory), passingValues, failingValues);
    }
    
    TestSuite(List<Function<T, Predicate<T>>> statementFactories, List<T> passingValues, List<T> failingValues) {
      this.statementFactories = statementFactories;
      this.passingValues = passingValues;
      this.failingValues = failingValues;
    }
    
    List<TestCase<T, ?>> createTestCases() {
      return Stream.concat(
              this.passingValues.stream()
                  .flatMap(v -> statementFactories.stream()
                      .map(statementFactory -> TestCaseUtils.testCaseExpectingPass(v, statementFactory.apply(v)).build())),
              this.failingValues.stream()
                  .flatMap(v -> statementFactories.stream()
                      .map(statementFactory -> TestCaseUtils.testCaseExpectingComparisonFailure(v, statementFactory.apply(v)).build())))
          .collect(toList());
    }
  }
  
  private final TestCase<?, ?> testCase;
  
  public GeneralFluentTest(TestCase<?, ?> testCase) {
    this.testCase = testCase;
  }
  
  @Test
  public void exerciseTestCase() throws Throwable {
    TestCaseUtils.exerciseTestCase(this.testCase);
  }
  
  @Parameterized.Parameters(name = "{index}: {0}")
  public static List<TestCase<?, ?>> toTestCases() {
    return Stream.of(
            booleanTestSuite_1(),
            listTestSuite_1(),
            listTestSuite_2(),
            listTestSuite_3a(),
            listTestSuite_3b(),
            stringTestSuite_1(),
            stringTestSuite_2(),
            stringTestSuite_3(),
            objectTestSuite_1(),
            objectTestSuite_2(),
            objectTestSuite_3(),
            objectTestSuite_4(),
            objectTestSuite_5(),
            exceptionTestSuite_1())
        .flatMap(each -> each.createTestCases().stream())
        .collect(toList());
  }
  
  private static TestSuite<Boolean> booleanTestSuite_1() {
    return new TestSuite<>(
        v -> Statement.booleanValue(v).then().isTrue().done(),
        asList(true, Boolean.TRUE),
        asList(false, Boolean.FALSE, null));
  }
  
  private static TestSuite<List<String>> listTestSuite_1() {
    return new TestSuite<>(
        asList(
            (List<String> v) -> Statement.listValue(v).subList(1).then().isNotEmpty().done(),
            (List<String> v) -> Statement.listValue(v).subList(1, v.size()).then().isNotEmpty().done()),
        asList(
            asList("A", "B", "C"),
            asList("A", "B")),
        asList(
            singletonList("X"),
            Collections.emptyList()));
  }
  
  private static TestSuite<List<String>> listTestSuite_2() {
    return new TestSuite<>(
        singletonList((List<String> v) -> Statement.listValue(v).then().isEmpty().done()),
        singletonList(emptyList()),
        singletonList(singletonList("X")));
  }
  
  private static TestSuite<List<String>> listTestSuite_3a() {
    return new TestSuite<>(
        singletonList((List<String> v) -> Statement.objectValue(v).asList().then().isEmpty().done()),
        singletonList(emptyList()),
        singletonList(singletonList("X")));
  }
  
  private static TestSuite<List<String>> listTestSuite_3b() {
    return new TestSuite<>(
        singletonList((List<String> v) -> Statement.objectValue(v).asListOf(String.class).then().isEmpty().done()),
        singletonList(emptyList()),
        singletonList(singletonList("X")));
  }
  
  
  private static TestSuite<String> stringTestSuite_1() {
    return new TestSuite<>(
        asList(
            (String v) -> Statement.stringValue(v).substring(1).parseShort().then().instanceOf(Short.class).equalTo((short) 23).done(),
            (String v) -> Statement.stringValue(v).parseShort().then().instanceOf(Short.class).equalTo((short) 123).done(),
            (String v) -> Statement.stringValue(v).parseLong().then().instanceOf(Long.class).equalTo((long) 123).done(),
            (String v) -> Statement.stringValue(v).parseFloat().then().instanceOf(Float.class).equalTo((float) 123).done(),
            (String v) -> Statement.stringValue(v).parseDouble().then().instanceOf(Double.class).equalTo((double) 123).done()),
        singletonList("123"),
        asList("456", "A", null));
  }
  
  private static TestSuite<String> stringTestSuite_2() {
    return new TestSuite<>(
        singletonList(
            (String v) -> Statement.stringValue(v).parseBoolean().then().instanceOf(Boolean.class).isTrue().done()),
        singletonList("true"),
        asList("false", "XYZ", null));
  }
  
  private static TestSuite<String> stringTestSuite_3() {
    return new TestSuite<>(
        asList(
            (String v) -> Statement.stringValue(v).split(":").then().isNotEmpty().contains("A").contains("B").contains("C").done(),
            (String v) -> Statement.stringValue(v).split(":").then().findElementsInOrder("A", "B", "C").done(),
            (String v) -> Statement.stringValue(v).split(":").then().findElementsInOrderBy(asList(Predicates.isEqualTo("A"), Predicates.isEqualTo("B"), Predicates.isEqualTo("C"))).done()),
        singletonList("A:B:C"),
        asList("A:B", null));
  }
  
  @SuppressWarnings("StringOperationCanBeSimplified")
  private static TestSuite<String> objectTestSuite_1() {
    String s = "hello";
    return new TestSuite<>(
        asList(
            (String v) -> Statement.stringValue(v).then().sameReferenceAs(s).done(),
            /*String#toString() method returns the object itself by specification. Check JavaDoc*/
            (String v) -> Statement.stringValue(v).stringify().then().sameReferenceAs(s).done()),
        singletonList(s),
        singletonList(new String(s)));
  }
  
  @SuppressWarnings("StringOperationCanBeSimplified")
  private static TestSuite<String> objectTestSuite_2() {
    String s = "hello";
    return new TestSuite<>(
        asList(
            (String v) -> Statement.stringValue(v).then().invokeStatic(Objects.class, "equals", "hello", parameter()).done(),
            (String v) -> Statement.stringValue(v).then().invoke("equals", "hello").done(),
            (String v) -> Statement.stringValue(v).invokeStatic(Objects.class, "equals", "hello", parameter()).asBoolean().then().isTrue().done(),
            (String v) -> Statement.stringValue(v).invoke("equals", "hello").asBoolean().then().isTrue().done()),
        asList(s, new String(s)),
        singletonList("HELLO"));
  }
  
  private static TestSuite<String> objectTestSuite_3() {
    String s = "hello";
    return new TestSuite<>(
        singletonList(
            (String v) -> Statement.stringValue(v).then().nullValue().done()),
        singletonList(null),
        singletonList("HELLO"));
  }
  
  private static TestSuite<String> objectTestSuite_4() {
    return new TestSuite<>(
        asList(
            (String v) -> Statement.stringValue(v).parseLong().asObject().asLong().then().equalTo(123L).done(),
            (String v) -> Statement.stringValue(v).parseInt().asObject().asInteger().then().equalTo(123).done(),
            (String v) -> Statement.stringValue(v).parseShort().asObject().asShort().then().equalTo((short) 123).done()
        ),
        singletonList("123"),
        singletonList("124"));
  }
  
  private static TestSuite<String> objectTestSuite_5() {
    
    return new TestSuite<>(
        asList(
            (String v) -> Statement.stringValue(v).parseFloat().asObject().asFloat().then().equalTo(123.4f).done(),
            (String v) -> Statement.stringValue(v).parseDouble().asObject().asDouble().then().equalTo(123.4).done()
        ),
        singletonList("123.4"),
        singletonList("123.5"));
  }
  
  private static TestSuite<String> exceptionTestSuite_1() {
    class IntentionalException extends RuntimeException {
    }
    Function<String, Object> throwRuntimeException = Printables.function("throwRuntimeException", v -> {
      if ("Hello".equals(v))
        throw new IntentionalException();
      return v;
    });
    return new TestSuite<>(
        singletonList(
            (String v) -> Statement.stringValue(v).expectException(Exception.class, throwRuntimeException)
                .then()
                .instanceOf(IntentionalException.class)
                .done()),
        singletonList("Hello"),
        singletonList("Bye"));
  }
  
}
