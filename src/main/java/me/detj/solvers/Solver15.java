package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver15 {
    public static void main(String[] args) {
        var input = Inputs.parseLanternFishWarehouse("input_15.txt");

        long s1 = Utils.calculateLanternFishBoxPositions(input, false);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = Utils.calculateLanternFishBoxPositionsDouble(input, true);
        System.out.printf("Solution Part 2: %d\n", s2);
    }
}
