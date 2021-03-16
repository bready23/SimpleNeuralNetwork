package org.bready.util.matrix;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MatrixFactory {
    
    public static <E> Matrix<E> createNullMatrix(int width, int height) {
        return create(width, height, (i, j) -> null);
    }

    public static <E> Matrix<E> create(int width, int height, BiFunction<Integer, Integer, E> function) {
        if (width < 0) {
            throw new IllegalArgumentException(String.valueOf(width));
        } else if (height < 0) {
            throw new IllegalArgumentException(String.valueOf(height));
        }

        return IntStream.range(0, width * height)
                        .mapToObj(v -> function.apply(v / width, v % width))
                        .collect(Matrix.Collector.toMatrixFromSingleRow(width));
    }

    public static <E> Matrix<E> create(E[][] es) {
        return new Matrix<>(Stream.of(es).map(Arrays::asList).collect(Collectors.toList()));
    }

    public static <E> Matrix<E> create(List<List<E>> list) {
        return new Matrix<>(list);
    }

    private MatrixFactory() {}
}
