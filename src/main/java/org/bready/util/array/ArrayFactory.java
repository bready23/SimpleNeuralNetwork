package org.bready.util.array;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class ArrayFactory {
    
    public static <E> Array<E> createNullArray(int size) {
        return create(size, i -> null);
    }

    public static <E> Array<E> create(int size, IntFunction<E> function) {
        if (size < 0) {
            throw new IllegalArgumentException(String.valueOf(size));
        }

        return IntStream.range(0, size).mapToObj(function).collect(Array.Collector.toArray());
    }

    @SafeVarargs
    public static <E> Array<E> create(E... es) {
        return new Array<>(Arrays.asList(es));
    }

    public static <E> Array<E> create(List<E> list) {
        return new Array<>(list);
    } 

    private ArrayFactory() {}
}
