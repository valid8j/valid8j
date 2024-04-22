package com.github.valid8j.utils;

import com.github.valid8j.utils.reporting.ReportParser;
import com.github.valid8j.pcond.forms.Printables;
import org.junit.ComparisonFailure;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.valid8j.pcond.forms.Printables.function;

public enum TestUtils {
  ;

  static final        PrintStream STDOUT = System.out;
  static final        PrintStream STDERR = System.err;
  public static final PrintStream NOP    = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) {
    }
  });

  /**
   * Typically called from a method annotated with {@literal @}{@code Before} method.
   */
  public static void suppressStdOutErrIfUnderPitestOrSurefire() {
    if (isRunUnderPitest() || TestUtils.isRunUnderSurefire()) {
      System.setOut(NOP);
      System.setErr(NOP);
    }
  }

  /**
   * Typically called from a method annotated with {@literal @}{@code After} method.
   */
  public static void restoreStdOutErr() {
    System.setOut(STDOUT);
    System.setErr(STDERR);
  }

  public static boolean isRunUnderSurefire() {
    return System.getProperty("surefire.real.class.path") != null;
  }

  public static boolean isRunUnderPitest() {
    return Objects.equals(System.getProperty("underpitest"), "yes");
  }

  public static String firstLineOf(String multilineString) {
    return lineAt(multilineString, 0);
  }

  public static String lineAt(String multilineString, int position) {
    return split(multilineString)[position];
  }

  public static String[] split(String multilineString) {
    return multilineString.split("\\r?\\n");
  }

  public static int numLines(String multilineString) {
    return split(multilineString).length;
  }

  public static Function<String, String> stringToLowerCase() {
    return function("stringToLowerCase", String::toLowerCase);
  }

  /**
   * Transform a given string into an easy to check form, where double quotates are replaced with single quotes and continuing spaces into single space.
   *
   * @param str A string to be converted.
   * @return Simplified string.
   */
  public static String simplifyString(String str) {
    return str.replaceAll(" +", " ").replaceAll("\"", "'");
  }

  public static Function<String, String> toLowerCase() {
    return function("toLowerCase", String::toLowerCase);
  }

  public static Function<String, String> toUpperCase() {
    return function("toUpperCase", String::toUpperCase);
  }

  public static Predicate<String> alwaysFalse() {
    return Printables.predicate("alwaysFalse", v -> false);
  }

  public static Predicate<ComparisonFailure> expectationSummarySizeIsEqualTo(final int expectedSize) {
    return Printables.predicate(
        "expectationSummarySize==" + expectedSize,
        comparisonFailure -> new ReportParser(comparisonFailure.getExpected()).summary().records().size() == expectedSize);
  }

  public static List<ReportParser.Summary.Record> summariesWithDetailsOf(String expectedOrActualStringInComparisonFailure) {
    return new ReportParser(expectedOrActualStringInComparisonFailure)
        .summary()
        .records()
        .stream()
        .filter(r -> r.detailIndex().isPresent())
        .collect(Collectors.toList());
  }
}
