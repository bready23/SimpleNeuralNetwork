package org.bready.util.transformer;

import java.util.function.UnaryOperator;

import org.bready.util.matrix.Matrix;
import org.bready.util.matrix.MatrixFactory;

public class MatrixTransponer<T> implements UnaryOperator<Matrix<T>> {

    @Override
    public Matrix<T> apply(Matrix<T> t) {
        Matrix<T> matrix = MatrixFactory.createNullMatrix(t.getHeight(), t.getWidth());

        for (int i = 0; i < t.getHeight(); i++) {
            for (int j = 0; j < t.getWidth(); j++) {
                matrix.set(j, i, t.get(i, j));
            }
        }

        return matrix;
    }
}
