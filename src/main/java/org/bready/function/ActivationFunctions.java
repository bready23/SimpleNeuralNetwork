package org.bready.function;

import java.util.function.UnaryOperator;

public class ActivationFunctions {

    public static ActivationFunction identity() {
        return new FunctionImpl(v -> v, v -> 1.0);
    }

    public static ActivationFunction binaryStep() {
        return new FunctionImpl(v -> v < 0 ? 0.0 : 1.0, v -> v == 0 ? Double.NaN : 0.0);
    }
    
    public static ActivationFunction sigmoid() {
        UnaryOperator<Double> operator = v -> 1 / (1 + Math.exp(-v));
        return new FunctionImpl(operator, v -> operator.apply(v) * (1 - operator.apply(v)));
    }

    public static ActivationFunction tanh() {
        UnaryOperator<Double> operator = v -> (Math.exp(v) - Math.exp(-v)) / (Math.exp(v) + Math.exp(-v));
        return new FunctionImpl(operator, v -> 1 - Math.pow(operator.apply(v), 2));
    }

    private ActivationFunctions() {}
    
    private static class FunctionImpl implements ActivationFunction {

        private final UnaryOperator<Double> operator1;
        private final UnaryOperator<Double> operator2;

        FunctionImpl(UnaryOperator<Double> operator1, UnaryOperator<Double> operator2) {
            this.operator1 = operator1;
            this.operator2 = operator2;
        }

        @Override
        public Double apply(Double v) {
            return operator1.apply(v);
        }

        @Override
        public Double derivative(Double v) {
            return operator2.apply(v);
        }

    }
}
