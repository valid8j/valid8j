package com.github.valid8j.pcond.experimentals.currying.context;

import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.Formattable;
import java.util.Formatter;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * `Context` is a concept to handle multiple values in the `pcond`.
 */
public interface CurriedContext extends Formattable {
  /**
   * Returns the number of context values.
   *
   * @return The number of context values.
   */
  default int size() {
    return values().size();
  }

  /**
   * Returns the context value specified by the index.
   *
   * @param i   The index of the context value to be returned.
   * @param <T> Expected type of the returned context value.
   * @return The specified context value.
   */
  @SuppressWarnings("unchecked")
  default <T> T valueAt(int i) {
    return (T) values().get(i);
  }

  /**
   * Creates a new context with an appended value.
   *
   * @param o A value to appended
   * @return A new context with the appended value.
   */
  default CurriedContext append(Object o) {
    return new CurriedContext() {
      @Override
      public List<Object> values() {
        return InternalUtils.append(CurriedContext.this.values(), o);
      }

      @Override
      public String toString() {
        return PrivateUtils.variableBundleToString(this);
      }
    };
  }

  @Override
  default void formatTo(Formatter formatter, int flags, int width, int precision) {
    formatter.format("%s",  PrivateUtils.variableBundleToString(this));
  }

  /**
   * Returns context values.
   *
   * @return context values.
   */
  List<Object> values();

  /**
   * Creates a new context which has the given value as its only context value.
   *
   * @param o The value for which a new context is created.
   * @return A new context.
   */
  static CurriedContext from(Object o) {
    return new CurriedContext() {
      @Override
      public List<Object> values() {
        return singletonList(o);
      }

      @Override
      public String toString() {
        return PrivateUtils.variableBundleToString(this);
      }

    };
  }

  enum PrivateUtils {
    ;

    static String variableBundleToString(CurriedContext curriedContext) {
      return "variables:" + curriedContext.values();
    }
  }
}
