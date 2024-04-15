package com.github.dakusui.valid8j.pcond.forms;

import com.github.dakusui.valid8j.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.valid8j.pcond.core.refl.MethodQuery;
import com.github.dakusui.valid8j.pcond.core.refl.Parameter;
import com.github.dakusui.valid8j.pcond.core.printable.PrintablePredicateFactory.Leaf;
import com.github.dakusui.valid8j.pcond.core.printable.PrintablePredicateFactory.ParameterizedLeafFactory;
import com.github.dakusui.valid8j.pcond.internals.InternalChecks;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.pcond.core.refl.ReflUtils.invokeMethod;
import static com.github.dakusui.valid8j.pcond.forms.Printables.function;
import static com.github.dakusui.valid8j.pcond.forms.Printables.predicate;
import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * An entry point for acquiring predicate objects.
 * Predicates retrieved by methods in this class are all "printable".
 */
public class Predicates {
    private Predicates() {
    }

    public static <T> Predicate<T> alwaysTrue() {
        return Leaf.ALWAYS_TRUE.instance();
    }

    public static Predicate<Boolean> isTrue() {
        return Leaf.IS_TRUE.instance();
    }

    public static Predicate<Boolean> isFalse() {
        return Leaf.IS_FALSE.instance();
    }

    public static <T> Predicate<T> isNull() {
        return Leaf.IS_NULL.instance();
    }

    public static <T> Predicate<T> isNotNull() {
        return Leaf.IS_NOT_NULL.instance();
    }

