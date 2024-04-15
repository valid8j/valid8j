package com.github.dakusui.valid8j.pcond.validator;

import com.github.dakusui.valid8j.pcond.validator.exceptions.PostconditionViolationException;
import com.github.dakusui.valid8j.pcond.validator.exceptions.PreconditionViolationException;
import com.github.dakusui.valid8j.pcond.validator.exceptions.ValidationException;

import java.lang.reflect.InvocationTargetException;

import static com.github.dakusui.valid8j.pcond.validator.Explanation.reportToString;

/**
 * An interface to define how an exception is composed based on a given message,
 * a class of a condition that the value violates (non-null constraint, invalid state,
 * input validation), and the context the violation happened (pre-, post-condition check,
 * test-assertion, etc.).
 *
 * This interface has methods to return objects, each of which composes actual exceptions.
 * Each of them intended to group methods that compose exceptions for a single context.
 */
public interface ExceptionComposer {
  /**
   * Returns an instance to compose exceptions used with `requireXyz` methods in
   * {@code Requires} entry-point class of valid8j library.
   *
   * @return An object to compose exceptions for methods in {@code Requires}.
   */
  ForRequire forRequire();

  /**
   * Returns an instance to compose exceptions used with `ensureXyz` methods in
   * {@code Ensures} entry-point class of valid8j library.
   *
   * @return An object to compose exceptions for methods in {@code Ensures}.
   */
  ForEnsure forEnsure();

  /**
   * Returns an instance to compose exceptions used with `validateXyz` methods in
   * {@code Validates} entry-point class of valid8j library.
   *
   * @return An object to compose exceptions for methods in {@code Validates}.
   */
  ForValidate defaultForValidate();

  /**
   * Returns an instance to compose exceptions used in `assert` statements.
   *
   * @return An object to compose exceptions for "precondition" violation.
   */
  ForAssertion forAssert();

  /**
   * Returns an instance to compose exceptions used in `assertThat` and `assumeThat`
   * methods in {@code TestAssertions} entry-point class of thincrest library.
   * Other entry-point classes provided for use cases of the `pcond` library as
   * a test assertion library may also use this method.
   *
   * @return An object to compose exceptions for test assertions
   */
  ForTestAssertion forAssertThat();

  /**
   * An implementation of the {@link ExceptionComposer} interface.
   * You usually do not need to extend this method to customize its behavior.
   * Rather you only need to control the arguments passed to its constructor
   * through {@link Validator.Configuration.Builder}.
   *
   * @see Validator.Configuration
   * @see Validator.Configuration.Builder
   */
  class Impl implements ExceptionComposer {
    final private ForRequire       forRequire;
    final private ForEnsure        forEnsure;
    final private ForValidate      defaultForValidate;
    final private ForAssertion     forAssert;
    final private ForTestAssertion forAssertThat;

    public Impl(ForRequire forRequire, ForEnsure forEnsure, ForValidate defaultForValidate, ForAssertion forAssert, ForTestAssertion forAssertThat) {
      this.forRequire = forRequire;
      this.forEnsure = forEnsure;
      this.defaultForValidate = defaultForValidate;
      this.forAssert = forAssert;
      this.forAssertThat = forAssertThat;
    }

    @Override
    public ForRequire forRequire() {
      return this.forRequire;
    }

    @Override
    public ForEnsure forEnsure() {
      return this.forEnsure;
    }

    @Override
    public ForValidate defaultForValidate() {
      return this.defaultForValidate;
    }

    @Override
    public ForAssertion forAssert() {
      return this.forAssert;
    }

    @Override
    public ForTestAssertion forAssertThat() {
      return this.forAssertThat;
    }
  }

  /**
   * An interface that defines common methods that an object returned by each method
   * in the {@link ExceptionComposer} has.
   */
  interface Base {
    /**
     * A method to compose an exception for a "Null-violation".
     * When you are checking a "pre-condition", if the value must not be `null`,
     * you may prefer a {@link NullPointerException}, rather than an exception
     * such as `YourPreconditionException` as Google Guava's `Precondition` does
     * so.
     *
     * This method by default, returns a `NullPointerException`.
     *
     * In case you prefer to throw `YourPreconditionException` for the sake of uniformity,
     * you can override this method for an object returned by {@link ExceptionComposer#forRequire()}
     *
     * For more detail, please refer to {@link Validator.Configuration}.
     *
     * @param message A message attached to the composed exception.
     * @return A composed exception.
     */
    default Throwable exceptionForNonNullViolation(String message) {
      return new NullPointerException(message);
    }

