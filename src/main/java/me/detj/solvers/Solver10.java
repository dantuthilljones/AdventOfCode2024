package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver10 {
    public static void main(String[] args) {
        var map = Inputs.parseDenseIntGrid("input_10.txt");

        long score = Utils.scoreTrails(map);
        System.out.printf("Solution Part 1: %d\n", score);

        long scoreDistinct = Utils.scoreTrailsDistinct(map);
        System.out.printf("Solution Part 2: %d\n", scoreDistinct);
    }
}
