package com.github.valid8j.pcond.validator;

import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class Explanation {
  private final String                message;
  private final ReportComposer.Report expected;
  private final ReportComposer.Report actual;

  public Explanation(String message) {
    this(message, ReportComposer.Utils.composeReport("", null), ReportComposer.Utils.composeReport("", null));
  }

  public Explanation(String message, ReportComposer.Report expected, ReportComposer.Report actual) {
    this.message = message;
    this.expected = requireNonNull(expected);
    this.actual = requireNonNull(actual);
  }

  public String message() {
    return this.message;
  }

  public ReportComposer.Report expected() {
    return this.expected;
  }

  public ReportComposer.Report actual() {
    return this.actual;
  }

  public String toString() {
    return actual != null ?
        format("%s%n%s", message, composeDiff(expected, actual)) :
        message;
  }

  private static String composeDiff(ReportComposer.Report expected, ReportComposer.Report actual) {
    String[] e = splitAndTrim(expected.summary());
    String[] a = splitAndTrim(actual.summary());
    List<String> b = new LinkedList<>();
    for (int i = 0; i < Math.max(a.length, e.length); i++) {
      if (i < Math.min(e.length, a.length) && Objects.equals(e[i], a[i])) {
        b.add(format("          %s", a[i]));
      } else {
        b.add(format("Mismatch>:%s", i < a.length ? a[i] : ""));
      }
    }
    b.add(InternalUtils.newLine());
    assert expected.details().size() == actual.details().size();
    return !expected.details().isEmpty() ?
        b.stream().collect(joining(InternalUtils.newLine()))
            + IntStream.range(0, expected.details().size())
            .mapToObj(i -> formatDetailItemPair(i, expected.details().get(i), actual.details().get(i)))
            .collect(joining(InternalUtils.newLine())) :
        "";
  }

  private static String formatDetailItemPair(int i, String detailItemForExpectation, String detailItemForActual) {
    return format(".Detail of failure [%s] (expectation)%n", i)
        + format("----%n")
        + detailItemForExpectation
        + InternalUtils.newLine()
        + format("----%n")
        + InternalUtils.newLine()
        + format(".Detail of failure [%s] (actual value)%n", i)
        + format("----%n")
        + detailItemForActual
        + InternalUtils.newLine()
        + "----";
  }

  public static String reportToString(ReportComposer.Report report) {
    String ret = report.summary();
    ret += InternalUtils.newLine();
    ret += InternalUtils.newLine();
    ret += IntStream.range(0, report.details().size())
        .mapToObj(i -> formatDetailItem(i, report.details().get(i)))
        .collect(joining(InternalUtils.newLine()));
    return ret;
  }

  private static String formatDetailItem(int i, String detailItem) {
    return format(".Detail of failure [%s]%n", i)
        + format("----%n")
        + detailItem
        + InternalUtils.newLine()
        + format("----%n");
  }

  public static Explanation fromMessage(String msg) {
    return new Explanation(msg);
  }

  private static String[] splitAndTrim(String expected) {
    String[] in = expected.split(InternalUtils.newLine());
    List<String> out = new LinkedList<>();
    boolean nonEmptyFound = false;
    for (int i = in.length - 1; i >= 0; i--) {
      if (!"".equals(in[i]))
        nonEmptyFound = true;
      if (nonEmptyFound)
        out.add(0, in[i]);
    }
    return out.toArray(new String[0]);
  }
}
