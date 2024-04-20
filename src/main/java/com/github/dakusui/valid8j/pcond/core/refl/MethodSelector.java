package com.github.dakusui.valid8j.pcond.core.refl;

import com.github.dakusui.valid8j.pcond.internals.InternalUtils;
import com.github.dakusui.valid8j.pcond.internals.InternalChecks;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

import static com.github.dakusui.valid8j.pcond.internals.InternalChecks.requireArgument;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * //@formater:off
 * An interface representing an object that selects {@link Method}s from given ones.
 *
 * This interface is used to choose methods that are appropriate to invoke with
 * given arguments.
 * //@formater:on
 */
public interface MethodSelector extends Formattable {
  /**
   * Selects methods that can be invoked with given {@code args}.
   *
   * @param methods Methods from which returned methods are selected.
   * @param args    Arguments to be passed to selected methods.
   * @return Selected methods.
   */
  List<Method> select(List<Method> methods, Object[] args);

  /**
   * Returns a string that describes this object.
   *
   * @return A description of this object
   */
  String describe();

  /**
   * Returns a composed {@link MethodSelector} that first applies this and
   * then applies {@code another}.
   *
   * @param another The method selector to apply after this.
   * @return The composed method selector
   */
  default MethodSelector andThen(MethodSelector another) {
    return new MethodSelector() {
      @Override
      public List<Method> select(List<Method> methods, Object[] args) {
        return another.select(MethodSelector.this.select(methods, args), args);
      }

      @Override
      public String describe() {
        return format("%s&&%s", MethodSelector.this.describe(), another.describe());
      }
    };
  }

  /**
   * Formats this object using the {@link MethodSelector#describe()} method.
   */
  @Override
  default void formatTo(Formatter formatter, int flags, int width, int precision) {
    formatter.format("%s", this.describe());
  }

  class Default implements MethodSelector {
    @Override
    public List<Method> select(List<Method> methods, Object[] args) {
      return methods
          .stream()
          .filter(m -> areArgsCompatible(m.getParameterTypes(), args))
          .collect(toList());
    }

    @Override
    public String describe() {
      return "default";
    }

    private static boolean areArgsCompatible(Class<?>[] formalParameters, Object[] args) {
      if (formalParameters.length != args.length)
        return false;
      for (int i = 0; i < args.length; i++) {
        if (args[i] == null)
          if (formalParameters[i].isPrimitive())
            return false;
          else
            continue;
        if (!Utils.isAssignableWithBoxingFrom(formalParameters[i], Utils.toClass(args[i])))
          return false;
      }
      return true;
    }
  }

  /**
   * A method selector that selects "narrower" methods over "wider" ones.
   */
  class PreferNarrower implements MethodSelector {
    @Override
    public List<Method> select(List<Method> methods, Object[] args) {
      if (methods.size() < 2)
        return methods;
      List<Method> ret = new LinkedList<>();
      for (Method i : methods) {
        if (methods.stream().filter(j -> j != i).noneMatch(j -> compareNarrowness(j, i) > 0))
          ret.add(i);
      }
      return ret;
    }

    @Override
    public String describe() {
      return "preferNarrower";
    }

    /**
     * If {@code a} is 'narrower' than {@code b}, positive integer will be returned.
     * If {@code b} is 'narrower' than {@code a}, negative integer will be returned.
     * Otherwise {@code zero}.
     *
     * 'Narrower' means that every parameter of {@code a} is assignable to corresponding
     * one of {@code b}, but any of {@code b} cannot be assigned to {@code a}'s
     * corresponding parameter.
     *
     * @param a A method.
     * @param b A method to be compared with {@code a}.
     * @return a negative integer, zero, or a positive integer as method {@code a}
     * is less compatible than, as compatible as, or more compatible than
     * the method {@code b} object.
     */
    private static int compareNarrowness(Method a, Method b) {
      if (isCompatibleWith(a, b) && isCompatibleWith(b, a))
        return 0;
      if (!isCompatibleWith(a, b) && !isCompatibleWith(b, a))
        return 0;
      return isCompatibleWith(a, b)
          ? -1
          : 1;
    }

    private static boolean isCompatibleWith(Method a, Method b) {
      requireSameParameterCounts(a, b);
      if (Objects.equals(a, b))
        return true;
      return IntStream
          .range(0, a.getParameterCount())
          .allMatch(i -> Utils.isAssignableWithBoxingFrom(a.getParameterTypes()[i], b.getParameterTypes()[i]));
    }

    private static void requireSameParameterCounts(Method a, Method b) {
      requireArgument(
          requireNonNull(a),
          (Method v) -> v.getParameterCount() == requireNonNull(b).getParameterCount(),
          () -> format("Parameter counts are different: a: %s, b: %s", a, b));
    }
  }

  class PreferExact implements MethodSelector {
    @Override
    public List<Method> select(List<Method> methods, Object[] args) {
      if (methods.size() < 2)
        return methods;
      List<Method> work = methods;
      for (Object ignored : args) {
        List<Method> tmp = new ArrayList<>(work);
        if (!tmp.isEmpty()) {
          work = tmp;
          break;
        }
      }
      return work;
    }

    @Override
    public String describe() {
      return "preferExact";
    }
  }

  enum Utils {
    ;

    static boolean isAssignableWithBoxingFrom(Class<?> a, Class<?> b) {
      if (a.isAssignableFrom(b))
        return true;
      if (InternalChecks.isPrimitiveWrapperClassOrPrimitive(a) && InternalChecks.isPrimitiveWrapperClassOrPrimitive(b))
        return InternalChecks.isWiderThanOrEqualTo(toWrapperIfPrimitive(a), toWrapperIfPrimitive(b));
      return false;
    }

    private static Class<?> toWrapperIfPrimitive(Class<?> in) {
      if (in.isPrimitive())
        return InternalUtils.wrapperClassOf(in);
      return in;
    }

    private static Class<?> toClass(Object value) {
      return value.getClass();
    }
  }
}
