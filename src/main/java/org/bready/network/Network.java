package org.bready.network;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

import org.bready.function.ActivationFunction;
import org.bready.function.ActivationFunctions;
import org.bready.util.array.Array;
import org.bready.util.array.ArrayFactory;
import org.bready.util.matrix.Matrix;
import org.bready.util.matrix.MatrixFactory;
import org.bready.util.transformer.DoubleMatrixMultiplier;

public class Network {

    private final List<Array<Double>> biasesList;
    private final List<Matrix<Double>> weightsList;

    private ActivationFunction function;

    private double upperBorder;
    private double lowerBorder;

    public Network(int var1, int var2, int... vars) {
        this.biasesList = new ArrayList<>();
        this.weightsList = new ArrayList<>();

        this.function = ActivationFunctions.identity();
        this.upperBorder = Double.POSITIVE_INFINITY;
        this.lowerBorder = Double.NEGATIVE_INFINITY;

        addBiasArray(var1);
        addBiasArray(var2);
        addWeightsMatrix(var2, var1);

        Arrays.stream(vars).forEach(this::addBiasArray);
        IntStream.range(0, vars.length)
                .forEach(index -> addWeightsMatrix(vars[index], index == 0 ? var2 : vars[index - 1]));
    }

    public int layersSize() {
        return biasesList.size();
    }

    public int layerSize(int layer) {
        return biasesList.get(layer).size();
    }

    public double getBiasAt(int layer, int index) {
        return biasesList.get(layer).get(index);
    }

    public void setBiasAt(int layer, int index, double value) {
        biasesList.get(layer).set(index, value);
    }

    public Double getWeightBetween(WeightRetriveRequest request) {
        return weightsList.get(request.layer).get(request.row1, request.row2);
    }

    public void setWeightBetween(WeightUpdateRequest request) {
        weightsList.get(request.layer).set(request.row1, request.row2, request.value);
    }

    public void setActivationFunction(ActivationFunction function) {
        this.function = function;
    }

    public ActivationFunction getActivationFunction() {
        return function;
    }

    public void setUpperBorder(double upperBorder) {
        this.upperBorder = upperBorder;
    }

    public double getUpperBorder() {
        return upperBorder;
    }

    public void setLowerBorder(double lowerBorder) {
        this.lowerBorder = lowerBorder;
    }

    public double getLowerBorder() {
        return lowerBorder;
    }

    public Array<Double> think(Double... values) {
        Matrix<Double> matrix = Arrays.stream(values).collect(Matrix.Collector.toSingleRowMatrix());

        for (int i = 0; i < weightsList.size(); i++) {
            matrix = getResultsForSingleStage(matrix, i).getRow(1).toMatrix();
        }

        return matrix.getRow(0);
    }

    public Array<Matrix<Double>> thinkAndRemember(Double... values) {
        LinkedList<Matrix<Double>> result = new LinkedList<>();

        Matrix<Double> m1 = MatrixFactory.create(new Double[][]{{1.0}, {1.0}});
        Matrix<Double> m2 = Arrays.stream(values).collect(Matrix.Collector.toSingleRowMatrix());

        result.add(new DoubleMatrixMultiplier().apply(m1, m2));

        for (int i = 0; i < weightsList.size(); i++) {
            result.add(getResultsForSingleStage(result.getLast(), i));
        }

        return ArrayFactory.create(result);
    }

    private void addBiasArray(int size) {
        biasesList.add(ArrayFactory.create(size, v -> 0.0));
    }

    private void addWeightsMatrix(int width, int height) {
        weightsList.add(MatrixFactory.create(width, height, (i, j) -> 0.0));
    }

    private Matrix<Double> getResultsForSingleStage(Matrix<Double> matrix, int stage) {
        DoubleUnaryOperator op = v -> {
            Double res = function.apply(v);

            if (res < lowerBorder) {
                return lowerBorder;
            } else if (res > upperBorder) {
                return upperBorder;
            } else {
                return res;
            }
        };

        Matrix<Double> weights = this.weightsList.get(stage);
        Array<Double> biases = this.biasesList.get(stage + 1);

        Matrix<Double> result = MatrixFactory.createNullMatrix(weights.getWidth(), 2);

        for (int i = 0; i < weights.getWidth(); i++) {
            Matrix<Double> m1 = matrix.getRow(0).toMatrix();
            Matrix<Double> m2 = weights.getColumn(i).stream().collect(Matrix.Collector.toMatrixFromSingleRow(1));

            Double v = new DoubleMatrixMultiplier().apply(m1, m2).get(0, 0);

            result.set(0, i, v);
            result.set(1, i, op.applyAsDouble(v + biases.get(i)));
        }

        return result;
    }
    
    public static class WeightRetriveRequest {

        private int layer;

        private int row1;
        private int row2;

        public WeightRetriveRequest setInputLayerNode(int layer, int row) {
            this.layer = layer;
            this.row1 = row;
            return this;
        }

        public WeightRetriveRequest setOutputNode(int row) {
            this.row2 = row;
            return this;
        }
    }
    
    public static class WeightUpdateRequest {

        private int layer;

        private int row1;
        private int row2;

        private double value;

        public WeightUpdateRequest setInputLayerNode(int layer, int row) {
            this.layer = layer;
            this.row1 = row;
            return this;
        }

        public WeightUpdateRequest setOutputNode(int row) {
            this.row2 = row;
            return this;
        }

        public WeightUpdateRequest setValue(double value) {
            this.value = value;
            return this;
        }
    }
}
