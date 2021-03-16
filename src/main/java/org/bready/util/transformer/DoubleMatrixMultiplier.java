package org.bready.util.transformer;

import java.util.function.BinaryOperator;

import org.bready.util.matrix.Matrix;
import org.bready.util.matrix.MatrixFactory;

public class DoubleMatrixMultiplier implements BinaryOperator<Matrix<Double>> {

    @Override
    public Matrix<Double> apply(Matrix<Double> t1, Matrix<Double> t2) {
        if (t1.getWidth() != t2.getHeight()) {
            throw new IllegalArgumentException();
        }

        Matrix<Double> result = MatrixFactory.createNullMatrix(t2.getWidth(), t1.getHeight());

        for (int i = 0; i < t1.getHeight(); i++) {
            for (int j = 0; j < t2.getWidth(); j++) {
                Double v = 0.0;

                for (int k = 0; k < t1.getWidth(); k++) {
                    v += t1.get(i, k) * t2.get(k, j);
                }

                result.set(i, j, v);
            }
        }

        return result;
    }
}
