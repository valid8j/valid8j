package com.github.valid8j.ut.propertybased.utils;

import com.github.valid8j.utils.reporting.ReportParser;
import org.junit.ComparisonFailure;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public class TestCheck<T, R> {
  final Function<T, R> transform;
  final Predicate<R>   check;

  TestCheck(Function<T, R> transform, Predicate<R> check) {
    this.transform = requireNonNull(transform);
    this.check = requireNonNull(check);
  }

  public String toString() {
    return transform + "=>" + check;
  }

  static <T> TestCheck<T, T> createFromSimplePredicate(Predicate<T> predicate) {
    return new TestCheck<>(ReportCheckUtils.makePrintableFunction("identity", Function.identity()), predicate);
  }

  public static TestCheck<ComparisonFailure, List<Integer>> numbersOfExpectAndActualSummariesAreEqual() {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction("numbersOfExpectAndActualSummaries",
            e -> asList(
                new ReportParser(e.getExpected()).summary().records().size(),
                new ReportParser(e.getActual()).summary().records().size())),
        ReportCheckUtils.makePrintablePredicate("areEqual", e -> Objects.equals(e.get(0), e.get(1)))
    );
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfActualSummariesIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction(
            "numberOfActualSummaries",
            e -> new ReportParser(e.getActual()).summary().records().size()),
        equalsPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfActualSummariesIsGreaterThanOrEqualTo(int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction(
            "numberOfActualSummaries",
            e -> new ReportParser(e.getActual()).summary().records().size()),
        greaterThanOrEqualToPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfExpectSummariesWithDetailsIsGreaterThanOrEqualTo(int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction(
            "numberOfActualSummaries",
            e -> (int) new ReportParser(e.getActual()).summary().records().stream().filter(r -> r.detailIndex().isPresent()).count()),
        greaterThanOrEqualToPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfExpectSummariesWithDetailsIsEqualTo(int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction(
            "numberOfActualSummaries",
            e -> (int) new ReportParser(e.getActual()).summary().records().stream().filter(r -> r.detailIndex().isPresent()).count()),
        greaterThanOrEqualToPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, List<Long>> numbersOfExpectAndActualSummariesWithDetailsAreEqual() {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction("numbersOfExpectAndActualSummariesWithDetails",
            e -> asList(
                new ReportParser(e.getExpected()).summary().records().stream().filter(s -> s.detailIndex().isPresent()).count(),
                new ReportParser(e.getActual()).summary().records().stream().filter(s -> s.detailIndex().isPresent()).count())),
        ReportCheckUtils.makePrintablePredicate("areEqual", e -> Objects.equals(e.get(0), e.get(1)))
    );
  }

  public static TestCheck<ComparisonFailure, Long> numberOfExpectSummariesWithDetailsIsEqualTo(long numberOfSummariesWithDetails) {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction("numberOfExpectSummariesWithDetail", comparisonFailure -> new ReportParser(comparisonFailure.getExpected()).summary().records().stream().filter(e -> e.detailIndex().isPresent()).count()),
        equalsPredicate(numberOfSummariesWithDetails));
  }

  public static TestCheck<ComparisonFailure, List<Integer>> numbersOfExpectAndActualDetailsAreEqual() {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction("numbersOfExpectAndActualDetails",
            e -> asList(
                new ReportParser(e.getExpected()).details().size(),
                new ReportParser(e.getActual()).details().size()
            )),
        ReportCheckUtils.makePrintablePredicate("areEqual", e -> Objects.equals(e.get(0), e.get(1))));
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfExpectDetailsIsGreaterThanOrEqualTo(int numberOfExpectDetailsToBe) {
    return new TestCheck<>(
        ReportCheckUtils.makePrintableFunction("numberOfExpectDetails", comparisonFailure -> new ReportParser(comparisonFailure.getExpected()).details().size()),
        greaterThanOrEqualToPredicate(numberOfExpectDetailsToBe));
  }

  public static TestCheck<ComparisonFailure, List<String>> formNamesContainAllOf(List<String> tokens, Function<ComparisonFailure, String> function) {
    return new TestCheck<>(
        functionToFindTokensInColumnByCondition(
            tokens,
            function,
            ReportCheckUtils.makePrintableFunction("formName", ReportParser.Summary.Record::op),
            ReportCheckUtils.makePrintableBiPredicate("notFoundInColumn", TestCheck::notFoundInColumn)),
        ReportCheckUtils.makePrintablePredicate("isEmpty", List::isEmpty));
  }


  public static TestCheck<ComparisonFailure, List<String>> inputValuesContainAllOf(List<String> tokens, Function<ComparisonFailure, String> function) {
    return new TestCheck<>(
        functionToFindTokensInColumnByCondition(
            tokens,
            function,
            ReportCheckUtils.makePrintableFunction("input", (ReportParser.Summary.Record r) -> r.in().orElse("")),
            ReportCheckUtils.makePrintableBiPredicate("notFoundInColumn", TestCheck::notFoundInColumn)),
        ReportCheckUtils.makePrintablePredicate("isEmpty", List::isEmpty));
  }

  public static TestCheck<ComparisonFailure, List<String>> expectDetailAtContainsToken(int i, String token) {
    return detailAtContainsToken(comparisonFailureToExpected(), i, token);
  }

  public static TestCheck<ComparisonFailure, List<String>> actualDetailAtContainsToken(int i, String token) {
    return detailAtContainsToken(comparisonFailureToActual(), i, token);
  }

  public static TestCheck<ComparisonFailure, List<String>> detailAtContainsToken(Function<ComparisonFailure, String> reportSelector, int i, String token) {
    requireNonNull(token);
    return detailAtContains(reportSelector, i, ReportCheckUtils.makePrintableBiPredicate(format("contains[%s]", token), (p, s) -> s.contains(token)));
  }

  public static TestCheck<ComparisonFailure, List<String>> detailAtContains(Function<ComparisonFailure, String> reportSelector, int i, BiPredicate<Integer, String> lineCondition) {
    return checkDetailAt(
        reportSelector,
        i,
        ReportCheckUtils.makePrintablePredicate(
            "anyMatch[" + lineCondition + "]",
            l -> {
              AtomicInteger c = new AtomicInteger(0);
              return l.stream().anyMatch(line -> lineCondition.test(c.getAndIncrement(), line));
            }));
  }

  public static TestCheck<ComparisonFailure, List<String>> checkDetailAt(Function<ComparisonFailure, String> reportSelector, int i, Predicate<List<String>> condition) {
    return new TestCheck<>(
        functionToExtractDetailAt(i, reportSelector),
        condition);
  }


  private static Function<ComparisonFailure, List<String>> functionToFindTokensInColumnByCondition(
      List<String> tokens,
      Function<ComparisonFailure, String> reportSelector,
      Function<ReportParser.Summary.Record, String> columnSelector,
      BiPredicate<String, Collection<String>> condition) {
    return ReportCheckUtils.makePrintableFunction("among[" + tokens + "].notFoundIn[" + reportSelector + "." + columnSelector + "]",
        comparisonFailure -> {
          Collection<String> column = new ReportParser(reportSelector.apply(comparisonFailure))
              .summary()
              .records()
              .stream()
              .map(columnSelector)
              .collect(Collectors.toList());
          return tokens.stream()
              .filter(t -> condition.test(t, column))
              .collect(Collectors.toList());
        });
  }

  private static Function<ComparisonFailure, List<String>> functionToExtractDetailAt(int i, Function<ComparisonFailure, String> reportSelector) {
    return ReportCheckUtils.makePrintableFunction(
        "[" + reportSelector + "]->reportToDetailAt[" + i + "]",
        comparisonFailure -> new ReportParser(reportSelector.apply(comparisonFailure)).details().get(i).body());
  }

  private static Predicate<List<String>> sizeIs(Predicate<Integer> p, int i) {
    return ReportCheckUtils.makePrintablePredicate("sizeIs[" + p + "][" + i + "]", l -> p.test(i));
  }

  private static boolean notFoundInColumn(String token, Collection<String> column) {
    return column.stream().noneMatch(x -> x.contains(token));
  }

  public static Function<ComparisonFailure, String> comparisonFailureToExpected() {
    return ReportCheckUtils.makePrintableFunction("expected", ComparisonFailure::getExpected);
  }

  public static Function<ComparisonFailure, String> comparisonFailureToActual() {
    return ReportCheckUtils.makePrintableFunction("actual", ComparisonFailure::getActual);
  }

  public static <T> Consumer<TestCase.Builder.ForThrownException<T, ComparisonFailure>> genericConfiguratorForComparisonFailure() {
    return b -> b.addCheck(numberOfExpectSummariesWithDetailsIsGreaterThanOrEqualTo(1))
        .addCheck(numbersOfExpectAndActualSummariesAreEqual())
        .addCheck(numbersOfExpectAndActualSummariesWithDetailsAreEqual())
        .addCheck(numberOfExpectDetailsIsGreaterThanOrEqualTo(1))
        .addCheck(numbersOfExpectAndActualDetailsAreEqual());
  }

  public static <T> Predicate<T> equalsPredicate(T w) {
    return ReportCheckUtils.makePrintablePredicate("isEqualTo(" + w + ")", v -> Objects.equals(v, w));
  }

  public static Predicate<Integer> greaterThanOrEqualToPredicate(int w) {
    return ReportCheckUtils.makePrintablePredicate("greaterThanOrEqualTo(" + w + ")", (Integer v) -> v >= w);
  }
}
