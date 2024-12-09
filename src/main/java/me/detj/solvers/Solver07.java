package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver07 {
    public static void main(String[] args) {
        var input = Inputs.parseLabelledList("input_07.txt");

        long calibration = Utils.canEvaluateToLabel(input);
        System.out.printf("Solution Part 1: %d\n", calibration);

        long calibrationWithConcat = Utils.canEvaluateToLabelWithConcatenation(input);
        System.out.printf("Solution Part 2: %d\n", calibrationWithConcat);
    }
}
