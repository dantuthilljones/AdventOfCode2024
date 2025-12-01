package me.detj.aoc_2024;

import me.detj.utils.Inputs;
import me.detj.utils.TowelProblem;
import me.detj.utils.Trie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solver19 {
    public static void main(String[] args) {
        var input = Inputs.parseTowelProblem("2024/input_19.txt");

        int s1 = countPossibleTowelPatterns(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = countAllPossibleTowelPatterns(input);
        System.out.printf("Solution Part 2: %d\n", s2);
    }

    private static int countPossibleTowelPatterns(TowelProblem problem) {
        Trie trie = Trie.build(problem.getTowels());

        int possible = 0;
        for (String patterns : problem.getPatterns()) {
            if (towelPatternIsPossible(trie, patterns)) {
                possible++;
            }
        }
        return possible;
    }


    private static long countAllPossibleTowelPatterns(TowelProblem problem) {
        Trie trie = Trie.build(problem.getTowels());
        Map<String, Long> known = new HashMap<>();

        long count = 0;
        for (String patterns : problem.getPatterns()) {
            count += countPossibleTowelPatterns(trie, known, patterns);
        }
        return count;
    }

    private static long countPossibleTowelPatterns(Trie trie, Map<String, Long> known, String pattern) {
        Long knownCount = known.get(pattern);
        if (knownCount != null) {
            return knownCount;
        }

        if (pattern.isEmpty()) {
            return 1;
        }
        long count = 0;
        List<String> nextTowels = trie.getPrefixingWords(pattern);
        for (String nextTowel : nextTowels) {
            String subPattern = pattern.substring(nextTowel.length());
            count += countPossibleTowelPatterns(trie, known, subPattern);
        }
        known.put(pattern, count);
        return count;
    }

    private static boolean towelPatternIsPossible(Trie trie, String pattern) {
        if (pattern.isEmpty()) {
            return true;
        }
        int count = 0;
        List<String> nextTowels = trie.getPrefixingWords(pattern);
        for (String nextTowel : nextTowels) {
            String subPattern = pattern.substring(nextTowel.length());
            if (towelPatternIsPossible(trie, subPattern)) {
                return true;
            }
        }
        return false;
    }
}
