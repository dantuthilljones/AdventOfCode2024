package me.detj.aoc_2024;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver17 {
    public static void main(String[] args) {
        var computer = Inputs.parseComputer("input_17.txt");
        computer.runProgram();

        String formattedOutput = computer.getFormattedOutput();
        System.out.printf("Solution Part 1: %s\n", formattedOutput);

        long s2 = Utils.findComputerInput(computer.getInstructions());
        System.out.printf("Solution Part 2: %d\n", s2);
    }
}