    public static <T> Predicate<T> isEqualTo(T value) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.IS_EQUAL_TO, singletonList(value));
    }

    public static <T> Predicate<T> isSameReferenceAs(T value) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.OBJECT_IS_SAME_AS, singletonList(value));
    }

    @SuppressWarnings({"unchecked", "RedundantClassCall"})
    public static <T> Function<Class<?>, Predicate<T>> isInstanceOf() {
        return Function.class.cast(Def.IS_INSTANCE_OF$2);
    }

    public static Predicate<Object> isInstanceOf(Class<?> value) {
        return applyOnceExpectingPredicate(requireNonNull(value), isInstanceOf());
    }

    private static <T, R> Predicate<R> applyOnceExpectingPredicate(T value, Function<T, Predicate<R>> p) {
        return predicate(() -> format("%s[%s]", p, formatObject(value)), p.apply(value));
    }

    public static <T extends Comparable<? super T>> Predicate<T> gt(T value) {
        return greaterThan(value);
    }

    public static <T extends Comparable<? super T>> Predicate<T> greaterThan(T value) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.GREATER_THAN, singletonList(value));
    }

    public static <T extends Comparable<? super T>> Predicate<T> ge(T value) {
        return greaterThanOrEqualTo(value);
    }

    public static <T extends Comparable<? super T>> Predicate<T> greaterThanOrEqualTo(T value) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.GREATER_THAN_OR_EQUAL_TO, singletonList(value));
    }

    public static <T extends Comparable<? super T>> Predicate<T> lt(T value) {
        return lessThan(value);
    }

    public static <T extends Comparable<? super T>> Predicate<T> lessThan(T value) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.LESS_THAN, singletonList(value));
    }

    public static <T extends Comparable<? super T>> Predicate<T> le(T value) {
        return lessThanOrEqualTo(value);
    }

    public static <T extends Comparable<? super T>> Predicate<T> lessThanOrEqualTo(T value) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.LESS_THAN_OR_EQUAL_TO, singletonList(value));
    }

    public static <T extends Comparable<T>> Predicate<T> eq(T value) {
        return equalTo(value);
    }

    public static <T extends Comparable<T>> Predicate<T> equalTo(T value) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.EQUAL_TO, singletonList(value));
    }

    public static Predicate<String> matchesRegex(String regex) {
        requireNonNull(regex);
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.MATCHES_REGEX, singletonList(regex));
    }

    public static Predicate<String> containsString(String string) {
        requireNonNull(string);
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.CONTAINS_STRING, singletonList(string));
    }

    public static Predicate<String> startsWith(String string) {
        requireNonNull(string);
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.STARTS_WITH, singletonList(string));
    }

    public static Predicate<String> endsWith(String string) {
        requireNonNull(string);
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.ENDS_WITH, singletonList(string));
    }

    public static Predicate<String> equalsIgnoreCase(String string) {
        requireNonNull(string);
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.EQUALS_IGNORE_CASE, singletonList(string));
    }

    public static Predicate<String> isEmptyString() {
        return Leaf.IS_EMPTY_STRING.instance();
    }

    public static Predicate<String> isNullOrEmptyString() {
        return Leaf.IS_NULL_OR_EMPTY_STRING.instance();
    }

    public static <E> Predicate<Collection<E>> contains(Object entry) {
        return ParameterizedLeafFactory.create(ParameterizedLeafFactory.CONTAINS, singletonList(entry));
    }

    public static Predicate<Object[]> isEmptyArray() {
        return Leaf.IS_EMPTY_ARRAY.instance();
    }

    public static Predicate<? super Collection<?>> isEmpty() {
        return Leaf.IS_EMPTY_COLLECTION.instance();
    }

    public static <E> Predicate<Stream<E>> allMatch(Predicate<E> predicate) {
        requireNonNull(predicate);
        return PrintablePredicateFactory.allMatch(predicate);
    }

    public static <E> Predicate<Stream<E>> noneMatch(Predicate<E> predicate) {
        requireNonNull(predicate);
        return PrintablePredicateFactory.noneMatch(predicate);
    }

    public static <E> Predicate<Stream<E>> anyMatch(Predicate<E> predicate) {
        requireNonNull(predicate);
        return PrintablePredicateFactory.anyMatch(predicate);
    }

    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<? super T>... predicates) {
        return PrintablePredicateFactory.and(asList(predicates));
    }

    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<? super T>... predicates) {
        return PrintablePredicateFactory.or(asList(predicates));
    }

    @SafeVarargs
    public static <T> Predicate<T> allOf(Predicate<? super T>... predicates) {
        return PrintablePredicateFactory.allOf(asList(predicates));
    }

    @SafeVarargs
    public static <T> Predicate<T> anyOf(Predicate<? super T>... predicates) {
        return PrintablePredicateFactory.anyOf(asList(predicates));
    }

    public static <T> Predicate<T> not(Predicate<T> cond) {
        return PrintablePredicateFactory.not(cond);
    }

    public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> transform(String funcName, Function<O, P> func) {
        return transform(function(funcName, func));
    }

    public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> transform(Function<O, P> function) {
        return PrintablePredicateFactory.transform(function);
    }

    /**
     * // @formatter:off
   * Returns a {@link Predicate} created from a method specified by a {@code methodQuery}.
   * If the {@code methodQuery} matches none or more than one methods, a {@code RuntimeException} will be thrown.
   *
   * The suffix {@code p} stands for "predicate" following the custom in LISP culture
   * and it is necessary to avoid collision with {@link Functions#call( MethodQuery )} method.
   *
   * // @formatter:on
     *
     * @param methodQuery A query object that specifies a method to be invoked by the returned predicate.
     * @param <T>         the type of the input to the returned predicate
     * @return Created predicate.
     * @see Functions#classMethod(Class, String, Object[])
     * @see Functions#instanceMethod(Object, String, Object[])
     * @see Functions#parameter()
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> Predicate<T> callp(MethodQuery methodQuery) {
        return predicate(
                methodQuery.describe(),
                t -> InternalChecks.ensureValue(
                        invokeMethod(methodQuery.bindActualArguments((o) -> o instanceof Parameter, o -> t)),
                        v -> v instanceof Boolean,
                        v -> format("Method matched with '%s' must return a boolean value but it gave: '%s'.", methodQuery.describe(), v)));
    }

    /**
     * // @formatter:off
   * Returns a predicate that calls a method which matches the given {@code methodName}
   * and {@code args} on the object given as input to it.
   *
   * Note that method look up is done when the predicate is applied.
   * This means this method does not throw any exception by itself and in case
   * you give wrong {@code methodName} or {@code arguments}, an exception will be
   * thrown when the returned function is applied.
   * // @formatter:on
     *
     * @param methodName The method name
     * @param arguments  Arguments passed to the method.
     * @param <T>        The type of input to the returned predicate
     * @return A predicate that invokes the method matching the {@code methodName} and {@code args}
     * @see Functions#parameter()
     */
    public static <T> Predicate<T> callp(String methodName, Object... arguments) {
        return callp(Functions.instanceMethod(Functions.parameter(), methodName, arguments));
    }

    enum Def {
        ;

        public static final Function<Class<?>, Predicate<?>> IS_INSTANCE_OF$2 = function(() -> "isInstanceOf", (Class<?> c) -> c::isInstance);
    }

}
