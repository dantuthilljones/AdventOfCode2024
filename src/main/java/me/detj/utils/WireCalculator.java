package me.detj.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.*;
import java.util.stream.IntStream;

@AllArgsConstructor
public class WireCalculator {

    private final Map<String, Wire> wires;
    private final Map<String, Gate> gates;

    public static WireCalculator fromGates(Collection<WireProblem.Gate> inputGates) {
        // 44 x inputs
        // 44 y inputs
        // 45 x outputs

        Map<String, Wire> wires = new HashMap<>();
        Map<String, Gate> gates = new HashMap<>();

        for (WireProblem.Gate inputGate : inputGates) {
            Gate gate = Gate.start(inputGate);
            gates.put(gate.string, gate);
            gate.setOperation(inputGate.getOperation());

            Wire outputWire = wires.computeIfAbsent(inputGate.getOutput(), Wire::new);
            gate.setOutput(outputWire);

            Wire leftWire = wires.computeIfAbsent(inputGate.getLeft(), Wire::new);
            leftWire.addConnection(gate, LEFT);

            Wire rightWire = wires.computeIfAbsent(inputGate.getRight(), Wire::new);
            rightWire.addConnection(gate, RIGHT);
        }

        return new WireCalculator(wires, gates);
    }

    public static Map<String, Boolean> buildInitialValues(long x, long y) {
        Map<String, Boolean> initialValues = new HashMap<>();

        List<Boolean> xNumbers = toBinary(x);
        for (int i = 0; i < xNumbers.size(); i++) {
            initialValues.put("x" + format(i), xNumbers.get(i));
        }

        List<Boolean> yNumbers = toBinary(y);
        for (int i = 0; i < yNumbers.size(); i++) {
            initialValues.put("y" + format(i), yNumbers.get(i));
        }

        return initialValues;
    }

    private static String format(long n) {
        if(n >= 10) {
            return "" + n;
        } else {
            return "0" + n;
        }
    }

    public long calculate(long x, long y) {
        Map<String, Boolean> initialValues = buildInitialValues(x, y);
        return calculate(initialValues);
    }

    public long calculate(Map<String, Boolean> input) {
        gates.values().forEach(Gate::reset);
        input.forEach((name, value) -> {
            Wire wire = wires.get(name);
            if (wire == null) {
                throw new IllegalArgumentException("Wire not found: " + name);
            }
            wire.setState(value);
        });
        return getOutput();
    }

    private long getOutput() {
        List<Boolean> binary = IntStream.range(0, 46)
                .mapToObj(i -> wires.get("z" + format(i)))
                .map(wire -> wire.state)
                .toList();

        return fromBinary(binary);
    }

    public Collection<String> getOnGates() {
        return gates.values()
                .stream()
                .filter(gate -> gate.output.state)
                .map(Gate::getString)
                .toList();
    }

    public Collection<String> getOffGates() {
        return gates.values()
                .stream()
                .filter(gate -> !gate.output.state)
                .map(Gate::getString)
                .toList();
    }

    public static List<Boolean> toBinary(long value) {
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            result.add(value % 2 == 1);
            value /= 2;
        }
        return result;
    }

    public static long fromBinary(List<Boolean> binary) {
        long result = 0;
        for (int i = 0; i < binary.size(); i++) {
            if (binary.get(i)) {
                result |= 1L << i;
            }
        }
        return result;
    }

    public static String bToString(List<Boolean> binary) {
        StringBuilder sb = new StringBuilder();
        for (Boolean b : binary) {
            sb.append(b ? "1" : "0");
        }
        return sb.toString();
    }

    public static String bToString(long num) {
        return bToString(toBinary(num));
    }

    public List<String> gatesAlongPath(String inputWire, String outputWire) {
        Wire start = wires.get(inputWire);

        List<String> path = new ArrayList<>();
        findPathTo(path, start, outputWire);

        return path.reversed();
    }

    private boolean findPathTo(List<String> path, Wire wire, String outputWire) {
        if (wire.name.equals(outputWire)) {
            return true;
        }
        for (Connection c : wire.connections) {
            if (findPathTo(path, c.gate.getOutput(), outputWire)) {
                path.add(c.gate.string);
                return true;
            }
        }
        return false;
    }


    @Data
    private static class Gate {
        Wire output;
        String operation;
        boolean leftValue;
        boolean receivedLeft;
        boolean rightValue;
        boolean receivedRight;
        String string;

        private static Gate start(WireProblem.Gate input) {
            String string = String.format(
                    "%s %s %s -> %s", input.getLeft(), input.getOperation(), input.getRight(), input.getOutput());
            Gate gate = new Gate();
            gate.string = string;
            return gate;
        }

        private void recieveLeft(boolean value) {
            leftValue = value;
            receivedLeft = true;
            run();
        }

        private void recieveRight(boolean value) {
            rightValue = value;
            receivedRight = true;
            run();
        }

        private void run() {
            if (!receivedLeft || !receivedRight) {
                return;
            }

            switch (operation) {
                case "AND" -> output.setState(leftValue && rightValue);
                case "OR" -> output.setState(leftValue || rightValue);
                case "XOR" -> output.setState(leftValue ^ rightValue);
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            }
        }

        private void reset() {
            receivedLeft = false;
            receivedRight = false;
        }

    }

    private static final boolean LEFT = true, RIGHT = false;

    @Data
    private static class Wire {

        private Wire(String name) {
            this.name = name;
            connections = new ArrayList<>();
        }

        String name;
        boolean state;
        List<Connection> connections;

        private void setState(boolean state) {
            this.state = state;
            for (Connection connection : connections) {
                if (connection.side == LEFT) {
                    connection.gate.recieveLeft(state);
                } else {
                    connection.gate.recieveRight(state);
                }
            }
        }

        public void addConnection(Gate gate, boolean side) {
            Connection connection = new Connection(gate, side);
            connections.add(connection);
        }
    }

    @Value
    private static class Connection {
        Gate gate;
        boolean side;
    }
}
