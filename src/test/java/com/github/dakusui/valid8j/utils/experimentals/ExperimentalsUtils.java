package com.github.dakusui.valid8j.utils.experimentals;

import com.github.dakusui.valid8j.pcond.experimentals.currying.CurriedFunction;

import static com.github.dakusui.valid8j.pcond.forms.Functions.curry;

public enum ExperimentalsUtils {
  ;

  public static CurriedFunction<Object, Object> stringEndsWith() {
    return curry(TargetMethodHolder.class, "stringEndsWith", String.class, String.class);
  }

  public static CurriedFunction<Object, Object> areEqual() {
    return curry(TargetMethodHolder.class, "areEqual", Object.class, Object.class);
  }
}
