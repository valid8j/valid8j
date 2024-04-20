package com.github.dakusui.valid8j.pcond.internals;

import com.github.dakusui.valid8j.pcond.validator.Explanation;
import com.github.dakusui.valid8j.pcond.validator.Validator;
import com.github.dakusui.valid8j.pcond.core.Evaluable;
import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunction;
import com.github.dakusui.valid8j.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.forms.Printables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public enum InternalUtils {
  ;

  private static final Predicate<?>   DUMMY_PREDICATE = Printables.predicate("DUMMY_PREDICATE:ALWAYSTHROW", v -> {
    throw new UnsupportedOperationException("Testing: '" + v + "' was failed, because this is a dummy predicate.");
  });
  private static final Function<?, ?> DUMMY_FUNCTION  = Printables.function("DUMMY_FUNCTION:ALWAYSTHROW", v -> {
    throw new UnsupportedOperationException("Applying: '" + v + "' was failed, because this is a dummy predicate.");
  });

  public static String formatObject(Object value) {
    return formatObject(value, summarizedStringLength());
  }

  public static String formatObject(Object value, int maxLength) {
    return _formatObject(value, maxLength).replaceAll("[\\r\\n]", " ");
  }

  private static String _formatObject(Object value, int maxLength) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() < 4)
        return format("[%s]",
            collection.stream()
                .map(InternalUtils::formatObject)
                .collect(joining(",")));
      Iterator<?> i = collection.iterator();
      return format("[%s,%s,%s...;%s]",
          formatObject(i.next()),
          formatObject(i.next()),
          formatObject(i.next()),
          collection.size()
      );
    }
    if (value instanceof Object[])
      return formatObject(asList((Object[]) value));
    if (value instanceof Formattable)
      return String.format("%s", value);
    if (value instanceof String) {
      String s = (String) value;
      s = summarizeString(s, maxLength);
      return format("\"%s\"", s);
    }
    if (value instanceof Throwable) {
      Throwable throwable = (Throwable) value;
      String simpleName = summarizeString(throwable.getClass().getSimpleName() + ":", maxLength);
      return simpleName +
          (simpleName.length() < Math.max(12, maxLength) ?
              formatObject(throwable.getMessage(), toNextEven(Math.max(12, maxLength - simpleName.length()))) :
              "");
    }
    if (isToStringOverridden(value))
      return summarizeString(
          value.toString(),
          maxLength + 2 /* 2 for margin for single quotes not necessary for non-strings */
      );
    return value.toString().substring(value.getClass().getPackage().getName().length() + 1);
  }

  public static String explainValue(Object value) {
    StringBuilder b = new StringBuilder();
    if (value instanceof Collection) {
      for (Object each : (Collection<?>) value) {
        explainValue(b, 0, each);
      }
    } else {
      explainValue(b, 0, value);
    }
    return b.toString().trim();
  }

  private static void explainValue(StringBuilder buffer, int level, Object value) {
    if (value instanceof Collection) {
      if (((Collection<?>) value).isEmpty())
        explainValue(buffer, level, "[]");
      else {
        for (Object each : (Collection<?>) value)
          explainValue(buffer, level + 1, each);
      }
    } else {
      buffer.append(String.format("%s%s%n", spaces(level * 2), value));
    }
  }

  private static String spaces(int spaces) {
    if (spaces <= 0)
      return "";
    return String.format("%-" + (spaces) + "s", "");
  }

  private static int toNextEven(int value) {
    if ((value & 1) == 0)
      return value;
    return value + 1;
  }

  private static String summarizeString(String s, int length) {
    assert (length & 1) == 0 : "The length must be an even int, but was <" + length + ">";
    assert length >= 12 : "The length must be greater than or equal to 12. Less than 20 is not recommended. But was <" + length + ">";
    if (s.length() > length) {
      int pre = length / 2 - 2;
      int post = length / 2 - 5;
      s = s.substring(0, length - pre) + "..." + s.substring(s.length() - post);
    }
    return s;
  }

  public static int summarizedStringLength() {
    return Validator.instance().configuration().summarizedStringLength();
  }

  private static boolean isToStringOverridden(Object object) {
    return getMethod(object.getClass(), "toString").getDeclaringClass() != Object.class;
  }

  /**
   * A method to check if assertion is enabled or not.
   *
   * @param v A boolean value to test.
   * @return {@code true} - assertion failed with the given value {@code v} / {@code false} - otherwise.
   */
  public static boolean assertFailsWith(boolean v) {
    boolean ret = false;
    try {
      assert v;
    } catch (AssertionError e) {
      ret = true;
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static <T> T createInstanceFromClassName(Class<? super T> expectedClass, String requestedClassName, Object... args) {
    try {
      Class<?> loadedClass = Class.forName(requestedClassName);
      try {
        return (T) expectedClass.cast(loadedClass.getDeclaredConstructor(Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new)).newInstance(args));
      } catch (ClassCastException e) {
        throw executionFailure("The requested class:'" + requestedClassName +
                "' was found but not an instance of " + expectedClass.getCanonicalName() + ".: " +
                "It was '" + loadedClass.getCanonicalName() + "'.",
            e);
      } catch (NoSuchMethodException e) {
        throw executionFailure("Matching public constructor for " + Arrays.toString(args) + " was not found in " + requestedClassName, e);
      } catch (InvocationTargetException e) {
        throw executionFailure("Matching public constructor was found in " + requestedClassName + " but threw an exception", e.getCause());
      }
    } catch (InstantiationException | IllegalAccessException |
        ClassNotFoundException e) {
      throw executionFailure("The requested class was not found or not accessible.: " + requestedClassName, e);
    }
  }

  public static InternalException executionFailure(String message, Throwable cause) {
    throw executionFailure(Explanation.fromMessage(message), cause);
  }

  public static InternalException executionFailure(Explanation explanation, Throwable cause) {
    throw new InternalException(explanation.toString(), cause);
  }

  public static InternalException wrapIfNecessary(Throwable cause) {
    if (cause instanceof Error)
      throw (Error) cause;
    if (cause instanceof RuntimeException)
      throw (RuntimeException) cause;
    throw executionFailure(cause.getMessage(), cause);
  }

  public static List<? super Object> append(List<? super Object> list, Object p) {
    return unmodifiableList(new ArrayList<Object>(list) {{
      add(p);
    }});
  }

  @SuppressWarnings("unchecked")
  public static <T> Evaluable<T> toEvaluableIfNecessary(Predicate<? super T> p) {
    requireNonNull(p);
    if (p instanceof Evaluable)
      return (Evaluable<T>) p;
    // We know that Printable.predicate returns a PrintablePredicate object, which is an Evaluable.
    return (Evaluable<T>) Printables.predicate(p::toString, p);
  }

  public static <T> Evaluable<T> toEvaluableIfNecessary(Function<? super T, ?> f) {
    return toEvaluableWithFormatterIfNecessary(f, Object::toString);
  }

  @SuppressWarnings("unchecked")
  public static <T> Evaluable<T> toEvaluableWithFormatterIfNecessary(Function<? super T, ?> f, Function<Function<? super T, ?>, String> formatter) {
    requireNonNull(f);
    if (f instanceof Evaluable)
      return (Evaluable<T>) f;
    // We know that Printable.predicate returns a PrintableFunction object, which is an Evaluable.
    return (Evaluable<T>) Printables.function(() -> formatter.apply(f), f);
  }

  public static Class<?> wrapperClassOf(Class<?> clazz) {
    if (clazz == Integer.TYPE)
      return Integer.class;
    if (clazz == Long.TYPE)
      return Long.class;
    if (clazz == Boolean.TYPE)
      return Boolean.class;
    if (clazz == Byte.TYPE)
      return Byte.class;
    if (clazz == Character.TYPE)
      return Character.class;
    if (clazz == Float.TYPE)
      return Float.class;
    if (clazz == Double.TYPE)
      return Double.class;
    if (clazz == Short.TYPE)
      return Short.class;
    if (clazz == Void.TYPE)
      return Void.class;
    throw new IllegalArgumentException("Unsupported type:" + (clazz != null ? clazz.getName() : "null") + " was given.");
  }

  public static Method getMethod(Class<?> aClass, String methodName, Class<?>... parameterTypes) {
    try {
      return aClass.getMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      throw executionFailure(format("Requested method: %s(%s) was not found in %s", methodName, Arrays.stream(parameterTypes).map(Class::getName).collect(joining(",")), aClass.getName()), e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<? super T> dummyPredicate() {
    return (Predicate<? super T>) DUMMY_PREDICATE;
  }

  @SuppressWarnings("unchecked")
  public static <T, R> Function<T, R> dummyFunction() {
    return (Function<T, R>) DUMMY_FUNCTION;
  }

  public static boolean isDummyFunction(Function<?, ?> function) {
    return function == DUMMY_FUNCTION;
  }

  public static Object toNonStringObject(String s) {
    return new Object() {
      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static String indent(int level) {
    return level == 0 ?
        "" :
        format("%" + (level * 2) + "s", "");
  }

  public static String newLine() {
    return format("%n");
  }

  /**
   * Marks "trivial" a given function.
   * A predicate marked trivial will not appear in an execution report.
   *
   * @param predicate A predicate to be marked.
   * @param <T>      Input type of the function.
   * @return A predicate marked trivial.
   */
  public static <T> Predicate<T> makeSquashable(Predicate<T> predicate) {
    return ((PrintablePredicate<T>) predicate).makeTrivial();
  }

  /**
   * Marks "trivial" given function.
   * A function marked trivial will not appear in an execution report.
   *
   * @param function A function to marked.
   * @param <T>      Input type of the function.
   * @param <R>      Output type of the function.
   * @return A function marked trivial.
   */
  public static <T, R> Function<T, R> makeSquashable(Function<T, R> function) {
    return ((PrintableFunction<T, R>) function).makeTrivial();
  }

  public static <T> Function<T, T> trivialIdentityFunction() {
    return Functions.identity();
  }
}
