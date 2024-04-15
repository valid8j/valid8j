package com.github.dakusui.valid8j.metamor;

import com.github.dakusui.valid8j.pcond.core.Evaluator;
import com.github.dakusui.valid8j.pcond.core.printable.PrintableFunction;
import com.github.dakusui.valid8j.pcond.forms.Printables;

import java.util.function.Function;
import java.util.function.IntFunction;

import static java.util.Objects.requireNonNull;

public interface IoContext<I, O> {
  String prefix();
  
  default String name() {
    return this.prefix() + ":" + this.input().name() + "=>" + this.output().name();
  }
  
  Dataset<I> input();
  
  Dataset<O> output();
  
  enum Utils {
    ;
    
    public static <I, O> Function<Dataset<I>, Ongoing<I, O>> toContextFunction(String contextName, String outputContextName) {
      return Printables.function("begin:" + contextName, input -> new Ongoing.Impl<>(contextName, input, outputContextName));
    }
    
    public static <I, O> Function<Ongoing<I, O>, Ongoing<I, O>> toContextEndomorphicFunction(Function<IoContext<I, O>, Function<I, O>> mapper, int numItems, IntFunction<String> variableNameFormatter) {
      requireNonNull(mapper);
      Function<Ongoing<I, O>, Ongoing<I, O>> ret = null;
      for (int i = 0; i < numItems; i++) {
        Function<Ongoing<I, O>, Ongoing<I, O>> cur = mapperToEndomorphicProcessor(mapper, i, variableNameFormatter);
        if (ret == null) {
          ret = cur;
        } else
          ret = ret.andThen(cur);
      }
      assert ret != null;
      return ret;
    }
    
    public static <I, O> Function<Ongoing<I, O>, Ongoing<I, O>> mapperToEndomorphicProcessor(Function<IoContext<I, O>, Function<I, O>> mapper, int i, IntFunction<String> variableNameFormatter) {
      return getOngoingOngoingFunction(mapper, i, variableNameFormatter);
    }
    
    private static <I, O> Function<Ongoing<I, O>, Ongoing<I, O>> getOngoingOngoingFunction(Function<IoContext<I, O>, Function<I, O>> mapper, int i, IntFunction<String> variableNameFormatter) {
      return Printables.function(
          () -> String.format("%s(%s)", mapper, variableNameFormatter.apply(i)),
          c -> {
            I in = c.input().get(i);
            O out = mapper.apply(c).apply(in);
            c.output().add(out);
            return c.cloneObject();
          });
    }
    
    public static <I, O> Function<Ongoing<I, O>, IoContext<I, O>> toCloseFunction(String contextName) {
      // close
      return ((PrintableFunction<Ongoing<I, O>, IoContext<I, O>>) Printables.<Ongoing<I, O>, IoContext<I, O>>function(() -> "end:" + contextName, Ongoing::close)).makeTrivial();
    }
    
    public static <I, O> Function<IoContext<I, O>, Dataset<O>> toOutputExtractorFunction(String contextName) {
      return Printables.function(() -> "output(" + contextName + ")", IoContext::output);
    }
  }
  
  interface Closed<I, O> extends IoContext<I, O> {
    class Impl<I, O> implements Closed<I, O> {
      private final Dataset<I> input;
      private final Dataset<O> output;
      private final String     prefix;
      
      public Impl(String prefix, Dataset<I> input, Dataset<O> output) {
        this.prefix = prefix;
        this.input = input;
        this.output = output;
      }
  
      @Override
      public String prefix() {
        return this.prefix;
      }
      
      @Override
      public Dataset<I> input() {
        return input;
      }
      
      @Override
      public Dataset<O> output() {
        return output;
      }
      
      
      @Override
      public String toString() {
        return "(context:" + name() + ")";
      }
    }
  }
  
  interface Ongoing<I, O> extends IoContext<I, O>, Evaluator.Snapshottable {
    @Override
    Dataset.OnGoing<O> output();
    
    default Ongoing<I, O> cloneObject() {
      return new Impl<>(this.prefix(), input(), output());
    }
    
    default IoContext<I, O> close() {
      return new Closed.Impl<>(this.prefix(), this.input(), this.output().close());
    }
    
    class Snapshot {
      final private Object in;
      final private Object out;
      
      public Snapshot(Object in, Object out) {
        this.in = in;
        this.out = out;
      }
      
      public Object in() {
        return this.in;
      }
      
      public Object out() {
        return this.out;
      }
      
      public String toString() {
        return String.format("%s=>%s", this.in, this.out);
      }
    }
    
    class Impl<I, O> implements Ongoing<I, O> {
      private final String prefix;
      private final Dataset<I>         input;
      private final Dataset.OnGoing<O> output;
      
      public Impl(String prefix, Dataset<I> input, String outputDatasetName) {
        this.prefix = prefix;
        this.input = requireNonNull(input);
        this.output = new Dataset.OnGoing.Impl<>(outputDatasetName);
      }
      
      public Impl(String prefix, Dataset<I> input, Dataset<O> output) {
        this.prefix = prefix;
        this.input = requireNonNull(input);
        this.output = new Dataset.OnGoing.Impl<>(output.name(), output);
      }
  
      @Override
      public String prefix() {
        return this.prefix;
      }
  
      @Override
      public Dataset<I> input() {
        return this.input;
      }
      
      @Override
      public Dataset.OnGoing<O> output() {
        assert this.output != null;
        return this.output;
      }
      
      @Override
      public Object snapshot() {
        if (this.output.size() == 0)
          return new Object() {
            @Override
            public String toString() {
              return String.format("(context:%s)", Impl.this.name());
            }
          };
        return new Snapshot(this.input.get(this.output.size() - 1), this.output.last());
      }
      
      @Override
      public String toString() {
        if (this.output.size() == 0)
          return "(empty)";
        return String.format("in: <%s>%nout:<%s>", this.input.get(this.output.size() - 1), this.output.last());
      }
    }
  }
}
