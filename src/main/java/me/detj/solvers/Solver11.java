package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver11 {
    public static void main(String[] args) {
        var stones = Inputs.parseIntList("input_11.txt");

        long numStones = Utils.countStonesAfterBlinkingDP(stones, 25);
        System.out.printf("Solution Part 1: %d\n", numStones);

        long numStones75 = Utils.countStonesAfterBlinkingDP(stones, 75);
        System.out.printf("Solution Part 2: %d\n", numStones75);
    }
}
