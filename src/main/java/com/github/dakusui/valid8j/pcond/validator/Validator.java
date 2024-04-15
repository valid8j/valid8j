package com.github.dakusui.valid8j.pcond.validator;

import com.github.dakusui.valid8j.pcond.core.*;
import com.github.dakusui.valid8j.pcond.forms.Predicates;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.valid8j.pcond.internals.InternalUtils.toEvaluableIfNecessary;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static com.github.dakusui.valid8j.pcond.validator.Validator.Configuration.Utils.instantiate;
import static com.github.dakusui.valid8j.pcond.validator.Validator.Configuration.Utils.loadPcondProperties;

/**
 * An interface of a policy for behaviours on 'contract violations'.
 */
public interface Validator {
  /**
   * A constant field that holds the default provider instance.
   */
  ThreadLocal<Validator> INSTANCE = ThreadLocal.withInitial(() -> create(loadPcondProperties()));


  /**
   * Returns a configuration object that determines behaviors of this object.
   *
   * @return A configuration object.
   */
  Configuration configuration();

  /**
   * Returns a provider instance created from a given `Properties` object.
   * This method reads the value for the FQCN of this class (`com.github.dakusui.valid8j.pcond.provider.AssertionProvider`) and creates an instance of a class specified by the value.
   * If the value is not set, this value instantiates an object of `DefaultAssertionProvider` and returns it.
   *
   * @param properties A {@code Properties} object from which an {@code AssertionProvider} is created
   * @return Created provider instance.
   */
  static Validator create(Properties properties) {
    return new Impl(configurationFromProperties(properties));
  }

