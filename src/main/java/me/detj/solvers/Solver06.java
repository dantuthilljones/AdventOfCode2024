package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver06 {
    public static void main(String[] args) {
        var map = Inputs.parseCharGrid("input_06.txt");

        int guardPositions = Utils.countGuardPositions(map.shallowCopy());
        System.out.printf("Solution Part 1: %d\n", guardPositions);

        int obstacleLoops = Utils.countObstacleLoops(map.shallowCopy());
        System.out.printf("Solution Part 2: %d\n", obstacleLoops);

    }
}
