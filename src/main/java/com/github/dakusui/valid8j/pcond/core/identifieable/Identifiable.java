package com.github.dakusui.valid8j.pcond.core.identifieable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * An interface that represents an object which can be identified based on objects
 * used on instantiation without overriding the {@link Object#hashCode} and {@link Object#equals(Object)}.
 * In order to minimize effort to implement this interface, you can extend
 * {@link Base} class.
 */
public interface Identifiable {
  static Optional<Object> creatorOf(Object object) {
    Optional<Object> ret = Optional.empty();
    if (object instanceof Identifiable)
      ret = Optional.of(((Identifiable) object).creator());
    return ret;
  }

  static List<Object> argsOf(Object object) {
    List<Object> ret = singletonList(object);
    if (object instanceof Identifiable)
      ret = ((Identifiable) object).args();
    return ret;
  }

  static <T> String formatObjectName(Object object) {
    return "noname:" + (object == null ? "null" : object.toString());
  }

  Object creator();

  /**
   * This method is designed to be called from the {@code hashCode()} method of a class
   * which implements {@link Identifiable} identity interface.
   *
   * @return A proper value to be returned from the {@link Object#hashCode()} method.
   */
  default int defaultHashCode() {
    return identityObject().hashCode();
  }

  /**
   * This method is designed to be called from the {@code defaultEquals()} method of a class
   * which implements {@link Identifiable} identity interface.
   *
   * @return A proper value to be returned from the {@link Object#equals(Object)} method.
   */
  default boolean defaultEquals(Object anotherObject) {
    if (this == anotherObject)
      return true;
    if (!this.getClass().isInstance(anotherObject))
      return false;
    return Objects.equals(this.identityObject(), ((Identifiable) anotherObject).identityObject());
  }

  default Object createIdentity() {
    return Stream.concat(
        Stream.of(creator()),
        args().stream()).collect(toList());
  }

  /**
   * Typically, implementation of this method should return a final field value to which the
   * value returned by {@link Identifiable#createIdentity()} method is assigned in the constructor.
   *
   * @return The identity object.
   */
  Object identityObject();

  List<Object> args();

  class Base implements Identifiable {
    private final Object       creator;
    private final List<Object> args;
    private final Object       identity;

    protected Base(Object creator, List<Object> args) {
      this.creator = Objects.requireNonNull(creator);
      this.args = Objects.requireNonNull(args);
      this.identity = createIdentity();
    }

    @Override
    public Object identityObject() {
      return this.identity;
    }

    @Override
    public Object creator() {
      return this.creator;
    }

    @Override
    public List<Object> args() {
      return args;
    }

    @Override
    public int hashCode() {
      return defaultHashCode();
    }

    // Done in a method to which the operation is delegated.
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
      return defaultEquals(obj);
    }
  }
}
