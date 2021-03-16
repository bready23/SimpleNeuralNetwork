package org.bready.network.trainer;

import java.util.LinkedList;

import org.bready.network.Network;
import org.bready.network.Network.WeightRetriveRequest;
import org.bready.network.Network.WeightUpdateRequest;
import org.bready.util.array.Array;
import org.bready.util.array.ArrayFactory;
import org.bready.util.matrix.Matrix;
import org.bready.util.matrix.MatrixFactory;
import org.bready.util.transformer.DoubleMatrixMultiplier;

class NetworkModifier {

    private final Network network;
    private final double trainSpeed;

    NetworkModifier(Network network, double trainSpeed) {
        this.network = network;
        this.trainSpeed = trainSpeed;
    }

    void train(Double[] entryData, Double[] expectedData) {
        Array<Matrix<Double>> results = network.thinkAndRemember(entryData);
        Array<Array<Double>> errors = getErrors(results, expectedData);

        for (int i = 0; i < errors.size(); i++) {
            updateBiases(errors.get(i), i + 1);
            updateWeights(results, errors.get(i), i);
        }
    }

    void updateBiases(Array<Double> errors, int layer) {
        for (int i = 0; i < errors.size(); i++) {
            network.setBiasAt(layer, i, errors.get(i) * trainSpeed);
        }
    }

    void updateWeights(Array<Matrix<Double>> results, Array<Double> errors, int inputLayer) {
        for (int i = 0; i < network.layerSize(inputLayer + 1); i++) {
            for (int j = 0; j < network.layerSize(inputLayer); j++) {
                WeightUpdateRequest request = new WeightUpdateRequest()
                        .setInputLayerNode(inputLayer, j)
                        .setOutputNode(i)
                        .setValue(trainSpeed * errors.get(i) * results.get(inputLayer).get(1, j));

                network.setWeightBetween(request);
            }
        }
    }

    private Array<Array<Double>> getErrors(Array<Matrix<Double>> results, Double[] expectedData) {
        LinkedList<Array<Double>> errors = new LinkedList<>();

        for (int i = network.layersSize() - 1; i > 0; i--) {
            Array<Double> array = i == network.layersSize() - 1 
                    ? getOuterLayerErrors(results.get(i), expectedData)
                    : getLayerErrors(errors.getFirst(), i);

            for (int j = 0; j < array.size(); j++) {
                array.set(j, array.get(j) * network.getActivationFunction().derivative(results.get(i).get(0, j)));
            }

            errors.addFirst(array);
        }

        return ArrayFactory.create(errors);
    }

    private Array<Double> getOuterLayerErrors(Matrix<Double> matrix, Double[] expectedData) {
        Array<Double> result = ArrayFactory.createNullArray(matrix.getWidth());

        for (int i = 0; i < matrix.getWidth(); i++) {
            result.set(i, expectedData[i] - matrix.get(1, i));
        }

        return result;
    }

    private Array<Double> getLayerErrors(Array<Double> errors, Integer layer) {
        Matrix<Double> m1 = errors.toMatrix();
        Matrix<Double> m2 = prepareWeightsMatrix(layer);

        return new DoubleMatrixMultiplier().apply(m1, m2).getRow(0);
    }

    private Matrix<Double> prepareWeightsMatrix(int inputLayer) {
        Matrix<Double> result = MatrixFactory.createNullMatrix(network.layerSize(inputLayer),
                network.layerSize(inputLayer + 1));

        for (int i = 0; i < network.layerSize(inputLayer); i++) {
            for (int j = 0; j < network.layerSize(inputLayer + 1); j++) {
                result.set(j, i, network.getWeightBetween(createWeightUpdateRequest(inputLayer, i, j)));
            }
        }

        return result;
    }

    private WeightRetriveRequest createWeightUpdateRequest(int i, int j, int k) {
        return new WeightRetriveRequest().setInputLayerNode(i, j).setOutputNode(k);
    }
}
