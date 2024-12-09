package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver05 {
    public static void main(String[] args) {
        var rulesAndPages = Inputs.parsePageRules("input_05.txt");

        int sum = Utils.countMiddleNumbersOfValidUpdates(rulesAndPages.getRight(), rulesAndPages.getLeft());
        System.out.printf("Solution Part 1: %d\n", sum);

        int sumFixes = Utils.countMiddleNumbersOfFixedUpdates(rulesAndPages.getRight(), rulesAndPages.getLeft());
        System.out.printf("Solution Part 2: %d\n", sumFixes);

    }
}
