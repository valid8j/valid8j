package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.core.fluent.AbstractObjectTransformer;
import com.github.valid8j.pcond.forms.Functions;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;


/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 *
 * @param <T> The type of the target value of the instance of this interface.
 * @param <E> The type of the target value of the checker instance.
 *            In most cases, the same type as `T` is chosen.
 */
public interface ObjectTransformer<
    T,
    E
    > extends
        AbstractObjectTransformer<
                    ObjectTransformer<T, E>,
                ObjectChecker<T, E>,
                    T,
                    E> {

  @SuppressWarnings("unchecked")
  default ObjectTransformer<T, E> transform(Function<ObjectTransformer<T, E>, Predicate<E>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((ObjectTransformer<T, E>) tx));
  }

  /**
   * Lets the framework know the value to be checked is a `String`.
   * This method may throw an exception, if the value is not a `String`.
   *
   * @return A {@link StringTransformer <T>} object.
   */
  default StringTransformer<T> asString() {
    return toString(Functions.cast(String.class));
  }

  /**
   * Lets the framework know the value to be checked is a `Integer`.
   * This method may throw an exception, if the value is not a `Integer`.
   *
   * @return A {@link IntegerTransformer <T>} object.
   */
  default IntegerTransformer<T> asInteger() {
    return toInteger(Functions.cast(Integer.class));
  }

  /**
   * Lets the framework know the value to be checked is a `Long`.
   * This method may throw an exception, if the value is not a `Long`.
   *
   * @return A {@link LongTransformer <T>} object.
   */
  default LongTransformer<T> asLong() {
    return toLong(Functions.cast(Long.class));
  }

  /**
   * Lets the framework know the value to be checked is a `Short`.
   * This method may throw an exception, if the value is not a `Short`.
   *
   * @return A {@link ShortTransformer <T>} object.
   */
  default ShortTransformer<T> asShort() {
    return toShort(Functions.cast(Short.class));
  }

  /**
   * Lets the framework know the value to be checked is a `Double`.
   * This method may throw an exception, if the value is not a `Double`.
   *
   * @return A {@link DoubleTransformer <T>} object.
   */
  default DoubleTransformer<T> asDouble() {
    return toDouble(Functions.cast(Double.class));
  }

  /**
   * Lets the framework know the value to be checked is a `Float`.
   * This method may throw an exception, if the value is not a `Float`.
   *
   * @return A {@link FloatTransformer <T>} object.
   */
  default FloatTransformer<T> asFloat() {
    return toFloat(Functions.cast(Float.class));
  }

  /**
   * Lets the framework know the value to be checked is a `Boolean`.
   * This method may throw an exception, if the value is not a `Boolean`.
   *
   * @return A {@link BooleanTransformer <T>} object.
   */
  default BooleanTransformer<T> asBoolean() {
    /*
     * Lets the framework know the value to be checked is a `Boolean`.
     * This method may throw an exception, if the value is not a `Boolean`.
     *
     * @return A {@link BooleanTransformer<T>} object.
     */
    return toBoolean(Functions.cast(Boolean.class));
  }

  /**
   * Lets the framework know the value to be checked is a `List` of `EE`.
   * This method may throw an exception, if the value is not a `List`.
   *
   * @return A {@link ListTransformer <EE>} object.
   */
  @SuppressWarnings("unchecked")
  default <EE> ListTransformer<T, EE> asListOf(Class<EE> type) {
    return toList(Printables.function(format("castTo[List<%s>]", type.getSimpleName()), v -> (List<EE>) v));
  }

  /**
   * Lets the framework know the value to be checked is a `List`.
   * This method may throw an exception, if the value is not a `List`.
   *
   * @return A {@link ListTransformer <T>} object.
   */
  @SuppressWarnings("unchecked")
  default <EE> ListTransformer<T, EE> asList() {
    return (ListTransformer<T, EE>) asListOf(Object.class);
  }

  /**
   * Creates a new Object transformer whose target value is given by a supplier.
   *
   * @param value A supplier of the target value.
   * @param <E>   The type of the target value.
   * @return Created transformer.
   */
  static <E> ObjectTransformer<E, E> create(Supplier<E> value) {
    return new Impl<>(value, InternalUtils.trivialIdentityFunction());
  }

  class Impl<
      T,
      E> extends
      Base<
          ObjectTransformer<T, E>,
          ObjectChecker<T, E>,
          T,
          E> implements
      ObjectTransformer<T, E> {
    public Impl(Supplier<T> rootValue, Function<T, E> root) {
      super(rootValue, root);
    }

    @Override
    protected ObjectChecker<T, E> toChecker(Function<T, E> transformFunction) {
      return new ObjectChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected ObjectTransformer<E, E> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }

  }
}
