package org.bready.util.array;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.bready.util.matrix.Matrix;
import org.bready.util.matrix.MatrixFactory;

public class Array<E> {

    private final List<E> list;

    Array(List<E> list) {
        this.list = new ArrayList<>(list);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public E get(int index) {
        return list.get(index);
    }

    public void set(int index, E e) {
        list.set(index, e);
    }

    public Matrix<E> toMatrix() {
        return MatrixFactory.create(Collections.singletonList(list));
    }

    public List<E> toList() {
        return list;
    }

    public Stream<E> stream() {
        return list.stream();
    }

    public static class Collector<T> implements java.util.stream.Collector<T, List<T>, Array<T>> {

        public static <T> Collector<T> toArray() {
            return new Collector<>();
        }

        private Collector() {
        }

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (l1, l2) -> {
                List<T> l = new ArrayList<>(l1);
                l.addAll(l2);
                return l;
            };
        }

        @Override
        public Function<List<T>, Array<T>> finisher() {
            return Array::new;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return new HashSet<>();
        }
    }
}
