package com.github.valid8j.pcond.fluent;

import com.github.valid8j.pcond.validator.ReportComposer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A list that are not printed in a report created by {@link ReportComposer}.
 * An instance of this class should be used together with a statement created by {@link Statement#createPredicateForAllOf(Statement[])} method.
 *
 * @param <E> Type of elements in this list.
 * @see ReportComposer
 * @see Statement#createPredicateForAllOf(Statement[])
 */
public class ListHolder<E> extends ArrayList<E> implements ValueHolder {
  private ListHolder(Collection<E> collection) {
    this.addAll(collection);
  }

  /**
   * A method to create an instance of this class.
   *
   * @param list A list wrapped by the returned object.
   * @return An instance of {@link ListHolder} that wraps a given list.
   * @param <E> Type of elements in the passed and returned list.
   */
  public static <E> List<E> fromList(List<E> list) {
    return new ListHolder<>(list);
  }
}
