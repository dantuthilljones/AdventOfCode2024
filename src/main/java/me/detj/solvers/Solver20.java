package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver20 {
    public static void main(String[] args) {
        var input = Inputs.parseCharGrid("input_20.txt");

        int s1 = Utils.countCheats(input, 100, 2);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = Utils.countCheats(input, 100, 20);
        System.out.printf("Solution Part 2: %d\n", s2);
    }
}
