package com.github.dakusui.valid8j.pcond.core.fluent;

import com.github.dakusui.valid8j.pcond.core.fluent.builtins.ObjectChecker;
import com.github.dakusui.valid8j.pcond.forms.Functions;
import com.github.dakusui.valid8j.pcond.internals.InternalException;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * A base class for a custom transformer.
 *
 * [source,java]
 * .Example Transformer
 * ----
 * public static class BookTransformer extends CustomTransformer<BookTransformer, Book> {
 *   public BookTransformer(Book rootValue) {
 *     super(rootValue);
 *   }
 *
 *   public StringTransformer<Book> title() {
 *     return toString(Printables.function("title", Book::title));
 *   }
 *
 *   public StringTransformer<Book> abstractText() {
 *     return toString(Printables.function("abstractText", Book::abstractText));
 *   }
 * }
 * ----
 *
 * In the example above, the custom transformer class `BookTransformer`, which targets `Book` type value is specified for `TX`,
 * while the `Book` is specified for `T`.
 *
 * @param <TX> This class
 * @param <T> Type of the class that is targeted by the transformer.
 */
public abstract class CustomTransformer<
    TX extends AbstractObjectTransformer<
                TX,
        ObjectChecker<T, T>,
                T,
                T>,
    T> extends
    Transformer.Base<
        TX,
        ObjectChecker<T, T>,
        T,
        T> implements
    AbstractObjectTransformer<
            TX,
        ObjectChecker<T, T>,
            T,
            T> {
  /**
   * Creates an instance of this class.
   *
   * @param baseValue The target value of this transformer.
   */
  public CustomTransformer(T baseValue) {
    super(() -> baseValue, Functions.identity());
  }

  /**
   * A method to be used for internal purposes.
   *
   * @return this object.
   */
  @Override
  protected TX rebase() {
    return create(this.value());
  }

  @Override
  protected ObjectChecker<T, T> toChecker(Function<T, T> transformFunction) {
    return new ObjectChecker.Impl<>(this::value, transformFunction);
  }

  @SuppressWarnings("unchecked")
  public TX transform(Function<TX, Predicate<T>> clause) {
    requireNonNull(clause);
    return this.addTransformAndCheckClause(tx -> clause.apply((TX) tx));
  }

  @SuppressWarnings("unchecked")
  protected TX create(T value) {
    try {
      return (TX) this.getClass().getConstructor(value.getClass()).newInstance(value);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new InternalException(String.format("Failed to create an instance of this class: <%s>", this.getClass()), e);
    }
  }
}
