package me.detj.utils;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class WireProblem {
    Map<String, Boolean> initialValues;
    List<Gate> gates;

    @Value
    public static class Gate {
        String left;
        String operation;
        String right;
        String output;

        @Override
        public String toString() {
            return String.format("%s %s %s -> %s", left, operation, right, output);
        }
    }

    public static Gate gateFromString(String s) {
        String[] split = s.split(" ");
        String left = split[0];
        String gate = split[1];
        String right = split[2];
        // skip the arrow
        String out = split[4];
        return new WireProblem.Gate(left, gate, right, out);
    }
}
