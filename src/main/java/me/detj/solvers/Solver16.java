package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.MazeResult;
import me.detj.utils.Utils;

public class Solver16 {
    public static void main(String[] args) {
        var input = Inputs.parseCharGrid("input_16.txt");

        MazeResult result = Utils.calculateReindeerMaze(input);
        System.out.printf("Solution Part 1: %d\n", result.getCost());
        System.out.printf("Solution Part 2: %d\n", result.getPointsInPaths().size());
    }
}
