package com.github.valid8j.pcond.core.fluent;

import com.github.valid8j.pcond.core.refl.MethodQuery;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Predicates;

/**
 * // @formatter:off
 * An interface that defines to check a target object.
 * // @formatter:on
 */
public interface AbstractObjectChecker<
    V extends Checker<V, T, R>,
    T,
    R> extends
    Checker<V, T, R> {
  /**
   * Adds a predicate to this checker object to check if the target object is NOT `null`.
   *
   * @return The updated checker object.
   */
  default V notNull() {
    return this.checkWithPredicate(Predicates.isNotNull());
  }

  /**
   * Adds a predicate to this checker object to check if the target object IS `null`.
   *
   * @return The updated checker object.
   */
  default V nullValue() {
    return this.checkWithPredicate(Predicates.isNull());
  }

  /**
   * Adds a predicate to this checker object to check the target object with the argument for `anotherObject` if they
   * are "equal", using `equalTo` method.
   *
   * @param anotherObject An object with which the target object of this checker should be checked.
   * @return The updated checker object.
   */
  default V equalTo(Object anotherObject) {
    return this.checkWithPredicate(Predicates.isEqualTo(anotherObject));
  }

  /**
   * Adds a predicate to this checker object to check the target object and the argument for `anotherObject` if they
   * are referencing the same object.
   *
   * @param anotherObject An object with which the target object of this checker should be checked.
   * @return The updated checker object.
   */
  default V sameReferenceAs(Object anotherObject) {
    return this.checkWithPredicate(Predicates.isSameReferenceAs(anotherObject));
  }

  /**
   * Adds a predicate to this checker object to check if the target object is an instance of `Class klass`.
   *
   * @param klass A class to check if the target object is an instance of.
   * @return The updated checker object.
   */
  default V instanceOf(Class<?> klass) {
    return this.checkWithPredicate(Predicates.isInstanceOf(klass));
  }

  /**
   * Adds a predicate to this checker object to check if the target object returns `true` if a method specified by
   * `methodName` and `args` is invoked.
   * The method to be invoked is chosen by {@link MethodQuery#instanceMethod(Object, String, Object[])}.
   *
   * @param methodName A nme of method to be invoked.
   * @param args       Arguments to be passed by the method.
   * @return The updated object.
   * @see MethodQuery#instanceMethod(Object, String, Object[])
   */
  default V invoke(String methodName, Object... args) {
    return this.checkWithPredicate(Predicates.callp(MethodQuery.instanceMethod(Functions.parameter(), methodName, args)));
  }

  /**
   * Checks if a static method specified by `klass`, `methodName`, and `args`.
   * The method to be invoked is chosen by {@link MethodQuery#classMethod(Class, String, Object[])}.
   * To specify the target object as an argument, you can use the value returned from {@link Functions#parameter()}.
   *
   * @param klass      A class to which the static method to be invoked belongs.
   * @param methodName The name of the method to be invoked.
   * @param args       Arguments to be passed to the method to be invoked.
   * @see MethodQuery#classMethod(Class, String, Object[])
   */
  default V invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.checkWithPredicate(Predicates.callp(MethodQuery.classMethod(klass, methodName, args)));
  }
}
