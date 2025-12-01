package me.detj.aoc_2024;

import me.detj.utils.Inputs;
import me.detj.utils.OpcodeComputer;

import java.util.List;
import java.util.Objects;

public class Solver17 {
    public static void main(String[] args) {
        var computer = Inputs.parseComputer("2024/input_17.txt");
        computer.runProgram();

        String formattedOutput = computer.getFormattedOutput();
        System.out.printf("Solution Part 1: %s\n", formattedOutput);

        long s2 = findComputerInput(computer.getInstructions());
        System.out.printf("Solution Part 2: %d\n", s2);
    }

    private static long findComputerInput(List<Integer> program) {
        return findComputerInput(program, program.size() - 1, 0);
    }

    private static long findComputerInput(List<Integer> program, int programIndex, long a) {
        if (programIndex < 0) {
            return a;
        }

        for (long test = 0; test < 8; test++) {
            long candidate = (a << 3) + test;
            OpcodeComputer opcodeComputer = OpcodeComputer.of(candidate, 0, 0, program);
            opcodeComputer.runProgram();
            List<Integer> output = opcodeComputer.getOutput();
            if (programsMatch(output, program, programIndex)) {
                long newA = findComputerInput(program, programIndex - 1, candidate);
                if (newA != -1) {
                    return newA;
                }
            }
        }
        return -1;
    }

    private static boolean programsMatch(List<Integer> output, List<Integer> program, int programIndex) {
        if (program.size() - programIndex != output.size()) {
            return false;
        }
        for (int i = 0; i < output.size(); i++) {
            if (!Objects.equals(output.get(i), program.get(programIndex + i))) {
                return false;
            }
        }
        return true;
    }

    private static long findComputerInputBruteForce(List<Integer> program) {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.printf("Cores %d\n", cores);

        long searchSpace = (long) Math.pow(8, 16);
        long step = searchSpace / cores;

        System.out.printf("Search space 0 -> %d\n", searchSpace);
        System.out.printf("Step %d\n", step);

        for (int threadNum = 0; threadNum < cores; threadNum++) {
            Thread thread = getThread(program, threadNum, step);
            thread.start();
        }
        return -1;
    }

    private static Thread getThread(List<Integer> program, int i, long step) {
        long start = i * step;
        long end = (i + 1) * step;
        return new Thread(() -> {
            System.out.printf("Started thread %d searching range %d -> %d\n", i, start, end);
            long[] programArray = program.stream().mapToLong(Integer::longValue).toArray();
            for (long a = start; a < end; a++) {
                if (testA(programArray, a)) {
                    System.out.println(a);
                }
            }
        });
    }

    private static boolean testA(long[] program, long a) {
        for (long out : program) {
            long c = (a % 8) ^ 1;
            long b = (a >> c) % 8;
            if (out != b) {
                return false;
            }
            a = a >> 3;
        }
        return true;
    }
}
