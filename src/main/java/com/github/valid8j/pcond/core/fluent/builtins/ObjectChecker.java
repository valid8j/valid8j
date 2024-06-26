package com.github.valid8j.pcond.core.fluent.builtins;

import com.github.valid8j.pcond.core.fluent.AbstractObjectChecker;
import com.github.valid8j.pcond.internals.InternalUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 */
public interface ObjectChecker<
    OIN,
    E> extends
    AbstractObjectChecker<
                    ObjectChecker<OIN, E>,
                    OIN,
                    E> {
  class Impl<
      OIN,
      E> extends
      Base<
          ObjectChecker<OIN, E>,
          OIN,
          E> implements
      ObjectChecker<OIN, E> {
    public Impl(Supplier<OIN> baseValue, Function<OIN, E> root) {
      super(baseValue, root);
    }

    @Override
    protected ObjectChecker<E, E> rebase() {
      return new Impl<>(this::value, InternalUtils.trivialIdentityFunction());
    }
  }
}
