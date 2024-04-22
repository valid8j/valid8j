package com.github.valid8j.pcond.validator;

import com.github.valid8j.pcond.core.DebuggingUtils;
import com.github.valid8j.pcond.core.EvaluationEntry;
import com.github.valid8j.pcond.fluent.ValueHolder;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface ReportComposer {
  default Explanation explanationFromMessage(String msg) {
    return Explanation.fromMessage(msg);
  }

  default Explanation composeExplanation(String message, List<EvaluationEntry> evaluationEntries) {
    return Utils.composeExplanation(this, message, evaluationEntries);
  }

  default FormattedEntry createFormattedEntryForExpectation(EvaluationEntry evaluationEntry) {
    return Utils.createFormattedEntryForExpectation(this, evaluationEntry);
  }

  default FormattedEntry createFormattedEntryForActualValue(EvaluationEntry evaluationEntry) {
    return Utils.createFormattedEntryForActualValue(this, evaluationEntry);
  }

  default boolean requiresExplanation(EvaluationEntry evaluationEntry) {
    return evaluationEntry.requiresExplanation();
  }

  /**
   * A default implementation of `ReportComposer`.
   */
  class Default implements ReportComposer {
  }

  interface Report {
    String summary();

    List<String> details();

    static Report create(String summary, List<String> details) {
      List<String> detailsCopy = unmodifiableList(new ArrayList<>(details));
      return new Report() {
        @Override
        public String summary() {
          return summary;
        }

        @Override
        public List<String> details() {
          return detailsCopy;
        }
      };
    }
  }

  class FormattedEntry {
    private final String  input;
    private final String  formName;
    private final String  indent;
    private final String  output;
    private final boolean requiresExplanation;

    public FormattedEntry(String input, String formName, String indent, String output, boolean requiresExplanation) {
      this.input = input;
      this.formName = formName;
      this.indent = indent;
      this.output = output;
      this.requiresExplanation = requiresExplanation;
    }

    Optional<String> input() {
      return Optional.ofNullable(this.input);
    }

    String indent() {
      return this.indent;
    }

    String formName() {
      return this.formName;
    }

    Optional<String> output() {
      return Optional.ofNullable(this.output);
    }

    public boolean requiresExplanation() {
      return this.requiresExplanation;
    }
  }

  enum Utils {
    ;

    /**
     * Note that an exception thrown during an evaluation is normally caught by the framework.
     *
     * @param message           A message to be prepended to a summary.
     * @param evaluationHistory An "evaluation history" object represented as a list of evaluation entries.
     * @return An explanation object.
     */
    public static Explanation composeExplanation(ReportComposer reportComposer, String message, List<EvaluationEntry> evaluationHistory) {
      List<Object> detailsForExpectation = new LinkedList<>();
      List<FormattedEntry> summaryDataForExpectations = squashTrivialEntries(reportComposer, evaluationHistory)
          .stream()
          .peek((EvaluationEntry each) -> addToDetailsListIfExplanationIsRequired(reportComposer, detailsForExpectation, each, each::detailOutputExpectation))
          .map(reportComposer::createFormattedEntryForExpectation)
          .collect(toList());
      String textSummaryForExpectations = composeSummaryForExpectations(minimizeIndentation(summaryDataForExpectations));
      List<Object> detailsForActual = new LinkedList<>();
      List<FormattedEntry> summaryForActual = squashTrivialEntries(reportComposer, evaluationHistory)
          .stream()
          .peek((EvaluationEntry each) -> addToDetailsListIfExplanationIsRequired(reportComposer, detailsForActual, each, each::detailOutputActualValue))
          .map(reportComposer::createFormattedEntryForActualValue)
          .collect(toList());
      String textSummaryForActualResult = composeSummaryForActualResults(minimizeIndentation(summaryForActual));
      return new Explanation(message,
          composeReport(textSummaryForExpectations, detailsForExpectation),
          composeReport(textSummaryForActualResult, detailsForActual));
    }

    public static FormattedEntry createFormattedEntryForExpectation(ReportComposer reportComposer, EvaluationEntry entry) {
      return new FormattedEntry(
          InternalUtils.formatObject(entry.inputExpectation()),
          entry.formName(),
          InternalUtils.indent(entry.level()),
          InternalUtils.formatObject(entry.outputExpectation()),
          reportComposer.requiresExplanation(entry));
    }

    public static FormattedEntry createFormattedEntryForActualValue(ReportComposer reportComposer, EvaluationEntry entry) {
      return new FormattedEntry(
          InternalUtils.formatObject(entry.inputActualValue()),
          entry.formName(),
          InternalUtils.indent(entry.level()),
          InternalUtils.formatObject(entry.outputActualValue()),
          reportComposer.requiresExplanation(entry));
    }

    private static List<FormattedEntry> minimizeIndentation(List<FormattedEntry> summaryForActual) {
      String minIndent = summaryForActual.stream()
          .map(e -> e.indent)
          .min(Comparator.comparingInt(String::length))
          .orElse("");
      return summaryForActual.stream()
          .map(e -> new FormattedEntry(e.input, e.formName(), e.indent().replaceFirst(minIndent, ""), e.output, e.requiresExplanation()))
          .collect(toList());
    }

    private static List<EvaluationEntry> squashTrivialEntries(ReportComposer reportComposer, List<EvaluationEntry> evaluationHistory) {
      if (evaluationHistory.size() > 1) {
        List<EvaluationEntry> ret = new LinkedList<>();
        List<EvaluationEntry> entriesToSquash = new LinkedList<>();
        AtomicReference<EvaluationEntry> cur = new AtomicReference<>();
        evaluationHistory.stream()
            .filter(each -> !each.ignored() || DebuggingUtils.reportIgnoredEntries())
            .filter(each -> {
              if (cur.get() != null)
                return true;
              else {
                cur.set(each);
                return false;
              }
            })
            .forEach(each -> {
              if (entriesToSquash.isEmpty()) {
                if (cur.get().isSquashable(each) && !suppressSquashing()) {
                  entriesToSquash.add(cur.get());
                } else {
                  ret.add(cur.get());
                }
              } else {
                entriesToSquash.add(cur.get());
                ret.add(squashEntries(reportComposer, entriesToSquash));
                entriesToSquash.clear();
              }
              cur.set(each);
            });
        finishLeftOverEntries(reportComposer, ret, entriesToSquash, cur);
        return ret.stream()
            .filter(e -> !(e.inputActualValue() instanceof ValueHolder))
            .collect(toList());
      } else {
        return new ArrayList<>(evaluationHistory);
      }
    }

    private static void finishLeftOverEntries(ReportComposer reportComposer, List<EvaluationEntry> out, List<EvaluationEntry> leftOverEntriesToSquash, AtomicReference<EvaluationEntry> leftOver) {
      if (!leftOverEntriesToSquash.isEmpty() && leftOverEntriesToSquash.get(leftOverEntriesToSquash.size() - 1).isSquashable(leftOver.get()) && !suppressSquashing()) {
        leftOverEntriesToSquash.add(leftOver.get());
        out.add(squashEntries(reportComposer, leftOverEntriesToSquash));
      } else {
        if (!leftOverEntriesToSquash.isEmpty())
          out.add(squashEntries(reportComposer, leftOverEntriesToSquash));
        out.add(leftOver.get());
      }
    }

    private static EvaluationEntry squashEntries(ReportComposer reportComposer, List<EvaluationEntry> squashedItems) {
      EvaluationEntry first = squashedItems.get(0);
      return EvaluationEntry.create(
          squashedItems.stream()
              .map(e -> (EvaluationEntry.Impl) e)
              .map(EvaluationEntry::formName)
              .collect(joining(":")),
          first.type(),
          first.level(),
          first.inputExpectation(), first.detailInputExpectation(),
          first.outputExpectation(), computeDetailOutputExpectationFromSquashedItems(squashedItems),
          first.inputActualValue(), null,
          first.outputActualValue(), squashedItems.get(squashedItems.size() - 1).detailOutputActualValue(),
          false,
          squashedItems.stream().anyMatch(reportComposer::requiresExplanation), false);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean suppressSquashing() {
      return DebuggingUtils.suppressSquashing();
    }

    private static String computeDetailOutputExpectationFromSquashedItems(List<EvaluationEntry> squashedItems) {
      return squashedItems.stream()
          .filter(e -> e.type() != EvaluationEntry.Type.TRANSFORM && e.type() != EvaluationEntry.Type.CHECK)
          .map(EvaluationEntry::detailOutputExpectation)
          .map(Objects::toString)
          .collect(joining(":"));
    }

    private static void addToDetailsListIfExplanationIsRequired(ReportComposer reportComposer, List<Object> detailsForExpectation, EvaluationEntry evaluationEntry, Supplier<Object> detailOutput) {
      if (reportComposer.requiresExplanation(evaluationEntry))
        detailsForExpectation.add(detailOutput.get());
    }

    static Report composeReport(String summary, List<Object> details) {
      List<String> stringFormDetails = details != null ?
          details.stream()
              .filter(Objects::nonNull)
              .map(Objects::toString)
              .collect(toList()) :
          emptyList();
      return Report.create(summary, stringFormDetails);
    }

    private static String composeSummaryForActualResults(List<FormattedEntry> formattedEntries) {
      return composeSummary(formattedEntries);
    }

    private static String composeSummaryForExpectations(List<FormattedEntry> formattedEntries) {
      return composeSummaryForActualResults(formattedEntries);
    }

    private static String composeSummary(List<FormattedEntry> formattedEntries) {
      AtomicInteger mismatchExplanationCount = new AtomicInteger(0);
      boolean mismatchExplanationFound = formattedEntries
          .stream()
          .anyMatch(FormattedEntry::requiresExplanation);
      return evaluatorEntriesToString(
          hideInputValuesWhenRepeated(formattedEntries),
          columnLengths -> formattedEntryToString(
              columnLengths[0],
              columnLengths[1],
              columnLengths[2],
              mismatchExplanationCount,
              mismatchExplanationFound));
    }

    private static Function<FormattedEntry, String> formattedEntryToString(
        int inputColumnWidth,
        int formNameColumnLength,
        int outputColumnLength,
        AtomicInteger i,
        boolean mismatchExplanationFound) {
      return (FormattedEntry formattedEntry) ->
          (mismatchExplanationFound ?
              format("%-4s", formattedEntry.requiresExplanation ?
                  "[" + i.getAndIncrement() + "]" : "") :
              "") +
              String.format("%-" + max(2, inputColumnWidth) + "s" +
                      "%-" + (formNameColumnLength + 2) + "s" +
                      "%-" + max(2, outputColumnLength) + "s",
                  formattedEntry.input().orElse(""),
                  formattedEntry.input()
                      .map(v -> "->")
                      .orElse("  ") + InternalUtils.formatObject(InternalUtils.toNonStringObject(formattedEntry.indent() + formattedEntry.formName()), formNameColumnLength - 2),
                  formattedEntry
                      .output()
                      .map(v -> "->" + v).orElse(""));
    }

    private static String evaluatorEntriesToString(List<FormattedEntry> formattedEntries, Function<int[], Function<FormattedEntry, String>> formatterFactory) {
      int maxInputLength = 0, maxIndentAndFormNameLength = 0, maxOutputLength = 0;
      for (FormattedEntry eachEntry : formattedEntries) {
        int inputLength = eachEntry.input().map(String::length).orElse(0);
        if (inputLength > maxInputLength)
          maxInputLength = inputLength;
        int inputAndFormNameLength = eachEntry.indent().length() + eachEntry.formName().length();
        if (inputAndFormNameLength > maxIndentAndFormNameLength)
          maxIndentAndFormNameLength = inputAndFormNameLength;
        int outputLength = eachEntry.output().map(String::length).orElse(0);
        if (outputLength > maxOutputLength)
          maxOutputLength = outputLength;
      }
      int formNameColumnLength = (formNameColumnLength = Math.max(
          DebuggingUtils.showEvaluableDetail() ? 80 : 12,
          Math.min(InternalUtils.summarizedStringLength(), maxIndentAndFormNameLength))) + formNameColumnLength % 2;
      Function<FormattedEntry, String> formatter = formatterFactory.apply(
          new int[] { maxInputLength, formNameColumnLength, maxOutputLength });
      return formattedEntries
          .stream()
          .map(formatter)
          .map(s -> ("+" + s).trim().substring(1))
          .collect(joining(format("%n")));
    }

    private static List<FormattedEntry> hideInputValuesWhenRepeated(List<FormattedEntry> formattedEntries) {
      AtomicReference<Object> previousInput = new AtomicReference<>();
      return formattedEntries.stream()
          .map(each -> {
            if (!Objects.equals(previousInput.get(), each.input())) {
              previousInput.set(each.input());
              return each;
            } else {
              return new FormattedEntry("", each.formName(), each.indent(), each.output().orElse(null), each.requiresExplanation());
            }
          })
          .collect(toList());
    }


  }
}
