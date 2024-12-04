package me.detj.solvers;

import me.detj.utils.Inputs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solver3 {
    public static void main(String[] args) {
        solveFirst();
        solveSecond();
    }

    private static void solveFirst() {
        Pattern pattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
        String input = Inputs.readFile("input_03.txt");

        Matcher matcher = pattern.matcher(input);

        int total = 0;
        while (matcher.find()) {
            int first = Integer.parseInt(matcher.group(1));
            int second = Integer.parseInt(matcher.group(2));
            total += (first * second);
        }

        System.out.printf("Total: %d\n", total);
    }

    private static void solveSecond() {
        Pattern pattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)|do\\(\\)|don't\\(\\)");
        String input = Inputs.readFile("input_03.txt");

        Matcher matcher = pattern.matcher(input);

        boolean on = true;
        int total = 0;
        while (matcher.find()) {
            String instruction = matcher.group(0);
            switch (instruction) {
                case "do()" -> on = true;
                case "don't()" -> on = false;
                default -> {// only remaining match is mul
                    if(!on) continue;
                    int first = Integer.parseInt(matcher.group(1));
                    int second = Integer.parseInt(matcher.group(2));
                    total += (first * second);
                }
            }
        }

        System.out.printf("Total: %d\n", total);
    }
}
