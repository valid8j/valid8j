package com.github.dakusui.valid8j.pcond.core.fluent;

import com.github.dakusui.valid8j.pcond.core.refl.MethodQuery;
import com.github.dakusui.valid8j.pcond.forms.Predicates;

import static com.github.dakusui.valid8j.pcond.forms.Functions.parameter;

public interface AbstractObjectChecker<
    V extends Checker<V, T, R>,
    T,
    R> extends
    Checker<V, T, R> {
  default V notNull() {
    return this.checkWithPredicate(Predicates.isNotNull());
  }

  default V nullValue() {
    return this.checkWithPredicate(Predicates.isNull());
  }

  /**
   * Checks the object with an argument if they are "equal", using `equalTo` method.
   *
   * @return the updated object.
   */
  default V equalTo(Object anotherObject) {
    return this.checkWithPredicate(Predicates.isEqualTo(anotherObject));
  }

  default V sameReferenceAs(Object anotherObject) {
    return this.checkWithPredicate(Predicates.isSameReferenceAs(anotherObject));
  }

  default V instanceOf(Class<?> klass) {
    return this.checkWithPredicate(Predicates.isInstanceOf(klass));
  }

  default V invoke(String methodName, Object... args) {
    return this.checkWithPredicate(Predicates.callp(MethodQuery.instanceMethod(parameter(), methodName, args)));
  }

  default V invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.checkWithPredicate(Predicates.callp(MethodQuery.classMethod(klass, methodName, args)));
  }
}
