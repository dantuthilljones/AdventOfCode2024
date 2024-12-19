package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Point;
import me.detj.utils.Utils;

public class Solver18 {
    public static void main(String[] args) {
        var input = Inputs.parseListOfPoints("input_18.txt");

        int s1 = Utils.lengthOfShortestPathInComputer(input, 1024);
        System.out.printf("Solution Part 1: %d\n", s1);

        Point s2 = Utils.lastWithSolution(input);
        System.out.printf("Solution Part 2: %s\n", s2);
    }
}
