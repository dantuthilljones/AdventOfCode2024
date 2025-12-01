package me.detj.aoc_2025;

import me.detj.utils.DTPair;
import me.detj.utils.Inputs;

import java.io.IOException;
import java.util.List;

public class Solver01 {

    public static void main(String[] args) throws IOException {
        var input = Inputs.parseDialRotations("2025/input_01.txt");
        System.out.printf("Total Zeros: %d\n", countZeros(input));
        System.out.printf("Total Zeros 2: %d\n", countZerosPart2BF(input));
    }

    private static int countZeros(List<DTPair<Character, Integer>> rotations) {
        int zeros = 0;
        int current = 50;

        for (DTPair<Character, Integer> rotation : rotations) {
            int direction = rotation.getLeft().equals('L') ? -1 : 1;
            int steps = rotation.getRight() * direction;

            current += steps;
            while (current < 0) current += 100;
            while (current >= 100) current -= 100;
            if (current == 0) zeros++;
        }

        return zeros;
    }

    private static int countZerosPart2BF(List<DTPair<Character, Integer>> rotations) {
        int zeros = 0;
        int current = 50;

        for (DTPair<Character, Integer> rotation : rotations) {
            int direction = rotation.getLeft().equals('L') ? -1 : 1;
            int steps = rotation.getRight() * direction;


            for (int step = steps; steps != 0; steps -= direction) {
                current += direction;
                while (current < 0) current += 100;
                while (current >= 100) current -= 100;
                if (current == 0) {
                    zeros++;
                }
            }

        }

        return zeros;
    }
}
