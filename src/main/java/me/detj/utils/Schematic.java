package me.detj.utils;

import lombok.Value;

import java.util.List;

@Value
public class Schematic {
    List<Boolean> indicatorLights;
    List<List<Integer>> buttons;
    List<Integer> joltages;
}
