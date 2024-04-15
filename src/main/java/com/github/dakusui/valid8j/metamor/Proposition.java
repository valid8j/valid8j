package com.github.dakusui.valid8j.metamor;

import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunction;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

public interface Proposition {
  boolean evaluate();
  
  int arity();
  
  class Impl<X> implements Proposition {
    final         Function<Object[], String> formatter;
    private final Predicate<Dataset<X>>      predicate;
    private final Dataset<X>                 dataset;
    
    public Impl(Predicate<Dataset<X>> predicate, Function<Object[], String> formatter, Dataset<X> dataset) {
      this.predicate = predicate;
      this.formatter = requireNonNull(formatter);
      this.dataset = requireNonNull(dataset);
    }
    
    @Override
    public boolean evaluate() {
      return this.predicate.test(this.dataset);
    }
    
    @Override
    public int arity() {
      return this.dataset.size();
    }
    
    @Override
    public String toString() {
      return this.formatter.apply(IntStream.range(0, this.arity())
          .mapToObj(dataset::get)
          .toArray());
    }
  }
  
  interface Factory<X> extends Function<Dataset<X>, Proposition> {

    static <X> Factory<X> create(Predicate<Dataset<X>> predicate, Function<Object[], String> formatter, IntFunction<String> placeHolderFormatter, int arity) {
      return new Impl<>(predicate, formatter, placeHolderFormatter, arity);
    }
    
    class Impl<X> extends PrintableFunction<Dataset<X>, Proposition> implements Factory<X> {
      
      protected Impl(Predicate<Dataset<X>> predicate, Function<Object[], String> formatter, IntFunction<String> placeHolderFormatter, int arity) {
        super(
            new Object(),
            emptyList(),
            () -> formatter.apply(IntStream.range(0, arity).mapToObj(placeHolderFormatter).toArray()),
            ds -> new Proposition.Impl<>(predicate, formatter, ds));
      }
      
      @Override
      public Proposition apply(Dataset<X> ds) {
        return super.apply(ds);
      }
    }
  }
}
