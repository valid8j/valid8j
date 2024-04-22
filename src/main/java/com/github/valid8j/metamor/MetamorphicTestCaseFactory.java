package com.github.valid8j.metamor;

import com.github.valid8j.metamor.internals.InternalUtils;
import com.github.valid8j.pcond.forms.Printables;

import java.text.MessageFormat;
import java.util.function.*;

import static com.github.valid8j.pcond.forms.Predicates.transform;
import static java.util.Objects.requireNonNull;

/**
 * An interface of a factory for a metamorphic test case.
 *
 * @param <X> Type of "source value".
 * @param <I> Input type of the function under test.
 * @param <O> Output type of function under test.
 * @param <R> Input type of metamorphic relation.
 */
public interface MetamorphicTestCaseFactory<X, I, O, R> {
  /**
   * Returns a function under test.
   *
   * @return a function under test.
   */
  Function<I, O> fut();

  InputResolver.Sequence.Factory<X, I, O> inputResolverSequenceFactory();

  Function<Dataset<IoPair<I, O>>, R> metamorphicTransformer();

  Predicate<R> metamorphicChecker();

  default Predicate<Dataset<IoPair<I, O>>> metamorphicRelation() {
    return transform(metamorphicTransformer()).check(metamorphicChecker());
  }

  /**
   * Returns a function that executes the FUT for each element in `Dataset<InputResolver<I, O>>`.
   *
   * @return A function that executes the FUT for each element in `Dataset<InputResolver<I, O>>`.
   */
  default Function<Dataset<InputResolver<I, O>>, Dataset<IoPair<I, O>>> metamorphicExecutor() {
    return InternalUtils.createObservableProcessingPipeline("fut", this.metamorphicMapper(), this.inputResolverSequenceFactory().count(), inputVariableNameFormatter(), ioVariableName());
  }

  /**
   * A name of input variable.
   * This will be printed in the test report.
   *
   * @return A name of input variable.
   */
  String inputVariableName();

  /**
   * In metamorphic testing context, the function under test is executed multiple times with different input values.
   * The returned function renders input variable names so that they can be identified each other when an index is given.
   * By default, it returns a function that appends the given index.
   *
   * @return A function to render an input variable name corresponding to a given index.
   */
  default IntFunction<String> inputVariableNameFormatter() {
    return i -> this.inputVariableName() + "[" + i + "]";
  }

  default Function<IoContext<InputResolver<I, O>, IoPair<I, O>>, Function<InputResolver<I, O>, IoPair<I, O>>> metamorphicMapper() {
    return Printables.function(
        () -> "  " + fut(),
        ioContext -> Printables.function(
            () -> "input:" + ioContext.output(),
            inputResolver -> {
              I in = inputResolver.apply(ioContext.output());
              return IoPair.create(in, fut().apply(in));
            }));
  }

  /**
   * A builder method that returns a printable predicate that examines the function under test.
   *
   * @return A printable predicate that examines FUT with a given metamorphic relation.
   */
  default Predicate<X> toMetamorphicTestPredicate() {
    return transform(this.inputResolverSequenceFactory().andThen(this.metamorphicExecutor())).check(this.metamorphicRelation());
  }

  String ioVariableName();


  static <I, O> Builder<Object, I, O, Object> forFunctionUnderTest(String name, Function<I, O> fut) {
    return forFunctionUnderTest(Printables.function(name, fut));
  }

  static <I, O> Builder<Object, I, O, Object> forFunctionUnderTest(Function<I, O> fut) {
    return new Builder<Object, I, O, Object>().fut(fut);
  }

  class Impl<X, I, O, R> implements MetamorphicTestCaseFactory<X, I, O, R> {

    private final Function<I, O> fut;
    private final InputResolver.Sequence.Factory<X, I, O> inputResolverSequenceFactory;
    private final Function<Dataset<IoPair<I, O>>, R> metamorphicTransformer;
    private final Predicate<R> metamorphicChecker;
    private final String inputVariableName;
    private final String ioVariableName;

    public Impl(Function<I, O> fut, InputResolver.Sequence.Factory<X, I, O> inputResolverSequenceFactory, Function<Dataset<IoPair<I, O>>, R> metamorphicTransformer, Predicate<R> metamorphicChecker, String inputVariableName, String ioVariableName) {
      this.fut = fut;
      this.inputResolverSequenceFactory = inputResolverSequenceFactory;
      this.metamorphicTransformer = metamorphicTransformer;
      this.metamorphicChecker = metamorphicChecker;
      this.inputVariableName = inputVariableName;
      this.ioVariableName = ioVariableName;
    }

    @Override
    public Function<I, O> fut() {
      return this.fut;
    }

    @Override
    public InputResolver.Sequence.Factory<X, I, O> inputResolverSequenceFactory() {
      return this.inputResolverSequenceFactory;
    }

    @Override
    public Function<Dataset<IoPair<I, O>>, R> metamorphicTransformer() {
      return this.metamorphicTransformer;
    }

    @Override
    public Predicate<R> metamorphicChecker() {
      return this.metamorphicChecker;
    }

    @Override
    public String inputVariableName() {
      return this.inputVariableName;
    }