  /**
   * Checks a value if it is {@code null} or not.
   * If it is not a {@code null}, this method returns the given value itself.
   *
   * @param value The given value.
   * @param <T>   The type of the value.
   * @return The {@code value}.
   */
  default <T> T requireNonNull(T value) {
    return require(value, Predicates.isNotNull(), configuration().exceptionComposer().forRequire()::exceptionForNonNullViolation);
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T requireArgument(T value, Predicate<? super T> cond) {
    return require(value, cond, configuration().exceptionComposer().forRequire()::exceptionForIllegalArgument);
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T requireState(T value, Predicate<? super T> cond) {
    return require(value, cond, configuration().exceptionComposer().forRequire()::exceptionForIllegalState);
  }

  /**
   * A method to check if a given `value` satisfies a precondition given as `cond`.
   * If the `cond` is satisfied, the `value` itself will be returned.
   * Otherwise, an exception returned by `configuration().exceptionComposer().forRequire().exceptionForGeneralViolation(String)`
   * will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check if `value` satisfies.
   * @param <T>   The of the `value`.
   * @return The `value`, if `cond` is satisfied.
   */
  default <T> T require(T value, Predicate<? super T> cond) {
    return require(value, cond, msg -> configuration().exceptionComposer().forRequire().exceptionForGeneralViolation(msg));
  }

  /**
   * A method to check if a given `value` satisfies a precondition given as `cond`.
   * If the `cond` is satisfied, the `value` itself will be returned.
   * Otherwise, an exception created by `exceptionFactory` is thrown.
   *
   * @param value            A value to be checked.
   * @param cond             A condition to check if `value` satisfies.
   * @param exceptionFactory A function to create an exception thrown when `cond`
   *                         is not satisfied by `value`.
   * @param <T>              The of the `value`.
   * @return The `value`, if `cond` is satisfied.
   */
  default <T> T require(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionFactory) {
    return checkValueAndThrowIfFails(
        value,
        cond,
        this.configuration().messageComposer()::composeMessageForPrecondition,
        explanation -> exceptionFactory.apply(explanation.toString()));
  }

  /**
   * Validates the given `value`.
   * If the value satisfies a condition `cond`, the value itself will be returned.
   * Otherwise, an exception created by `forValidate.exceptionForGeneralViolation()`
   * will be thrown.
   * This method is intended to be used by {@code Validates#validate(Object, Predicate, Function)}
   * method in `valid8j` library.
   *
   * @param value       The value to be checked.
   * @param cond        A condition to validate the `value`.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validate(T value, Predicate<? super T> cond, ExceptionComposer.ForValidate forValidate) {
    return validate(value, cond, forValidate::exceptionForGeneralViolation);
  }

  /**
   * Validates the given `value`.
   * If the value is not `null`, the value itself will be returned.
   * Otherwise, an exception created by `forValidate.exceptionForGeneralViolation()`
   * will be thrown.
   * This method is intended to be used by {@code Validates#validateNonNull(Object)}
   * method in valid8j library.
   *
   * @param value       The value to be checked.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validateNonNull(T value, ExceptionComposer.ForValidate forValidate) {
    return validate(value, Predicates.isNotNull(), forValidate::exceptionForNonNullViolation);
  }

  /**
   * Validates the given argument variable `value`.
   * If the value satisfies a condition `cond` for checking an argument variable, the value itself will be returned.
   * Otherwise, an exception created by `forValidate.exceptionForIllegalArgument()`
   * will be thrown.
   * This method is intended to be used by {@code Validates#validateArgument(Object, Predicate)}
   * method in `valid8j` library.
   *
   * @param value       The value to be checked.
   * @param cond        A condition to validate the `value`.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validateArgument(T value, Predicate<? super T> cond, ExceptionComposer.ForValidate forValidate) {
    return validate(value, cond, forValidate::exceptionForIllegalArgument);
  }

  /**
   * Validates the given state variable `value`.
   * If the value satisfies a condition `cond` for checking a state, the value itself will be returned.
   * Otherwise, an exception created by `forValidate.exceptionForIllegalState()`
   * will be thrown.
   * This method is intended to be used by {@code Validates#validateState(Object, Predicate)}
   * method in valid8j library.
   *
   * @param value       The value to be checked.
   * @param cond        A condition to validate the `value`.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validateState(T value, Predicate<? super T> cond, ExceptionComposer.ForValidate forValidate) {
    return validate(value, cond, forValidate::exceptionForIllegalState);
  }

  /**
   * Validates the given variable `value`.
   * If the value satisfies a condition `cond`, the value itself will be returned.
   * Otherwise, an exception created by `exceptionFactory` will be thrown.
   * This method is intended to be used by {@code Validates#validate(Object, Predicate, Function)}
   * method in valid8j library.
   *
   * @param value            The value to be checked.
   * @param cond             A condition to validate the `value`.
   * @param exceptionFactory A function to create an exception when the `cond` is not satisfied.
   * @param <T>              The type of the value.
   * @return The value itself.
   */
  default <T> T validate(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionFactory) {
    return validate_2(value, cond, explanation -> exceptionFactory.apply(explanation.toString()));
  }

  default <T> T validate_2(T value, Predicate<? super T> cond, ExceptionFactory<Throwable> exceptionFactory) {
    return checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForValidation,
        exceptionFactory);
  }

  /**
   * Checks a value if it is not `null`.
   * If it is not `null`, the value itself will be returned.
   * If it is, an exception created by `configuration().exceptionComposer().forEnsure().exceptionForNonNullViolation()` will be thrown.
   * This method is intended for ensuring a "post-condition".
   *
   * @param value The value to be checked.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T ensureNonNull(T value) {
    return ensure(value, Predicates.isNotNull(), configuration().exceptionComposer().forEnsure()::exceptionForNonNullViolation);
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   * If it does not, an exception created by `configuration().exceptionComposer().forEnsure().exceptionForIllegalState()` will be thrown.
   * This method is intended for ensuring a "post-condition" of a state.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return ensure(value, cond, configuration().exceptionComposer().forEnsure()::exceptionForIllegalState);
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   * If it does not, an exception created by `configuration().exceptionComposer().forEnsure().exceptionForGeneralViolation()` will be thrown.
   * This method is intended for ensuring a "post-condition".
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T ensure(T value, Predicate<? super T> cond) {
    return ensure(value, cond, msg -> configuration().exceptionComposer().forEnsure().exceptionForGeneralViolation(msg));
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   * If it does not, an exception created by `exceptionComposer` will be thrown.
   * This method is intended for ensuring a "post-condition".
   *
   * @param value             The value to be checked.
   * @param cond              The requirement to check the {@code value}.
   * @param exceptionComposer A function to create an exception to be thrown when
   *                          `cond` is not met.
   * @param <T>               The type of the value.
   * @return The value.
   */
  default <T> T ensure(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionComposer) {
    return checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForPostcondition,
        explanation -> exceptionComposer.apply(explanation.toString()));
  }

