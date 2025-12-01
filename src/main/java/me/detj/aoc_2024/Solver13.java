package me.detj.aoc_2024;

import me.detj.utils.ClawMachine;
import me.detj.utils.Inputs;
import me.detj.utils.LClawMachine;
import me.detj.utils.LPoint;
import me.detj.utils.Point;

import java.util.List;

public class Solver13 {
    public static void main(String[] args) {
        var input = Inputs.parseClawMachines("2024/input_13.txt");

        long s1 = fewestTokens(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = fewestTokensBigger(input);
        System.out.printf("Solution Part 2: %d\n", s2);
    }

    private static int fewestTokens(List<ClawMachine> clawMachines) {
        int sum = 0;
        for (ClawMachine machine : clawMachines) {
            int tokens = tokensForPrize(machine);
            if (tokens != -1) {
                sum += tokens;
            }
        }
        return sum;
    }

    private static int tokensForPrize(ClawMachine machine) {
        int min = Integer.MAX_VALUE;
        for (int a = 0; a < 100; a++) {
            for (int b = 0; b < 100; b++) {
                Point position = machine.getA().times(a).plus(machine.getB().times(b));
                int tokens = a * 3 + b;

                if (position.equals(machine.getPrize())) {
                    System.out.printf("%s: %s\n", machine.getPrize(), position);
                }

                if (position.equals(machine.getPrize()) && min > tokens) {
                    min = tokens;
                }
            }
        }

        if (min == Integer.MAX_VALUE) {
            return -1;
        }
        return min;
    }


    public static long fewestTokensBigger(List<ClawMachine> clawMachines) {
        long sum = 0;
        for (ClawMachine machine : clawMachines) {
            long tokens = tokensForPrizeBigger(machine);
            if (tokens != -1) {
                sum += tokens;
            }
        }
        return sum;
    }

    public static long tokensForPrizeBigger(ClawMachine machine) {
        LClawMachine lMachine = fixRounding(machine);

        double[] prize = toVector(lMachine.getPrize());
        double[][] matrix = toMatrix(lMachine.getA(), lMachine.getB());
        double[][] inverse = getInverse(matrix);
        double[] ans = multiply(prize, inverse);

        long as = Math.round(ans[0]);
        long bs = Math.round(ans[1]);

        if (lMachine.getA().times(as).plus(lMachine.getB().times(bs)).equals(lMachine.getPrize())) {
            return as * 3 + bs;
        }

        return -1;
    }

    private static final long CORRECTION = 10000000000000L;
    //private static final long CORRECTION = 0;

    private static LClawMachine fixRounding(ClawMachine machine) {
        LPoint a = LPoint.from(machine.getA());
        LPoint b = LPoint.from(machine.getB());
        LPoint prize = new LPoint(CORRECTION + machine.getPrize().getX(), CORRECTION + machine.getPrize().getY());
        return new LClawMachine(a, b, prize);
    }

    private static double[] toVector(LPoint p) {
        return new double[]{p.getX(), p.getY()};
    }

    private static double[][] toMatrix(LPoint a, LPoint b) {
        return new double[][]{
                toVector(a),
                toVector(b),
        };
    }

    private static double[][] getInverse(double[][] matrix) {
        double determinant = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        return new double[][]{
                {(1.0 / determinant) * matrix[1][1], (1.0 / determinant) * -matrix[0][1]},
                {(1.0 / determinant) * -matrix[1][0], (1.0 / determinant) * matrix[0][0]}
        };
    }

    private static double[] multiply(double[] vector, double[][] matrix) {
        return new double[]{
                vector[0] * matrix[0][0] + vector[1] * matrix[1][0],
                vector[0] * matrix[0][1] + vector[1] * matrix[1][1]
        };
    }

}