    @Override
    public String ioVariableName() {
      return this.ioVariableName;
    }

  }

  abstract class BuilderBase<B extends BuilderBase<B, X, I, O, R>, X, I, O, R> {
    abstract static class InputResolverSequenceFactoryProvider<X, I, O> implements Supplier<InputResolver.Sequence.Factory<X, I, O>> {
      final BuilderBase<?, X, I, O, ?> parent;

      protected InputResolverSequenceFactoryProvider(BuilderBase<?, X, I, O, ?> parent) {
        this.parent = parent;
      }

      abstract void add(Function<Object, String> formatter, Function<X, I> function);

      abstract int count();
    }

    protected Function<I, O> fut;
    protected InputResolverSequenceFactoryProvider<X, I, O> inputResolverSequenceFactoryProvider;
    protected Predicate<R> checker;
    protected String sourceVariableName;
    protected String inputVariableName;
    protected String ioVariableName;
    protected String outputVariableName;

    protected BuilderBase() {
      this.sourceVariableName("x")
          .inputVariableName("input")
          .ioVariableName("io")
          .outputVariableName("out");
    }

    protected <BB extends BuilderBase<BB, XX, I, O, RR>, XX, RR> BB newBuilder(Supplier<BB> constructor) {
      return constructor.get()
          .fut(this.fut)
          .sourceVariableName(this.sourceVariableName)
          .inputVariableName(this.inputVariableName)
          .ioVariableName(this.ioVariableName)
          .outputVariableName(this.outputVariableName);
    }

    protected <BB extends BuilderBase<BB, X, I, O, RR>, RR> BB newBuilderWithSpecifiedRelationType(Supplier<BB> constructor) {
      return newBuilder(constructor)
          .inputResolverSequenceFactoryProvider(this.inputResolverSequenceFactoryProvider);
    }

    protected <BB extends BuilderBase<BB, XX, I, O, R>, XX> BB newBuilderWithSpecifiedSourceType(Supplier<BB> constructor) {
      return this.newBuilder(constructor);
    }

