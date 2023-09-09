package com.github.dakusui.thincrest.metamor;

import com.github.dakusui.valid8j_pcond.core.EvaluationEntry;
import com.github.dakusui.valid8j_pcond.core.Evaluator;
import com.github.dakusui.valid8j_pcond.internals.InternalUtils;
import com.github.dakusui.valid8j_pcond.validator.ReportComposer;

import static com.github.dakusui.valid8j_pcond.core.EvaluationEntry.Type.FUNCTION;

public class MetamorphicReportComposer implements ReportComposer {
  
  @Override
  public FormattedEntry createFormattedEntryForExpectation(EvaluationEntry evaluationEntry) {
    if (evaluationEntry.type() == FUNCTION)
      return new FormattedEntry(
          InternalUtils.formatObject(extractInput(evaluationEntry.inputExpectation())),
          evaluationEntry.formName(),
          InternalUtils.indent(evaluationEntry.level()),
          InternalUtils.formatObject(extractOutput(evaluationEntry.outputExpectation())),
          isExplanationRequiredForExpectation(evaluationEntry)
      );
    return Utils.createFormattedEntryForExpectation(this, evaluationEntry);
  }
  
  @Override
  public FormattedEntry createFormattedEntryForActualValue(EvaluationEntry evaluationEntry) {
    if (evaluationEntry.type() == FUNCTION)
      return new FormattedEntry(
          InternalUtils.formatObject(extractInput(evaluationEntry.inputActualValue())),
          evaluationEntry.formName(),
          InternalUtils.indent(evaluationEntry.level()),
          InternalUtils.formatObject(extractOutput(evaluationEntry.outputActualValue())),
          isExplanationRequiredForActualValue(evaluationEntry));
    return Utils.createFormattedEntryForActualValue(this, evaluationEntry);
  }
  
  @Override
  public boolean requiresExplanation(EvaluationEntry evaluationEntry) {
    if (evaluationEntry.outputExpectation() instanceof IoContext.Ongoing.Snapshot)
      return true;
    return ReportComposer.super.requiresExplanation(evaluationEntry);
  }
  
  private Object extractInput(Object o) {
    if (o instanceof IoContext.Ongoing.Snapshot)
      return ((IoContext.Ongoing.Snapshot) o).in();
    if (o instanceof Evaluator.Snapshottable) {
      Object s = ((Evaluator.Snapshottable) o).snapshot();
      if (s instanceof IoContext.Ongoing.Snapshot)
        return ((IoContext.Ongoing.Snapshot) s).in();
    }
    return o;
  }
  
  private static Object extractOutput(Object o) {
    if (o instanceof IoContext.Ongoing.Snapshot)
      return ((IoContext.Ongoing.Snapshot) o).out();
    return o;
  }
  
  private static boolean isExplanationRequiredForExpectation(EvaluationEntry evaluationEntry) {
    return evaluationEntry.outputExpectation() instanceof IoContext.Ongoing.Snapshot;
  }
  
  private static boolean isExplanationRequiredForActualValue(EvaluationEntry evaluationEntry) {
    return evaluationEntry.outputActualValue() instanceof IoContext.Ongoing.Snapshot;
  }
}
