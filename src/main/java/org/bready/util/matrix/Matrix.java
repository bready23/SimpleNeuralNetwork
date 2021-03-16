package org.bready.util.matrix;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bready.util.array.Array;
import org.bready.util.array.ArrayFactory;

public class Matrix<E> {

    private final List<List<E>> list;

    Matrix(List<List<E>> list) {
        this.list = list;
    }

    public int getWidth() {
        return list.isEmpty() ? 0 : list.get(0).size();
    }

    public int getHeight() {
        return list.size();
    }

    public Array<E> getRow(int row) {
        return ArrayFactory.create(list.get(row));
    }

    public Array<E> getColumn(int column) {
        return list.stream().map(v -> v.get(column)).collect(Array.Collector.toArray());
    }

    public E get(int row, int column) {
        return list.get(row).get(column);
    }

    public void set(int row, int column, E value) {
        list.get(row).set(column, value);
    }

    public static class Collector<T> implements java.util.stream.Collector<T, LinkedList<List<T>>, Matrix<T>> {

        public static <T> Collector<T> toSingleRowMatrix() {
            return new Collector<>(Integer.MAX_VALUE);
        }

        public static <T> Collector<T> toMatrixFromSingleRow(int width) {
            return new Collector<>(width);
        }

        private final int width;

        private Collector(int width) {
            this.width = width;
        }

        @Override
        public Supplier<LinkedList<List<T>>> supplier() {
            return LinkedList::new;
        }

        @Override
        public BiConsumer<LinkedList<List<T>>, T> accumulator() {
            return (l, v) -> {
                if (l.isEmpty() || l.getLast().size() == width) {
                    l.add(new ArrayList<>());
                }

                l.getLast().add(v);
            };
        }

        @Override
        public BinaryOperator<LinkedList<List<T>>> combiner() {
            return (l1, l2) -> {
                LinkedList<List<T>> l = new LinkedList<>(l1);
                l.addAll(l2);
                return l;
            };
        }

        @Override
        public Function<LinkedList<List<T>>, Matrix<T>> finisher() {
            return Matrix::new;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return new HashSet<>();
        }
    }
}
