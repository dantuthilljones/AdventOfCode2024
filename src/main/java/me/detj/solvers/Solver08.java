package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver08 {
    public static void main(String[] args) {
        var map = Inputs.parseCharGrid("input_08.txt");

        int antiNodes = Utils.countAntiNodes(map.shallowCopy());
        System.out.printf("Solution Part 1: %d\n", antiNodes);

        int antiNodesWithHarmonics = Utils.countAntiNodesWithHarmonics(map.shallowCopy());
        System.out.printf("Solution Part 2: %d\n", antiNodesWithHarmonics);
    }
}
