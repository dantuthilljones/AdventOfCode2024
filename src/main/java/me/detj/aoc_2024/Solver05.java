package me.detj.aoc_2024;

import me.detj.utils.Inputs;
import me.detj.utils.Point;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.swap;

public class Solver05 {
    public static void main(String[] args) {
        var rulesAndPages = Inputs.parsePageRules("2024/input_05.txt");

        int sum = countMiddleNumbersOfValidUpdates(rulesAndPages.getRight(), rulesAndPages.getLeft());
        System.out.printf("Solution Part 1: %d\n", sum);

        int sumFixes = countMiddleNumbersOfFixedUpdates(rulesAndPages.getRight(), rulesAndPages.getLeft());
        System.out.printf("Solution Part 2: %d\n", sumFixes);
    }

    private static int countMiddleNumbersOfValidUpdates(List<List<Integer>> pages, List<Point> rules) {
        int sum = 0;
        for (List<Integer> page : pages) {
            if (pageInRightOrder(page, rules)) {
                sum += page.get(page.size() / 2);
            }
        }
        return sum;
    }

    public static int countMiddleNumbersOfFixedUpdates(List<List<Integer>> pages, List<Point> rules) {
        int sum = 0;
        for (List<Integer> page : pages) {
            if (pageInRightOrder(page, rules)) {
                continue;
            }

            page = fixPage(page, rules);
            sum += page.get(page.size() / 2);
        }
        return sum;
    }

    private static List<Integer> fixPage(List<Integer> page, List<Point> rules) {
        page = new ArrayList<>(page);
        MultiValuedMap<Integer, Integer> rulesMap = generateRulesMap(rules);

        while (!pageInRightOrder(page, rules)) {
            for (int i = 0; i < page.size(); i++) {
                swapIfRulesSaySo(page, i, rulesMap);
            }
        }
        return page;
    }

    private static void swapIfRulesSaySo(List<Integer> pages, int i, MultiValuedMap<Integer, Integer> rulesMap) {
        int page = pages.get(i);
        Collection<Integer> mustBeAfter = rulesMap.get(page);
        if (mustBeAfter.isEmpty()) {
            return;
        }

        for (int j = 0; j < pages.size(); j++) {
            int otherPage = pages.get(j);
            if (mustBeAfter.contains(otherPage)) {
                swap(pages, i, j);
                return;// need to return because the page has changed index
            }
        }


    }

    private static MultiValuedMap<Integer, Integer> generateRulesMap(List<Point> rules) {
        MultiValuedMap<Integer, Integer> rulesMap = new HashSetValuedHashMap<>();
        for (Point rule : rules) {
            rulesMap.put(rule.getX(), rule.getY());
        }
        return rulesMap;
    }

    private static boolean pageInRightOrder(List<Integer> page, List<Point> rules) {
        for (Point rule : rules) {
            if (ruleBroken(page, rule)) {
                return false;
            }
        }
        return true;
    }

    private static boolean ruleBroken(List<Integer> pages, Point rule) {
        boolean seenY = false;

        for (Integer page : pages) {
            if (page.equals(rule.getY())) {
                seenY = true;
            } else if (seenY && page.equals(rule.getX())) {
                return true;
            }
        }
        return false;
    }
}
