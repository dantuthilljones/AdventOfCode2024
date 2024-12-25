package me.detj.aoc_2024;

import me.detj.utils.Inputs;
import me.detj.utils.LockProblem;

import java.util.List;

public class Solver25 {

    public static void main(String[] args) {
        var input = Inputs.parseKeysAndLocks("input_25.txt");

        long s1 = countValidPairs(input);
        System.out.printf("Solution Part 1: %d\n", s1);

//        String s2 = countValidPairs(input);
//        System.out.printf("Solution Part 2: %s\n", s2);
    }

    private static long countValidPairs(LockProblem input) {
        long pairs = 0;

        for(List<Integer> lock : input.getLocks()) {
            for(List<Integer> key : input.getKeys()) {
                if (lockFitsKey(lock, key)) {
                    pairs++;
                }
            }
        }
        return pairs;
    }

    private static boolean lockFitsKey(List<Integer> lock, List<Integer> key) {
        for (int i = 0; i < lock.size(); i++) {
            if (lock.get(i) +  key.get(i) > 5) {
                return false;
            }
        }
        return true;
    }
}
