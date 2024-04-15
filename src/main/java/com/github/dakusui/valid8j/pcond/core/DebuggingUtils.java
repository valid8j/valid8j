package com.github.dakusui.valid8j.pcond.core;

import com.github.dakusui.valid8j.pcond.validator.Validator;

import java.io.PrintStream;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.indent;

public enum DebuggingUtils {
  ;

  static <T, R> void printIo(String x, EvaluableIo<T, ? extends Evaluable<T>, R> io) {
    if (isDebugLogEnabled())
      System.err.println(x + ":" + io.evaluableType() + ":" + io.evaluable() + "(" + io.input() + ")=" + io.output());
  }

  static <T> void printInput(String x, Evaluable<T> evaluable, ValueHolder<T> input) {
    if (isDebugLogEnabled())
      System.err.println(x + ":" + evaluable + "(" + input + ")");
  }

  static <T, R> void printInputAndOutput(Evaluable<T> evaluable, ValueHolder<T> input, ValueHolder<R> output) {
    if (isDebugLogEnabled())
      System.err.println("TRANSFORMATION:AFTER" + ":" + evaluable + "(" + input + ")=" + output);
  }

  static <T> void printTo(EvaluationContext<T> evaluationContext, PrintStream ps, int indent) {
    if (isDebugLogEnabled()) {
      ps.println(indent(indent) + "context=<" + evaluationContext + ">");
      for (Object each : evaluationContext.resultEntries()) {
        ps.println(indent(indent + 1) + each);
      }
    }
  }

  public static boolean showEvaluableDetail() {
    return Validator.instance().configuration().debugging().map(Validator.Configuration.Debugging::showEvaluableDetail).orElse(false);
  }

  public static boolean suppressSquashing() {
    return Validator.instance().configuration().debugging().map(Validator.Configuration.Debugging::suppressSquashing).orElse(false);
  }
  public static boolean isDebugLogEnabled() {
    return Validator.instance().configuration().debugging().map(Validator.Configuration.Debugging::enableDebugLog).orElse(false);
  }

  public static boolean reportIgnoredEntries() {
    return Validator.instance().configuration().debugging().map(Validator.Configuration.Debugging::reportIgnoredEntries).orElse(false);
  }

  public static boolean passThroughComparisonFailure() {
    return Validator.instance().configuration().debugging().map(Validator.Configuration.Debugging::passThroughComparisonFailure).orElse(false);
  }
}
