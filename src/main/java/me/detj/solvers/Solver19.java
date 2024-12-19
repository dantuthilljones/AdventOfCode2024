package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver19 {
    public static void main(String[] args) {
        var input = Inputs.parseTowelProblem("input_19.txt");

        int s1 = Utils.countPossibleTowelPatterns(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = Utils.countAllPossibleTowelPatterns(input);
        System.out.printf("Solution Part 2: %d\n", s2);
    }
}
