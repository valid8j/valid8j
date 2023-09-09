package com.github.dakusui.thincrest.metamor;


import com.github.dakusui.thincrest.metamor.internals.InternalUtils;
import com.github.dakusui.valid8j_pcond.forms.Printables;

import java.text.MessageFormat;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public interface MetamorphicTestCaseFactoryWithPreformer<X, I, O, P, R> extends MetamorphicTestCaseFactory<X, I, O, R> {
  Function<IoPair<I, O>, P> metamorphicPreformer();

  Function<Dataset<P>, R> metamorphicReducer();

  @Override
  default Function<Dataset<IoPair<I, O>>, R> metamorphicTransformer() {
    return metamorphicPreformerStage().andThen(metamorphicReducer());
  }

  default Function<Dataset<IoPair<I, O>>, Dataset<P>> metamorphicPreformerStage() {
    return InternalUtils.createObservableProcessingPipeline(
        "preform",
        metamorphicPreformerToIoContextCallback(metamorphicPreformer()),
        inputResolverSequenceFactory().count(),
        ioVariableNameFormatter(),
        ioVariableName());
  }

  default Function<IoContext<IoPair<I, O>, P>, Function<IoPair<I, O>, P>> metamorphicPreformerToIoContextCallback(Function<IoPair<I, O>, P> preformer) {
    return Printables.function(
        () -> "preform:" + preformer,
        c -> Printables.function(
            () -> String.format("preform[%s]:%s", c, preformer),
            preformer));
  }

  IntFunction<String> ioVariableNameFormatter();

  class Impl<X, I, O, P, R> implements MetamorphicTestCaseFactoryWithPreformer<X, I, O, P, R> {

    private final Function<I, O>                          fut;
    private final InputResolver.Sequence.Factory<X, I, O> inputResolverSequenceFactory;
    private final Function<IoPair<I, O>, P>               preformer;
    private final Function<Dataset<P>, R>                 reducer;
    private final Predicate<R>                            checker;
    private final String                                  ioVariableName;
    private final String                                  inputVariableName;

    public Impl(Function<I, O> fut, InputResolver.Sequence.Factory<X, I, O> inputResolverSequenceFactory, Function<IoPair<I, O>, P> preformer, Function<Dataset<P>, R> reducer, Predicate<R> checker, String inputVariableName, String ioVariableName) {
      this.fut = requireNonNull(fut);
      this.inputResolverSequenceFactory = inputResolverSequenceFactory;
      this.preformer = requireNonNull(preformer);
      this.reducer = requireNonNull(reducer);
      this.checker = requireNonNull(checker);
      this.inputVariableName = requireNonNull(inputVariableName);
      this.ioVariableName = requireNonNull(ioVariableName);
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
    public Function<IoPair<I, O>, P> metamorphicPreformer() {
      return this.preformer;
    }

    @Override
    public Function<Dataset<P>, R> metamorphicReducer() {
      return Printables.function(() -> "reduce:" + reducer, this.reducer);
    }

    @Override
    public Predicate<R> metamorphicChecker() {
      return this.checker;
    }

    @Override
    public String inputVariableName() {
      return this.inputVariableName;
    }

    @Override
    public IntFunction<String> inputVariableNameFormatter() {
      return i -> this.inputVariableName + "[" + i + "]";
    }

    @Override
    public String ioVariableName() {
      return this.ioVariableName;
    }

    @Override
    public IntFunction<String> ioVariableNameFormatter() {
      return i -> this.ioVariableName + "[" + i + "]";
    }
  }

  class Builder<X, I, O, P, R> extends BuilderBase<Builder<X, I, O, P, R>, X, I, O, R> {
    private Function<Dataset<P>, R>   reducer;
    private Function<IoPair<I, O>, P> preformer;

    public Builder() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <BB extends BuilderBase<BB, XX, I, O, R>, XX> BB sourceValueType(XX sourceType) {
      return (BB) this.<Builder<XX, I, O, P, R>, XX>newBuilderWithSpecifiedSourceType(Builder::new);
    }

    @Override
    public <PP> Builder<X, I, O, PP, R> preformer(Function<IoPair<I, O>, PP> preformer) {
      Builder<X, I, O, PP, R> ret = this.withPreformer();
      ret.preformer = preformer;
      return ret;
    }

    public <QQ> Builder<X, I, O, QQ, R> preform(Function<P, QQ> preformer) {
      Function<IoPair<I, O>, P> currentPreformer = this.preformer;
      if (currentPreformer == null)
        throw new IllegalStateException();
      Builder<X, I, O, QQ, R> ret = this.withPreformer();
      ret.preformer = currentPreformer.andThen(preformer);
      return ret;
    }

    public <QQ> Builder<X, I, O, QQ, R> preform(String name, Function<P, QQ> preformer) {
      return this.preform(Printables.function(name, preformer));
    }

    public Builder<X, I, O, P, R> reducer(Function<Dataset<P>, R> reducer) {
      this.reducer = requireNonNull(reducer);
      return this;
    }

    public Builder<X, I, O, P, R> reduce(Function<Dataset<P>, R> reducer) {
      return this.reducer(reducer);
    }

    public Builder<X, I, O, P, R> reduce(String reducerName, Function<Dataset<P>, R> reducer) {
      return this.reduce(Printables.function(reducerName, reducer));
    }

    public Builder<X, I, O, P, Proposition> propositionFactory(Function<Dataset<P>, Proposition> pf) {
      return this
          .<Builder<X, I, O, P, Proposition>, Proposition>newBuilderWithSpecifiedRelationType(Builder::new)
          .preformer(this.preformer)
          .reducer(pf)
          .checker(PropositionPredicate.INSTANCE);
    }

    public MetamorphicTestCaseFactory<X, I, O, Proposition> proposition(Function<Object[], String> propositionFormatter, Predicate<Dataset<P>> p) {
      return this.propositionFactory(
          Proposition.Factory.create(
              p,
              propositionFormatter,
              i -> this.outputVariableName + "[" + i + "]",
              this.inputResolverSequenceFactoryProvider.count()))
          .build();
    }

    public MetamorphicTestCaseFactory<X, I, O, Proposition> proposition(String propositionName, Predicate<Dataset<P>> p) {
      return this.proposition(args -> MessageFormat.format(propositionName, args), p);
    }

    public MetamorphicTestCaseFactoryWithPreformer<X, I, O, P, R> build() {
      return new Impl<>(fut, inputResolverSequenceFactoryProvider.get(), preformer, reducer, checker, inputVariableName, ioVariableName);
    }
  }
}