    /**
     * A method to compose an exception for a "State-violation".
     *
     * @param message A message attached to the composed exception.
     * @return A composed exception.
     * @see Base#exceptionForNonNullViolation(String)
     */
    default Throwable exceptionForIllegalState(String message) {
      return new IllegalStateException(message);
    }

    /**
     * A method to compose an exception for a general violation.
     * An extension-point to customize the exception to be thrown for a certain
     * context.
     *
     * @param message A message attached to the composed exception.
     * @return A composed exception.
     */
    Throwable exceptionForGeneralViolation(String message);
  }

  interface ForRequire extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new PreconditionViolationException(message);
    }

    Throwable exceptionForIllegalArgument(String message);

    @SuppressWarnings("unused") // Referenced reflectively
    class Default implements ForRequire {
      @Override
      public Throwable exceptionForIllegalArgument(String message) {
        return new IllegalArgumentException(message);
      }
    }
  }

  interface ForEnsure extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new PostconditionViolationException(message);
    }

    @SuppressWarnings("unused") // Referenced reflectively
    class Default implements ForEnsure {
    }
  }

  interface ForValidate extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new ValidationException(message);
    }

    default Throwable exceptionForIllegalArgument(String message) {
      return new IllegalArgumentException(message);
    }

    @SuppressWarnings("unused") // Referenced reflectively
    class Default implements ForValidate {
    }
  }

  interface ForAssertion {
    default Throwable exceptionPreconditionViolation(String message) {
      return new AssertionError(message);
    }

    default Throwable exceptionInvariantConditionViolation(String message) {
      return new AssertionError(message);
    }

    default Throwable exceptionPostconditionViolation(String message) {
      return new AssertionError(message);
    }

    @SuppressWarnings("unused") // Referenced reflectively
    class Default implements ForAssertion {
    }
  }

  interface ForTestAssertion {
    <T extends RuntimeException> T testSkippedException(String message, ReportComposer reportComposer);

    default <T extends RuntimeException> T testSkippedException(Explanation explanation, ReportComposer reportComposer) {
      return testSkippedException(explanation.toString(), reportComposer);
    }

    <T extends Error> T testFailedException(Explanation explanation, ReportComposer reportComposer);

    @SuppressWarnings("unused") // Referenced reflectively
    class JUnit4 implements ForTestAssertion {
      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message, ReportComposer reportComposer) {
        throw (T) Utils.createException(
            "org.junit.AssumptionViolatedException",
            reportComposer.explanationFromMessage(message),
            (c, exp) -> c.getConstructor(String.class).newInstance(exp.message()));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Error> T testFailedException(Explanation explanation, ReportComposer reportComposer) {
        throw (T) Utils.createException(
            "org.junit.ComparisonFailure",
            explanation,
            (c, exp) -> c.getConstructor(String.class, String.class, String.class).newInstance(exp.message(), reportToString(exp.expected()), reportToString(exp.actual())));
      }
    }

    @SuppressWarnings("unused") // Referenced reflectively
    class Opentest4J implements ForTestAssertion {
      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message, ReportComposer reportComposer) {
        throw (T) Utils.createException("org.opentest4j.TestSkippedException", reportComposer.explanationFromMessage(message), (c, exp) ->
            c.getConstructor(String.class).newInstance(exp.message()));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Error> T testFailedException(Explanation explanation, ReportComposer reportComposer) {
        throw (T) Utils.createException("org.opentest4j.AssertionFailedError", explanation, (c, exp) ->
            c.getConstructor(String.class, Object.class, Object.class).newInstance(exp.message(), reportToString(exp.expected()), reportToString(exp.actual())));
      }
    }
  }

  enum Utils {
    ;

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T createException(String className, Explanation explanation, ReflectiveExceptionFactory<T> reflectiveExceptionFactory) {
      try {
        return reflectiveExceptionFactory.apply((Class<T>) Class.forName(className), explanation);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + className + "' (NOT FOUND)", e);
      }
    }

    @FunctionalInterface
    public
    interface ReflectiveExceptionFactory<T extends Throwable> {
      T create(Class<T> c, Explanation explanation) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

      default T apply(Class<T> c, Explanation explanation) {
        try {
          return create(c, explanation);
        } catch (InvocationTargetException | InstantiationException |
            IllegalAccessException | NoSuchMethodException e) {
          throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + c.getCanonicalName() + "'", e);
        }
      }
    }
  }
}