  /**
   * A method to check if a `value` satisfies a predicate `cond`.
   *
   * This method is intended to be used by {@code Assertions#that(Object, Predicate)} in valid8j library.
   * If the condition is not satisfied, an exception created by `this.exceptionComposer().forAssert().exceptionInvariantConditionViolation()`
   * method will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check the `value`.
   * @param <T>   The type of `value`.
   */
  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForAssertion,
        explanation -> configuration().exceptionComposer().forAssert().exceptionInvariantConditionViolation(explanation.toString()));
  }

  /**
   * A method to check if a `value` satisfies a predicate `cond`.
   *
   * This method is intended to be used by {@code Assertions#precondition(Object, Predicate)} in valid8j library.
   * If the condition is not satisfied, an exception created by `this.exceptionComposer().forAssert().exceptionPreconditionViolation()`
   * method will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check the `value`.
   * @param <T>   The type of `value`.
   */
  default <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForPrecondition,
        explanation -> configuration().exceptionComposer().forAssert().exceptionPreconditionViolation(explanation.toString()));
  }

  /**
   * A method to check if a `value` satisfies a predicate `cond`.
   *
   * This method is intended to be used by {@code Assertions#postcondition(Object, Predicate)} in valid8j library.
   * If the condition is not satisfied, an exception created by `this.exceptionComposer().forAssert().exceptionPostconditionViolation()`
   * method will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check the `value`.
   * @param <T>   The type of `value`.
   */
  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForPostcondition,
        explanation -> configuration().exceptionComposer().forAssert().exceptionPostconditionViolation(explanation.toString()));
  }

  /**
   * Executes a test assertion for a given `value` using a predicate `cond`.
   * If the `cond` is not satisfied by the `value`, an exception created by `configuration().messageComposer().composeMessageForAssertion()`
   * will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A predicate to check a given `value`.
   * @param <T>   The type of the `value`.
   */
  default <T> void assertThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForAssertion,
        explanation -> configuration().exceptionComposer().forAssertThat().testFailedException(explanation, configuration().reportComposer()));
  }

  /**
   * Executes a test assumption check for a given `value` using a predicate `cond`.
   * If the `cond` is not satisfied by the `value`, an exception created by `configuration().messageComposer().composeMessageForAssertion()`
   * will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A predicate to check a given `value`.
   * @param <T>   The type of the `value`.
   */
  default <T> void assumeThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForAssertion,
        explanation -> configuration().exceptionComposer().forAssertThat().testSkippedException(explanation, configuration().reportComposer()));
  }

  /**
   * The core method of the `ValueChecker`.
   * This method checks if the given `evaluationContext` satisfies a condition, passed as `cond`.
   * If it does, the `evaluationContext` itself will be returned.
   * If not, an appropriate message will be composed based on the `evaluationContext` and `cond` by the `messageComposerFunction`.
   * Internally in this method, an `Explanation` of the failure is created by a {@link ReportComposer}
   * object returned by `configuration().reportComposer()` method.
   * The `Explanation` is passed to the `exceptionComposerFunction` and the exception
   * created by the function will be thrown.
   *
   * @param <T>                       The type of the `evaluationContext`.
   * @param value                     A value to be checked.
   * @param cond                      A predicate that checks the `evaluationContext`.
   * @param messageComposerFunction   A function that composes an error message from the `evaluationContext` and the predicate `cond`.
   * @param exceptionComposerFunction A function that creates an exception from a failure report created inside this method.
   * @return The `evaluationContext` itself.
   */
  @SuppressWarnings("unchecked")
  default <T> T checkValueAndThrowIfFails(
      T value,
      Predicate<? super T> cond,
      BiFunction<T, Predicate<? super T>, String> messageComposerFunction,
      ExceptionFactory<Throwable> exceptionComposerFunction) {
    ValueHolder<T> valueHolder = ValueHolder.forValue(value);
    Evaluable<T> evaluable = toEvaluableIfNecessary(cond);
    EvaluableIo<T, Evaluable<T>, Boolean> evaluableIo = new EvaluableIo<>(valueHolder, EvaluationContext.resolveEvaluationEntryType(evaluable), evaluable);
    EvaluationContext<T> evaluationContext = new EvaluationContext<>();
    if (this.configuration().useEvaluator() && cond instanceof Evaluable) {
      Evaluator evaluator = Evaluator.create();
      ((Evaluable<T>) cond).accept(evaluableIo, evaluationContext, evaluator);
      if (evaluableIo.output().isValueReturned() && Objects.equals(true, evaluableIo.output().value()))
        return value;
      List<EvaluationEntry> entries = evaluationContext.resultEntries();
      throw exceptionComposerFunction.create(configuration()
          .reportComposer()
          .composeExplanation(
              messageComposerFunction.apply(value, cond),
              entries
          ));
    } else {
      if (!cond.test(valueHolder.returnedValue()))
        throw exceptionComposerFunction.create(configuration()
            .reportComposer()
            .composeExplanation(
                messageComposerFunction.apply(value, cond),
                emptyList()
            ));
      return value;
    }
  }

  static Validator instance() {
    return INSTANCE.get();
  }

  static void reconfigure(Consumer<Configuration.Builder> configurator) {
    Configuration.Builder b = instance().configuration().parentBuilder();
    reconfigure(configurator, b);
  }

  static void reconfigure(Consumer<Configuration.Builder> configurator, Properties properties) {
    Configuration.Builder b = Configuration.Builder.fromProperties(properties);
    reconfigure(configurator, b);
  }

  static void reconfigure(Consumer<Configuration.Builder> configurator, Configuration.Builder b) {
    Objects.requireNonNull(configurator).accept(b);
    INSTANCE.set(new Impl(b.build()));
  }

  static void resetToDefault() {
    reconfigure(b -> {
    });
  }

  static Configuration configurationFromProperties(Properties properties) {
    return Configuration.Builder.fromProperties(properties).build();
  }

  interface ExceptionFactory<E extends Throwable> extends Function<Explanation, E> {
    default RuntimeException create(Explanation explanation) {
      return createException(this, explanation);
    }

    static RuntimeException createException(ExceptionFactory<?> exceptionFactory, Explanation explanation) {
      Throwable t = exceptionFactory.apply(explanation);
      if (t instanceof Error)
        throw (Error) t;
      if (t instanceof RuntimeException)
        throw (RuntimeException) t;
      throw new AssertionError(format("Checked exception(%s) cannot be used for validation.", t.getClass()), t);
    }
  }

  interface Configuration {
    /**
     * When `com.github.dakusui.valid8j.pcond.debug` is not `true`, it is assumed that those methods in this interface return `false`.
     */
    interface Debugging {
      default boolean suppressSquashing() {
        return true;
      }

      default boolean enableDebugLog() {
        return true;
      }

      default boolean showEvaluableDetail() {
        return true;
      }

      default boolean reportIgnoredEntries() {
        return true;
      }

      default boolean passThroughComparisonFailure() {
        return true;
      }
    }

    int summarizedStringLength();

    boolean useEvaluator();

    /**
     * Returns a message composer, which is responsible for composing an appropriate message for
     * a context.
     *
     * @return A message composer.
     */
    MessageComposer messageComposer();

    /**
     * Returns a report composer, which is responsible for composing an appropriate "report" for
     * a context.
     *
     * @return A report composer
     */
    ReportComposer reportComposer();

    /**
     * Returns an exception composer, which is responsible for creating an exception
     * object of an appropriate type for a context.
     *
     * @return An exception composer.
     */
    ExceptionComposer exceptionComposer();

    Optional<Debugging> debugging();

    Builder parentBuilder();

    enum Utils {
      ;

      @SuppressWarnings("unchecked")
      static <E> E instantiate(@SuppressWarnings("unused") Class<E> baseClass, String className) {
        try {
          return (E) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }

      public static Properties loadPcondProperties() {
        try {
          Properties ret = new Properties();
          System.getProperties().forEach((k, v) -> {
            String key = Objects.toString(k);
            String value = Objects.toString(v);
            String prefix = "com.github.dakusui.valid8j.pcond.";
            if (key.startsWith(prefix)) {
              ret.put(key.replace(prefix, ""), value);
            }
          });
          try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("pcond.properties")) {
            if (inputStream == null)
              return ret;
            ret.load(inputStream);
            return ret;
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    class Builder implements Cloneable {
      boolean useEvaluator;
      int     summarizedStringLength;


      MessageComposer messageComposer;
      ReportComposer reportComposer;
      private ExceptionComposer.ForRequire       exceptionComposerForRequire;
      private ExceptionComposer.ForEnsure        exceptionComposerForEnsure;
      private ExceptionComposer.ForValidate      defaultExceptionComposerForValidate;
      private ExceptionComposer.ForAssertion     exceptionComposerForAssert;
      private ExceptionComposer.ForTestAssertion exceptionComposerForTestFailures;

      public Builder() {
      }


      public Builder useEvaluator(boolean useEvaluator) {
        this.useEvaluator = useEvaluator;
        return this;
      }

      public Builder summarizedStringLength(int summarizedStringLength) {
        this.summarizedStringLength = summarizedStringLength;
        return this;
      }

      public Builder exceptionComposerForRequire(ExceptionComposer.ForRequire exceptionComposerForRequire) {
        this.exceptionComposerForRequire = exceptionComposerForRequire;
        return this;
      }

      public Builder exceptionComposerForEnsure(ExceptionComposer.ForEnsure exceptionComposerForEnsure) {
        this.exceptionComposerForEnsure = exceptionComposerForEnsure;
        return this;
      }

      public Builder defaultExceptionComposerForValidate(ExceptionComposer.ForValidate exceptionComposerForValidate) {
        this.defaultExceptionComposerForValidate = exceptionComposerForValidate;
        return this;
      }

      public Builder exceptionComposerForAssert(ExceptionComposer.ForAssertion exceptionComposerForAssert) {
        this.exceptionComposerForAssert = exceptionComposerForAssert;
        return this;
      }

      public Builder exceptionComposerForAssertThat(ExceptionComposer.ForTestAssertion exceptionComposerForAssertThat) {
        this.exceptionComposerForTestFailures = exceptionComposerForAssertThat;
        return this;
      }

      public Builder messageComposer(MessageComposer messageComposer) {
        this.messageComposer = messageComposer;
        return this;
      }

      public Builder reportComposer(ReportComposer reportComposer) {
        this.reportComposer = reportComposer;
        return this;
      }

      public Builder useOpentest4J() {
        this.exceptionComposerForTestFailures = new ExceptionComposer.ForTestAssertion.Opentest4J();
        return this;
      }

      public Configuration build() {
        if (!isClassPresent("org.junit.ComparisonFailure"))
          this.useOpentest4J();
        return new Configuration() {
          private final Debugging debugging = new Debugging() {
          };

          private final ExceptionComposer exceptionComposer = new ExceptionComposer.Impl(
              exceptionComposerForRequire,
              exceptionComposerForEnsure,
              defaultExceptionComposerForValidate,
              exceptionComposerForAssert,
              exceptionComposerForTestFailures
          );

          @Override
          public int summarizedStringLength() {
            return Builder.this.summarizedStringLength;
          }

          @Override
          public boolean useEvaluator() {
            return Builder.this.useEvaluator;
          }

          /**
           * Returns an exception composer, which is responsible for creating an exception
           * object of an appropriate type for a context.
           *
           * @return An exception composer.
           */
          public ExceptionComposer exceptionComposer() {
            return this.exceptionComposer;
          }

          @Override
          public Optional<Debugging> debugging() {
            if (Boolean.parseBoolean(System.getProperty("com.github.dakusui.valid8j.pcond.debug"))) {
              return Optional.of(this.debugging);
            }
            return Optional.empty();
          }

          @Override
          public MessageComposer messageComposer() {
            return Builder.this.messageComposer;
          }

          @Override
          public ReportComposer reportComposer() {
            return Builder.this.reportComposer;
          }

          @Override
          public Builder parentBuilder() {
            return Builder.this.clone();
          }
        };
      }

      private static boolean isClassPresent(String s) {
        try {
          Class.forName(s);
          return true;
        } catch (ClassNotFoundException e) {
          return false;
        }
      }

      @Override
      public Builder clone() {
        try {
          return (Builder) super.clone();
        } catch (CloneNotSupportedException e) {
          throw new AssertionError();
        }
      }

      static Builder fromProperties(Properties properties) {
        return new Builder()
            .useEvaluator(Boolean.parseBoolean(properties.getProperty("useEvaluator", "true")))
            .summarizedStringLength(Integer.parseInt(properties.getProperty("summarizedStringLength", "40")))
            .exceptionComposerForRequire(instantiate(ExceptionComposer.ForRequire.class, properties.getProperty("exceptionComposerForRequire", "com.github.dakusui.valid8j.pcond.validator.ExceptionComposer$ForRequire$Default")))
            .exceptionComposerForEnsure(instantiate(ExceptionComposer.ForEnsure.class, properties.getProperty("exceptionComposerForEnsure", "com.github.dakusui.valid8j.pcond.validator.ExceptionComposer$ForEnsure$Default")))
            .defaultExceptionComposerForValidate(instantiate(ExceptionComposer.ForValidate.class, properties.getProperty("defaultExceptionComposerForValidate", "com.github.dakusui.valid8j.pcond.validator.ExceptionComposer$ForValidate$Default")))
            .exceptionComposerForAssert(instantiate(ExceptionComposer.ForAssertion.class, properties.getProperty("exceptionComposerForAssert", "com.github.dakusui.valid8j.pcond.validator.ExceptionComposer$ForAssertion$Default")))
            .exceptionComposerForAssertThat(instantiate(ExceptionComposer.ForTestAssertion.class, properties.getProperty("exceptionComposerForTestFailures", "com.github.dakusui.valid8j.pcond.validator.ExceptionComposer$ForTestAssertion$JUnit4")))
            .messageComposer(instantiate(MessageComposer.class, properties.getProperty("messageComposer", "com.github.dakusui.valid8j.pcond.validator.MessageComposer$Default")))
            .reportComposer(instantiate(ReportComposer.class, properties.getProperty("reportComposer", "com.github.dakusui.valid8j.pcond.validator.ReportComposer$Default")));
      }
    }
  }

  class Impl implements Validator {

    private final Configuration configuration;

    public Impl(Configuration configuration) {
      this.configuration = Objects.requireNonNull(configuration);
    }

    @Override
    public Configuration configuration() {
      return this.configuration;
    }
  }
}
