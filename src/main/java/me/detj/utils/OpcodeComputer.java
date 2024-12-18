package me.detj.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class OpcodeComputer {
    long a;
    long b;
    long c;

    List<Integer> instructions;
    int instructionPointer;

    List<Integer> output;

    public static OpcodeComputer of(long a, long b, long c, List<Integer> instructions) {
        return new OpcodeComputer(a, b, c, instructions, 0, new ArrayList<>());
    }

    public String getFormattedOutput() {
        return output.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public void runProgram() {
        while (instructionPointer < instructions.size()) {
            int opcode = instructions.get(instructionPointer);
            int operand = instructions.get(instructionPointer + 1);

            switch (opcode) {
                case 0 -> adv(operand);
                case 1 -> bxl(operand);
                case 2 -> bst(operand);
                case 3 -> jnz(operand);
                case 4 -> bxc(operand);
                case 5 -> out(operand);
                case 6 -> bdv(operand);
                case 7 -> cdv(operand);
            }
        }
    }

    private void adv(int operand) {
        long comboOperand = getComboOperand(operand);
        a = a >> comboOperand;
        instructionPointer += 2;
    }

    private static long long2Power(long comboOperand) {
        long result = 1;
        for (long i = 0; i < comboOperand; i++) {
            result *= 2;
        }
        return result;
    }

    private void bxl(int operand) {
        b = b ^ operand;
        instructionPointer += 2;
    }

    private void bst(int operand) {
        long comboOperand = getComboOperand(operand);
        b = comboOperand % 8;
        instructionPointer += 2;
    }

    private void jnz(int operand) {
        if (a == 0) {
            instructionPointer += 2;
            return;
        }
        instructionPointer = operand;
    }

    private void bxc(int operand) {
        b = b ^ c;
        instructionPointer += 2;
    }

    private void out(int operand) {
        long comboOperand = getComboOperand(operand);
        output.add((int) (comboOperand % 8));
        instructionPointer += 2;
    }

    private void bdv(int operand) {
        long comboOperand = getComboOperand(operand);
        b = a >> comboOperand;
        instructionPointer += 2;
    }

    private void cdv(int operand) {
        long comboOperand = getComboOperand(operand);
        c = a >> comboOperand;
        instructionPointer += 2;
    }

    private long getComboOperand(int operand) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> a;
            case 5 -> b;
            case 6 -> c;
            default -> throw new IllegalArgumentException("Illegal operand: " + operand);
        };
    }
}
