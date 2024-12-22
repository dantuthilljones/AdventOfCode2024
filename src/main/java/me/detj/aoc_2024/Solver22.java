package me.detj.aoc_2024;

import me.detj.utils.Inputs;

import java.util.List;

public class Solver22 {
    public static void main(String[] args) {
        var input = Inputs.parseListOfNumbers("input_22.txt");

        long s1 = calculateSecretNumberSum(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = calculateBestNumber(input);
        System.out.printf("Solution Part 2: %d\n", s2);
    }


    private static long calculateSecretNumberSum(List<Long> input) {
        long sum = 0;

        for (long secret : input) {
            sum += calculateNthSecretNumber(secret, 2_000);
        }
        return sum;
    }

    private static long calculateNthSecretNumber(long secret, int n) {
        for (int i = 0; i < n; i++) {
            secret = calculateSecretNumber(secret);
        }
        return secret;
    }

    public static long calculateSecretNumber(long secret) {
        // * 64, mix in to secret number, prune secret number
        long x64 = secret << 6; // * 64
        secret = mix(secret, x64);
        secret = prune(secret);


        // divide by 32, mix in to, prune
        long d32 = secret >> 5; // / 32
        secret = mix(secret, d32);
        secret = prune(secret);

        // * 2048. Then, mix, then prune
        long x2048 = secret << 11; // * 2048
        secret = mix(secret, x2048);
        secret = prune(secret);

        return secret;
    }

    private static long mix(long secret, long value) {
        return secret ^ value;
    }

    private static long prune(long secret) {
        return secret % 16777216;
    }


    private static long calculateBestNumber(List<Long> input) {
        return 0;
    }
}
