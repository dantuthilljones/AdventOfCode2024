package me.detj.aoc_2024;

import me.detj.utils.ButtonRobot;
import me.detj.utils.Inputs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solver21 {
    public static void main(String[] args) {
        var input = Inputs.readLines("input_21.txt");

        long s1 = calculateComplexities(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = calculateComplexities(input);
        System.out.printf("Solution Part 2: %d\n", s2);
    }

    private static long calculateComplexities(List<String> codes) {
        List<ButtonRobot> robots = List.of(
                ButtonRobot.directionalRobot(),
                ButtonRobot.directionalRobot(),
                ButtonRobot.keyPadRobot()
        );

        long sum = 0;
        for (String code : codes) {
            String humanInput = calcHumanInput(code, robots);
            sum += calculateComplexity(humanInput, code);
        }
        return sum;
    }

    private static int calculateComplexity(String humanInput, String code) {
        String numericComponent = code.substring(0, 3);
        int complexity = Integer.parseInt(numericComponent);
        return humanInput.length() * complexity;
    }

    private static String calcHumanInput(String code, List<ButtonRobot> robots) {
        Set<String> allSequences = calcPresses(robots, robots.size() - 1, Set.of(code));
        return getShortestString(allSequences);
    }

    public static String getShortestString(Collection<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return null;
        }

        List<String> shortests = new ArrayList<>();
        shortests.add(strings.iterator().next());
        for (String str : strings) {
            if (str.length() < shortests.get(0).length()) {
                shortests.clear();
                shortests.add(str);
            } else if (str.length() == shortests.get(0).length()) {
                shortests.add(str);
            }
        }
        System.out.println();
        shortests.forEach(System.out::println);
        return shortests.get(0);
    }

    private static Set<String> calcPresses(List<ButtonRobot> robots, int i, Set<String> prevInputs) {
        ButtonRobot robot = robots.get(i);

        Set<String> nextInputs = new HashSet<>();
        for (String input : prevInputs) {
            nextInputs.addAll(robot.possibleSequencesToEnter(input));
        }

        if (i == 0) {
            return nextInputs;
        } else {
            return calcPresses(robots, i - 1, nextInputs);
        }
    }

    public static long calculateComplexities2(List<String> codes) {
        List<ButtonRobot> robots = List.of(
                ButtonRobot.directionalRobot(),
                ButtonRobot.directionalRobot(),
                ButtonRobot.keyPadRobot()
        );

        long sum = 0;
        for (String code : codes) {
            String humanInput = calcHumanInput(code, robots);
            sum += calculateComplexity(humanInput, code);
        }
        return sum;
    }
}