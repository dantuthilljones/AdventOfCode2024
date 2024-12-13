package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver13 {
    public static void main(String[] args) {
        var input = Inputs.parseClawMachines("input_13.txt");

        long s1 = Utils.fewestTokens(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = Utils.fewestTokensBigger(input);
        System.out.printf("Solution Part 2: %d\n", s2);
    }
}
