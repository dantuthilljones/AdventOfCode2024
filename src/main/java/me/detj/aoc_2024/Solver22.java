package me.detj.aoc_2024;

import lombok.SneakyThrows;
import me.detj.utils.Inputs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Solver22 {
    public static void main(String[] args) {
        var input = Inputs.parseListOfNumbers("2024/input_22.txt");

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
            secret = calculateNextSecretNumber(secret);
        }
        return secret;
    }

    public static long calculateNextSecretNumber(long secret) {
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
        List<List<Long>> allPrices = new ArrayList<>(input.size());
        List<List<Long>> allDifferences = new ArrayList<>(input.size());

        for (long secret : input) {
            List<Long> prices = calculatePrices(secret);
            allPrices.add(prices);
            allDifferences.add(differences(prices));
        }

        return calculateBestSequenceMultithreaded(allPrices, allDifferences);
    }

    private static long calculateBestSequence(List<List<Long>> allPrices, List<List<Long>> allDifferences) {
        long mostNanas = 0;
        for (long[] code : allCodes()) {
            long bananas = countBananas(code, allPrices, allDifferences);
            if (bananas > mostNanas) {
                mostNanas = bananas;
            }
        }
        return mostNanas;
    }

    @SneakyThrows
    private static long calculateBestSequenceMultithreaded(List<List<Long>> allPrices, List<List<Long>> allDifferences) {
        AtomicLong mostNanas = new AtomicLong(0);

        long[][] codes = allCodes();

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.printf("Cores %d\n", cores);

        int searchSpace = codes.length;
        int step = searchSpace / cores;

        System.out.printf("Search space 0 -> %d\n", searchSpace);
        System.out.printf("Step %d\n", step);

        List<Thread> threads = new ArrayList<>();

        for (int threadNum = 0; threadNum < cores; threadNum++) {
            Thread thread = makeThread(codes, mostNanas, threadNum, step, allPrices, allDifferences);
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }
        return mostNanas.get();
    }

    private static Thread makeThread(long[][] codes, AtomicLong mostNanasResult, int threadNum, int step, List<List<Long>> allPrices, List<List<Long>> allDifferences) {
        return new Thread(() -> {
            long mostNanas = 0;

            int start = threadNum * step;
            int end = (threadNum + 1) * step;
            for (int i = start; i < end; i++) {
                long bananas = countBananas(codes[i], allPrices, allDifferences);
                if (bananas > mostNanas) {
                    mostNanas = bananas;
                }
            }
            long finalMostNanas = mostNanas;
            mostNanasResult.updateAndGet(value -> Math.max(value, finalMostNanas));
        });
    }


    private static long countBananas(long[] code, List<List<Long>> allPrices, List<List<Long>> allDifferences) {
        long bananas = 0;
        for (int i = 0; i < allPrices.size(); i++) {
            List<Long> prices = allPrices.get(i);
            List<Long> differences = allDifferences.get(i);
            bananas += countBananasInner(code, prices, differences);
        }
        return bananas;
    }

    private static long countBananasInner(long[] code, List<Long> allPrices, List<Long> allDifferences) {
        for (int i = 4; i < allPrices.size(); i++) {
            if (allDifferences.get(i).equals(code[3])
                    && allDifferences.get(i - 1).equals(code[2])
                    && allDifferences.get(i - 2).equals(code[1])
                    && allDifferences.get(i - 3).equals(code[0])
            ) {
                return allPrices.get(i);
            }
        }
        return 0;
    }

    private static long[][] allCodes() {
        int numCodes = 19 * 19 * 19 * 19;
        long[][] codes = new long[numCodes][];
        int index = 0;
        for (int i = -9; i < 10; i++) {
            for (int j = -9; j < 10; j++) {
                for (int k = -9; k < 10; k++) {
                    for (int l = -9; l < 10; l++) {
                        codes[index] = new long[]{i, j, k, l};
                        index++;
                    }
                }
            }
        }
        return codes;
    }

    private static List<Long> calculatePrices(long secret) {
        List<Long> prices = new ArrayList<>();
        for (int i = 0; i < 2001; i++) {
            prices.add(secret % 10);
            secret = calculateNextSecretNumber(secret);
        }
        return prices;
    }

    private static List<Long> differences(List<Long> prices) {
        List<Long> differences = new ArrayList<>(prices.size());
        differences.add(null);
        for (int i = 1; i < prices.size(); i++) {
            differences.add(prices.get(i) - prices.get(i - 1));
        }
        return differences;
    }
}
