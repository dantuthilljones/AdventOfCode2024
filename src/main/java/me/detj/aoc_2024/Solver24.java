package me.detj.aoc_2024;

import lombok.Data;
import lombok.Value;
import me.detj.utils.Inputs;
import me.detj.utils.WireProblem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solver24 {
    public static void main(String[] args) {
        var input = Inputs.parseWireProblem("2024/input_24.txt");

        long s1 = getZNumber(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        String s2 = findSwaps(input);
        System.out.printf("Solution Part 2: %s\n", s2);
    }

    private static String findSwaps(WireProblem input) {
        doAddition(input.getGates(), 123456, 0);
        doAddition(input.getGates(), 123456, 1);

        doAddition(input.getGates(), 0, 123456);
        doAddition(input.getGates(), 1, 123456);
        return null;
    }

    private static long doAddition(List<WireProblem.Gate> gates, long x, long y) {
        Map<String, Boolean> initialValues = new HashMap<>();

        List<Boolean> xNumbers = toBinary(x);
        for (int i = 0; i < xNumbers.size(); i++) {
            initialValues.put(String.format("x%02d", i), xNumbers.get(i));
        }

        List<Boolean> yNumbers = toBinary(y);
        for (int i = 0; i < yNumbers.size(); i++) {
            initialValues.put(String.format("y%02d", i), yNumbers.get(i));
        }

        WireProblem wireProblem = new WireProblem(initialValues, gates);
        long result = getZNumber(wireProblem);

        System.out.println("   " + toString(xNumbers));
        System.out.println(" + " + toString(yNumbers));
        System.out.println(" = " + toString(toBinary(result)));
        System.out.printf("%d + %d = %d\n", x, y, result);
        return result;
    }

    private static List<Boolean> toBinary(long value) {
        List<Boolean> result = new ArrayList<>();
        for(int i = 0; i < 45; i++) {
            result.add(value % 2 == 1);
            value /= 2;
        }
        return result;
    }

    private static long fromBinary(List<Boolean> binary) {
        long result = 0;
        for (int i = 0; i < binary.size(); i++) {
            if (binary.get(i)) {
                result |= 1L << i;
            }
        }
        return result;
    }

    private static String toString(List<Boolean> binary) {
        StringBuilder sb = new StringBuilder();
        for (Boolean b : binary) {
            sb.append(b ? "1" : "0");
        }
        return sb.toString();
    }

    private static long getZNumber(WireProblem input) {
        Map<String, Wire> wires = resolveWires(input);

        List<Wire> zWires = wires.values().stream()
                .filter(wire -> wire.getName().startsWith("z"))
                .sorted(Comparator.comparing(Wire::getName))
                .toList();

        List<Boolean> binary = zWires.stream().map(wire -> wire.state).toList();

        return fromBinary(binary);
    }

    private static Map<String, Wire> resolveWires(WireProblem input) {
        Map<String, Wire> wires = new HashMap<>();

        for (WireProblem.Gate inputGate : input.getGates()) {
            Gate gate = new Gate();
            gate.setOperation(inputGate.getOperation());

            Wire outputWire = wires.computeIfAbsent(inputGate.getOutput(), Wire::new);
            gate.setOutput(outputWire);

            Wire leftWire = wires.computeIfAbsent(inputGate.getLeft(), Wire::new);
            leftWire.addConnection(gate, LEFT);

            Wire rightWire = wires.computeIfAbsent(inputGate.getRight(), Wire::new);
            rightWire.addConnection(gate, RIGHT);
        }

        input.getInitialValues().forEach((name, value) -> {
            Wire wire = wires.get(name);
            if (wire == null) {
                throw new IllegalArgumentException("Wire not found: " + name);
            }

            wire.setState(value);
        });

        return wires;
    }

    @Data
    private static class Gate {
        Wire output;
        String operation;
        boolean leftValue;
        boolean receivedLeft;
        boolean rightValue;
        boolean receivedRight;

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
