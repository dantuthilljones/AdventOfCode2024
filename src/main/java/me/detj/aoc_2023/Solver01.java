package me.detj.aoc_2023;

import me.detj.utils.Inputs;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Solver01 {

    public static void main(String[] args) throws IOException {
        var input = Inputs.parseCharLists("2023/input_01.txt");
        System.out.printf("Solution part 1: %d\n", calibrationSum(input));
        System.out.printf("Solution part 2: %d\n", calibrationSum(input));
    }

    private static int calibrationSum(List<List<Character>> input) {
        int sum = 0;

        for (List<Character> chars : input) {
            int firstDigit = 0;
            for (char c : chars) {
                int val = tryParse(c);
                if (val != -1) {
                    firstDigit = val;
                    break;
                }
            }

            int lastDigit = 0;
            for (char c : chars.reversed()) {
                int val = tryParse(c);
                if (val != -1) {
                    lastDigit = val;
                    break;
                }
            }
            sum += firstDigit * 10 + lastDigit;
        }
        return sum;
    }

    private static int tryParse(char c) {
        try {
            return Integer.parseInt(String.valueOf(c));
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    private static final Map<String, Integer> numbers = Map.of(
            "one", 1,
            "two", 2,
            "three", 3,
            "four", 4,
            "five", 5,
            "six", 6,
            "seven", 7,
            "eight", 8,
            "nine", 9
    );

    private static final Map<String, Integer> numbersReverse = MapUtils.transformedMap(numbers,
            StringUtils::reverse,
            value -> value);

    private static int calibrationSum2(List<List<Character>> input) {

        int sum = 0;

        for (List<Character> chars : input) {
            int firstDigit = 0;
            for (int i = 0; i < chars.size(); i++) {
                int val = tryParse(chars.get(i));
                if (val != -1) {
                    firstDigit = val;
                    break;
                }
                for(String numString : numbers.keySet()) {
                    for(int j = 0; j < numString.length(); j++) {
                        boolean match = true;
                        if (numString.charAt(j) != chars.get(i + j)) {
                            match = false;
                        }
                        if (match) {
                            firstDigit = numbers.get(numString);
                        }
                    }
                }
            }

            int lastDigit = 0;
            for (char c : chars.reversed()) {
                int val = tryParse(c);
                if (val != -1) {
                    lastDigit = val;
                    break;
                }
            }
            sum += firstDigit * 10 + lastDigit;
        }
        return sum;
    }
}