    @SuppressWarnings("unchecked")
    public B sourceVariableName(String sourceVariableName) {
      this.sourceVariableName = sourceVariableName;
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B inputVariableName(String inputVariableName) {
      this.inputVariableName = inputVariableName;
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B outputVariableName(String outputVariableName) {
      this.outputVariableName = requireNonNull(outputVariableName);
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B ioVariableName(String ioVariableName) {
      this.ioVariableName = requireNonNull(ioVariableName);
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    B inputResolverSequenceFactoryProvider(InputResolverSequenceFactoryProvider<X, I, O> inputResolverSequenceFactory) {
      this.inputResolverSequenceFactoryProvider = requireNonNull(inputResolverSequenceFactory);
      return (B) this;
    }

    public B inputResolverSequenceFactory(InputResolver.Sequence.Factory<X, I, O> inputResolverSequenceFactory) {
      Utils.requireState(this.inputResolverSequenceFactoryProvider == null, "Input Resolver Sequence Factory is already set.");
      return this.inputResolverSequenceFactoryProvider(new InputResolverSequenceFactoryProvider<X, I, O>(this) {

        @Override
        public InputResolver.Sequence.Factory<X, I, O> get() {
          return inputResolverSequenceFactory;
        }

        @Override
        void add(Function<Object, String> formatter, Function<X, I> function) {
          throw new IllegalStateException();
        }

        @Override
        int count() {
          return inputResolverSequenceFactory.count();
        }
      });
    }

    public B addInputResolvers(Function<InputResolver.Sequence.Factory.Builder<X, I, O>, InputResolver.Sequence.Factory<X, I, O>> b) {
      return this.addInputResolvers(this.sourceVariableName, b);
    }

    public B addInputResolvers(String variableName, Function<InputResolver.Sequence.Factory.Builder<X, I, O>, InputResolver.Sequence.Factory<X, I, O>> b) {
      InputResolver.Sequence.Factory.Builder<X, I, O> ib = new InputResolver.Sequence.Factory.Builder<>(this.inputVariableName, variableName);
      return this.inputResolverSequenceFactory(b.apply(ib));
    }

    @SuppressWarnings("unchecked")
    public B addInputResolver(Function<Object, String> formatter, Function<X, I> f) {
      requireNonNull(formatter);
      requireNonNull(f);
      if (this.inputResolverSequenceFactoryProvider == null) {
        this.inputResolverSequenceFactoryProvider = new InputResolverSequenceFactoryProvider<X, I, O>(this) {
          int count = 0;
          Consumer<InputResolver.Sequence.Factory.Builder<X, I, O>> inputResolverAdder = b -> {
          };

          @Override
          void add(Function<Object, String> formatter, Function<X, I> f) {
            inputResolverAdder = inputResolverAdder.andThen(b -> b.function(formatter, f));
            count++;
          }

          @Override
          int count() {
            return count;
          }

          @Override
          public InputResolver.Sequence.Factory<X, I, O> get() {
            InputResolver.Sequence.Factory.Builder<X, I, O> b = new InputResolver.Sequence.Factory.Builder<>(BuilderBase.this.inputVariableName, BuilderBase.this.sourceVariableName);
            this.inputResolverAdder.accept(b);
            return b.build();
          }
        };
      }
      this.inputResolverSequenceFactoryProvider.add(formatter, f);
      return (B) this;
    }

    public abstract <BB extends BuilderBase<BB, XX, I, O, R>, XX> BB sourceValueType(XX sourceType);

    /**
     * Let this object know the source type.
     *
     * @param sourceType The type of the source value.x
     * @param <BB>       The type of this object.
     * @param <XX>       The type of the input value.
     * @return This object
     */
    @SuppressWarnings("unused")
    public <BB extends BuilderBase<BB, XX, I, O, R>, XX> BB sourceValueType(Class<XX> sourceType) {
      return this.sourceValueType((XX) null);
    }

    /**
     * Let this factory know that the source value and the input values are the same type.
     *
     * @param <BB> The type of this builder.
     * @return This object
     */
    @SuppressWarnings("unchecked")
    public <BB extends BuilderBase<BB, I, I, O, R>> BB makeInputResolversEndomorphic() {
      return (BB) this.sourceValueType((I) null)
          .addInputResolver(x -> String.format("%s", x), Function.identity());
    }


    /**
     * Specifies a function under test.
     *
     * @param fut A function under test
     * @return This builder object
     */
    @SuppressWarnings("unchecked")
    public B fut(Function<I, O> fut) {
      this.fut = requireNonNull(fut);
      return (B) this;
    }

    public <P> MetamorphicTestCaseFactoryWithPreformer.Builder<X, I, O, P, R> withPreformer() {
      return this.newBuilderWithSpecifiedRelationType(MetamorphicTestCaseFactoryWithPreformer.Builder::new);
    }

    public Builder<X, I, O, R> skipPreformer() {
      return this.newBuilderWithSpecifiedRelationType(Builder::new);
    }

    public abstract <P> MetamorphicTestCaseFactoryWithPreformer.Builder<X, I, O, P, R> preformer(Function<IoPair<I, O>, P> preformer);

    public <P> MetamorphicTestCaseFactoryWithPreformer.Builder<X, I, O, P, R> preformer(String preformerName, Function<IoPair<I, O>, P> preformer) {
      return this.preformer(Printables.function(preformerName, preformer));
    }

    public MetamorphicTestCaseFactoryWithPreformer.Builder<X, I, O, O, R> outputOnly() {
      return this.preformer("outputOnly", IoPair::output);
    }

    @SuppressWarnings("unchecked")
    public B checker(Predicate<R> checker) {
      this.checker = requireNonNull(checker);
      return (B) this;
    }

    public MetamorphicTestCaseFactory<X, I, O, R> check(String name, Predicate<R> checker) {
      return this.check(Printables.predicate(name, checker));
    }

    public MetamorphicTestCaseFactory<X, I, O, R> check(Predicate<R> checker) {
      return checker(checker).build();
    }

    public abstract MetamorphicTestCaseFactory<X, I, O, R> build();
  }

  class Builder<X, I, O, R> extends BuilderBase<Builder<X, I, O, R>, X, I, O, R> {
    private Function<Dataset<IoPair<I, O>>, R> transformer;

    public Builder() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <BB extends BuilderBase<BB, XX, I, O, R>, XX> BB sourceValueType(XX sourceType) {
      return (BB) this.<Builder<XX, I, O, R>, XX>newBuilderWithSpecifiedSourceType(Builder::new);
    }

    public Builder<X, I, O, Proposition> propositionFactory(Function<Dataset<IoPair<I, O>>, Proposition> pf) {
      return this.<Builder<X, I, O, Proposition>, Proposition>newBuilderWithSpecifiedRelationType(Builder::new)
          .transformer(pf)
          .checker(PropositionPredicate.INSTANCE);
    }

    public MetamorphicTestCaseFactory<X, I, O, Proposition> proposition(Function<Object[], String> formatter, Predicate<Dataset<IoPair<I, O>>> p) {
      return this.propositionFactory(
              Proposition.Factory.create(
                  p,
                  formatter,
                  i -> ioVariableName + "[" + i + "]",
                  inputResolverSequenceFactoryProvider.count()))
          .build();
    }

    public MetamorphicTestCaseFactory<X, I, O, Proposition> proposition(String propositionName, Predicate<Dataset<IoPair<I, O>>> p) {
      return this.proposition(args -> MessageFormat.format(propositionName, args), p);
    }

    public <P> MetamorphicTestCaseFactoryWithPreformer.Builder<X, I, O, P, R> preformer(Function<IoPair<I, O>, P> preformer) {
      return this.<P>withPreformer().preformer(preformer);
    }

    public Builder<X, I, O, R> transformer(Function<Dataset<IoPair<I, O>>, R> transformer) {
      this.transformer = requireNonNull(transformer);
      return this;
    }

    @Override
    public MetamorphicTestCaseFactory<X, I, O, R> build() {
      return new Impl<>(this.fut, this.inputResolverSequenceFactoryProvider.get(), this.transformer, this.checker, this.inputVariableName, this.ioVariableName);
    }
  }
}
