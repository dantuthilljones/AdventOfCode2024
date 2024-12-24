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
    }
}
