package org.bready.network.trainer;

import java.util.ArrayList;
import java.util.List;
import org.bready.network.Network;
import org.bready.network.Network.WeightUpdateRequest;

public class NetworkTrainer {

    private final List<Double[]> entryDataList;
    private final List<Double[]> expectedDataList;

    private double trainSpeed;
    private int epochs;

    public NetworkTrainer() {
        this.entryDataList = new ArrayList<>();
        this.expectedDataList = new ArrayList<>();
        
        this.trainSpeed = 1.0;
        this.epochs = 0;
    }

    public void addTestValues(Double[] entryData, Double[] expectedData) {
        this.entryDataList.add(entryData);
        this.expectedDataList.add(expectedData);
    }

    public void setTrainSpeed(double trainSpeed) {
        this.trainSpeed = trainSpeed;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public void train(Network network) {
        NetworkModifier modifier = new NetworkModifier(network, trainSpeed);

        setRandomBiases(network);
        setRandomWeights(network);

        for (int i = 0; i < epochs; i++) {
            for (int j = 0; j < entryDataList.size(); j++) {
                modifier.train(entryDataList.get(j), expectedDataList.get(j));
            }
        }
    }

    private void setRandomBiases(Network network) {
        for (int i = 0; i < network.layersSize(); i++) {
            for (int j = 0; j < network.layerSize(i); j++) {
                network.setBiasAt(i, j, Math.random() * 4 - 2);
            }
        }
    }

    private void setRandomWeights(Network network) {
        for (int i = 0; i < network.layersSize() - 1; i++) {
            for (int j = 0; j < network.layerSize(i); j++) {
                for (int k = 0; k < network.layerSize(i + 1); k++) {
                    network.setWeightBetween(createWeightUpdateRequest(i, j, k));
                }
            }
        }
    }

    private WeightUpdateRequest createWeightUpdateRequest(int i, int j, int k) {
        return new WeightUpdateRequest().setInputLayerNode(i, j).setOutputNode(k).setValue(Math.random() * 4 - 2);
    }
}
