package com.github.dakusui.thincrest.metamor;

import com.github.dakusui.valid8j_pcond.core.printable.PrintableFunction;
import com.github.dakusui.valid8j_pcond.forms.Printables;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface InputResolver<I, O> extends Function<Dataset<IoPair<I, O>>, I> {
  class Impl<I, O> extends PrintableFunction<Dataset<IoPair<I, O>>, I> implements InputResolver<I, O> {
    public Impl(Supplier<String> s, Function<? super Dataset<IoPair<I, O>>, ? extends I> function) {
      super(new Object(), emptyList(), s, function);
    }
  }
  
  interface Sequence<I, O> extends Dataset<InputResolver<I, O>> {
    class Impl<I, O> implements Sequence<I, O> {
  
      private final String inputVariableName;
      private final List<InputResolver<I, O>> inputResolvers;
      
      public Impl(String inputVariableName, List<InputResolver<I, O>> inputResolvers) {
        this.inputVariableName = requireNonNull(inputVariableName);
        this.inputResolvers = requireNonNull(inputResolvers);
      }
      
      @Override
      public InputResolver<I, O> get(int i) {
        return inputResolvers.get(i);
      }
      
      @Override
      public int size() {
        return inputResolvers.size();
      }
  
      @Override
      public String name() {
        return this.inputVariableName;
      }
  
      @Override
      public String toString() {
        return inputVariableName + ":" + inputResolvers;
      }

      @Override
      public Iterator<InputResolver<I, O>> iterator() {
        return new AbstractList<InputResolver<I, O>>() {

          @Override
          public int size() {
            return Impl.this.size();
          }

          @Override
          public InputResolver<I, O> get(int index) {
            return Impl.this.get(index);
          }
        }.iterator();
      }
    }
    
    interface Factory<X, I, O> extends Function<X, Sequence<I, O>> {
      int count();
      
      class Impl<X, I, O>
          extends PrintableFunction<X, Sequence<I, O>>
          implements Factory<X, I, O> {
        
        private final int count;
        
        public Impl(Supplier<String> s, Function<? super X, ? extends Sequence<I, O>> function, int count) {
          super(new Object(), emptyList(), s, function);
          this.count = count;
        }
        
        public Impl(String inputVariableName, List<Function<? super X, ? extends InputResolver<I, O>>> functions) {
          this(
              () -> functions.stream()
                  .map(Objects::toString)
                  .collect(joining(",", "[", "]")),
              Factory.Impl.toSequenceCreatorFunction(inputVariableName, functions), functions.size());
        }
        
        @Override
        public int count() {
          return this.count;
        }
        
        private static <X, I, O> Function<? super X, ? extends Sequence<I, O>> toSequenceCreatorFunction(String inputVariableName, List<Function<? super X, ? extends InputResolver<I, O>>> functions) {
          return (X value) -> new Sequence.Impl<>(
              inputVariableName,
              functions.stream()
                  .map(f -> f.apply(value))
                  .collect(toList()));
        }
      }
      
      class Builder<X, I, O> {
        private final List<Function<? super X, ? extends InputResolver<I, O>>> functions = new LinkedList<>();
        private final String                                                   placeHolderVariableName;
        private final String inputVariableName;
  
        public Builder(String inputVariableName, String placeHolderVariableName) {
          this.placeHolderVariableName = placeHolderVariableName;
          this.inputVariableName = inputVariableName;
        }
        
        public Builder<X, I, O> function(Function<Object, String> formatter, Function<X, I> f) {
          this.functions.add(Printables.function(() -> formatter.apply(this.placeHolderVariableName), x -> new InputResolver.Impl<>(() -> formatter.apply(x), ds -> f.apply(x))));
          return this;
        }
        
        public Factory<X, I, O> build() {
          return new Impl<>(this.inputVariableName, this.functions);
        }
      }
    }
  }
}
