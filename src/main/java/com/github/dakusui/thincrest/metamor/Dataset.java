package com.github.dakusui.thincrest.metamor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public interface Dataset<E> extends Iterable<E> {
  String name();

  E get(int i);
  
  int size();
  
  default E last() {
    return this.get(this.size() - 1);
  }

  default Stream<E> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  abstract class Base<E> implements Dataset<E> {
    private final String name;
  
    Base(String name) {
      this.name = requireNonNull(name);
    }
    
    public String name() {
      return this.name;
    }
  }
  
  interface OnGoing<E> extends Dataset<E> {
    OnGoing<E> add(E value);
    
    Dataset<E> close();
    
    class Impl<E> extends Base<E> implements OnGoing<E> {
      final List<E> content;
      
      public Impl(String name) {
        super(name);
        this.content = new ArrayList<>();
      }
      
      public Impl(String name, Dataset<E> content) {
        this(name);
        for (int i = 0; i < content.size(); i++)
          this.add(content.get(i));
      }
      
      @Override
      public E get(int i) {
        return this.content.get(i);
      }
      
      @Override
      public int size() {
        return this.content.size();
      }
      
      @Override
      public OnGoing<E> add(E value) {
        this.content.add(value);
        return this;
      }
      
      @Override
      public Dataset<E> close() {
        return new Closed.Impl<>(this.name(), this.content);
      }

      @Override
      public Iterator<E> iterator() {
        return this.content.iterator();
      }

      @Override
      public String toString() {
        if (this.size() == 0)
          return this.name() + ":(empty)";
        return this.name() + ":" + last();
      }
    }
  }
  
  interface Closed<E> extends Dataset<E> {
    class Impl<E> extends Base<E> implements Closed<E> {
      final List<E> content;
      
      public Impl(String name, List<E> content) {
        super(name);
        this.content = unmodifiableList(content);
      }
      
      @Override
      public E get(int i) {
        return this.content.get(i);
      }
      
      @Override
      public int size() {
        return this.content.size();
      }

      @Override
      public Iterator<E> iterator() {
        return this.content.iterator();
      }

      @Override
      public String toString() {
        return this.name() + ":" + this.content;
      }
    }
  }
}
