package me.detj.aoc_2025;

import me.detj.utils.Inputs;
import me.detj.utils.Schematic;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solver10 {

    static void main() throws IOException {
        String file = "2025/input_10_example.txt";
        var input = Inputs.parseSchematics(file);
        System.out.printf("Solution 1: %d\n", fewestPresses(input));
        System.out.printf("Solution 2: %d\n", fewestPresses2(input));
    }

    private static long fewestPresses(List<Schematic> input) {
        long presses = 0;
        for (Schematic schematic : input) {
            presses += calcFewestPresses(schematic);
        }
        return presses;
    }

    private static long calcFewestPresses(Schematic schematic) {
        for (int presses = 1; presses <= schematic.getButtons().size(); presses++) {
            if (checkPresses(schematic, presses)) {
                return presses;
            }
        }
        throw new RuntimeException("No fewest presses found");
    }

    private static boolean checkPresses(Schematic schematic, int presses) {
        List<List<Boolean>> pressesToTry = generatePresses(presses, schematic.getButtons().size());
        for (List<Boolean> pressCombination : pressesToTry) {
            if (checkPress(schematic, pressCombination)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkPress(Schematic schematic, List<Boolean> presses) {
        List<Boolean> lights = makeAllFalseList(schematic.getIndicatorLights().size());

        for (int i = 0; i < presses.size(); i++) {
            boolean isPressed = presses.get(i);
            if (isPressed) {
                List<Integer> lightsToToggle = schematic.getButtons().get(i);
                for (Integer toggleIndex : lightsToToggle) {
                    lights.set(toggleIndex, !lights.get(toggleIndex));
                }
            }
        }

        return lightsMatch(lights, schematic.getIndicatorLights());
    }

    private static boolean lightsMatch(List<Boolean> lights1, List<Boolean> lights2) {
        if (lights1.size() != lights2.size()) {
            throw new RuntimeException("Number of lights do not match");
        }

        for (int i = 0; i < lights1.size(); i++) {
            if (!lights1.get(i).equals(lights2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<List<Boolean>> generatePresses(int presses, int buttons) {
        if (presses == 1) {
            List<Boolean> noPresses = makeAllFalseList(buttons);
            List<List<Boolean>> singlePresses = new ArrayList<>();
            for (int i = 0; i < buttons; i++) {
                List<Boolean> press = new ArrayList<>(noPresses);
                press.set(i, true);
                singlePresses.add(press);
            }
            return singlePresses;
        } else {
            List<List<Boolean>> previousPresses = generatePresses(presses - 1, buttons);
            List<List<Boolean>> newPresses = new ArrayList<>();
            for (List<Boolean> previous : previousPresses) {
                for (int i = 0; i < buttons; i++) {
                    List<Boolean> newPress = new ArrayList<>(previous);

                    // Don't need to press a button more than once ever
                    Boolean pressed = newPress.get(i);
                    if (pressed) {
                        continue;
                    }

                    newPress.set(i, true);
                    newPresses.add(newPress);
                }
            }
            return newPresses;
        }
    }

    private static List<Boolean> makeAllFalseList(int buttons) {
        List<Boolean> allFalse = new ArrayList<>(buttons);
        for (int i = 0; i < buttons; i++) {
            allFalse.add(false);
        }
        return allFalse;
    }

    private static long fewestPresses2(List<Schematic> input) {
        long presses = 0;
        for (Schematic schematic : input) {
            presses += calcFewestPresses2(schematic);
        }
        return presses;
    }

    private static long calcFewestPresses2(Schematic schematic) {
        SimpleMatrix target = new SimpleMatrix(schematic.getJoltages().size(), 1);
        for(int i = 0; i < schematic.getJoltages().size(); i++) {
            target.set(schematic.getJoltages().size() - i -1, 0, schematic.getJoltages().get(i));
        }

//        System.out.println("Target matrix:");
//        target.print();

        SimpleMatrix buttons = new SimpleMatrix(schematic.getButtons().size(), schematic.getJoltages().size());
        for (int i = 0; i < schematic.getButtons().size(); i++) {
            List<Integer> button = schematic.getButtons().get(i);
            for (Integer j : button) {
                buttons.set(i, j, 1);
            }
        }
        buttons = buttons.transpose();

//        System.out.println("Button matrix:");
//        buttons.print();

        SimpleMatrix solution = solve(buttons, target);

        check(schematic);
        return (long) solution.elementSum();
    }

    private static void check(Schematic schematic) {
        SimpleMatrix target = new SimpleMatrix(4, 1);
        target.set(0, 0, 3);
        target.set(1, 0, 5);
        target.set(2, 0, 4);
        target.set(3, 0, 7);

        System.out.println("Target matrix:");
        target.print();

        SimpleMatrix targetParsed = new SimpleMatrix(schematic.getJoltages().size(), 1);
        for(int i = 0; i < schematic.getJoltages().size(); i++) {
            targetParsed.set(schematic.getJoltages().size() - i -1, 0, schematic.getJoltages().get(i));
        }

        System.out.println("Target Parsed matrix:");
        target.print();


        SimpleMatrix buttons = new SimpleMatrix(schematic.getButtons().size(), schematic.getJoltages().size());
        for (int i = 0; i < schematic.getButtons().size(); i++) {
            List<Integer> button = schematic.getButtons().get(i);
            for (Integer j : button) {
                buttons.set(i, j, 1);
            }
        }
        buttons = buttons.transpose();

        System.out.println("Buttons matrix:");
        buttons.print();

        SimpleMatrix pressesMatrix = new SimpleMatrix(6, 1);
        pressesMatrix.set(0, 0, 1);
        pressesMatrix.set(1, 0, 3);
        pressesMatrix.set(2, 0, 0);
        pressesMatrix.set(3, 0, 3);
        pressesMatrix.set(4, 0, 1);
        pressesMatrix.set(5, 0, 2);

        System.out.println("Presses matrix:");
        pressesMatrix.print();

        SimpleMatrix buttonsMultPresses = buttons.mult(pressesMatrix);
        System.out.println("buttonsMultPresses matrix:");
        buttonsMultPresses.print();


        SimpleMatrix solution = solve(buttons, target);
        System.out.println("Solution matrix:");
        solution.print();

        SimpleMatrix check = buttons.mult(solution);
        System.out.println("Check matrix:");
        check.print();

        int i = 0;
    }

    private static SimpleMatrix solve(SimpleMatrix A, SimpleMatrix b) {
        // Solve: minimize 1^T x subject to A x = b, x >= 0.
        // A is totally unimodular, so LP relaxation yields integral solution.
        // Two-phase simplex (tableau-based).
        int m = A.numRows();
        int n = A.numCols();

        // Normalize rows so RHS is non-negative (standard form for simplex ratios)
        double[][] Ad = new double[m][n];
        double[] bd = new double[m];
        for (int i = 0; i < m; i++) {
            double bi = b.get(i, 0);
            double sign = bi >= 0 ? 1.0 : -1.0;
            bd[i] = bi * sign;
            for (int j = 0; j < n; j++) {
                Ad[i][j] = A.get(i, j) * sign;
            }
        }

        // Phase I: Add artificial variables a_i to each equality to find a feasible basis.
        int totalCols = n + m + 1; // x vars + artificial vars + RHS
        int totalRows = m + 1;     // m constraints + 1 objective
        double[][] T = new double[totalRows][totalCols];

        // Fill constraints: [Ad | I | bd]
        for (int i = 0; i < m; i++) {
            // x-part
            System.arraycopy(Ad[i], 0, T[i], 0, n);
            // artificial identity
            for (int a = 0; a < m; a++) {
                T[i][n + a] = (i == a) ? 1.0 : 0.0;
            }
            // RHS
            T[i][n + m] = bd[i];
        }

        // Phase I objective: minimize sum(a) -> maximize negative of sum(a)
        // Initialize objective row c = [0...0 | -1...-1 | 0]
        for (int j = 0; j < n; j++) T[m][j] = 0.0;
        for (int a = 0; a < m; a++) T[m][n + a] = -1.0;
        T[m][n + m] = 0.0;
        // Make current basis (artificial vars) consistent by eliminating their coefficients
        // For each artificial var a_i basic in row i, add the constraint row i to the objective
        for (int i = 0; i < m; i++) {
            // Objective has -1 for a_i; add row i to make it zero
            double coeff = T[m][n + i];
            if (Math.abs(coeff) > 1e-12) {
                for (int j = 0; j < totalCols; j++) {
                    T[m][j] += T[i][j];
                }
            }
        }

        // Simplex helpers
        java.util.function.IntSupplier chooseEntering = () -> {
            int enter = -1;
            double best = 1e-12; // need negative to improve (maximize)
            for (int j = 0; j < n + m; j++) { // exclude RHS
                double cj = T[m][j];
                if (cj < best - 1e-12) {
                    best = cj;
                    enter = j;
                }
            }
            return enter;
        };
        java.util.function.IntUnaryOperator chooseLeaving = (enter) -> {
            int leave = -1;
            double bestRatio = Double.POSITIVE_INFINITY;
            for (int i = 0; i < m; i++) {
                double aie = T[i][enter];
                if (aie > 1e-12) {
                    double rhs = T[i][n + m];
                    double ratio = rhs / aie;
                    if (ratio >= 0 && ratio < bestRatio - 1e-12) {
                        bestRatio = ratio;
                        leave = i;
                    }
                }
            }
            return leave;
        };
        java.util.function.BiConsumer<Integer, Integer> pivot = (row, col) -> {
            double piv = T[row][col];
            for (int j = 0; j < totalCols; j++) T[row][j] /= piv;
            for (int i = 0; i < totalRows; i++) {
                if (i == row) continue;
                double factor = T[i][col];
                if (Math.abs(factor) > 1e-12) {
                    for (int j = 0; j < totalCols; j++) {
                        T[i][j] -= factor * T[row][j];
                    }
                }
            }
        };

        // Run Phase I simplex
        int iter = 0, maxIter = 10000;
        while (true) {
            int enter = chooseEntering.getAsInt();
            if (enter == -1) break; // optimal Phase I
            int leave = chooseLeaving.applyAsInt(enter);
            if (leave == -1) throw new RuntimeException("LP infeasible in Phase I: no leaving row");
            pivot.accept(leave, enter);
            if (++iter > maxIter) throw new RuntimeException("Simplex exceeded max iterations in Phase I");
        }

        // Feasibility check: objective RHS should be 0 (sum of artificials eliminated)
        double phase1Obj = T[m][n + m];
        if (Math.abs(phase1Obj) > 1e-9) {
            throw new RuntimeException("No feasible solution: artificial variables remain");
        }

        // Build Phase II tableau using only x variables and RHS
        int cols2 = n + 1;
        double[][] TP2 = new double[m + 1][cols2];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) TP2[i][j] = T[i][j];
            TP2[i][n] = T[i][n + m];
        }
        // Objective: minimize sum(x) -> maximize -sum(x)
        for (int j = 0; j < n; j++) TP2[m][j] = -1.0;
        TP2[m][n] = 0.0;

        // Make objective consistent with current basis (eliminate coefficients for basic x columns)
        boolean[] isBasic = new boolean[n];
        int[] basicRowForCol = new int[n];
        for (int j = 0; j < n; j++) {
            int oneRow = -1;
            boolean ok = true;
            for (int i = 0; i < m; i++) {
                double v = TP2[i][j];
                if (Math.abs(v - 1.0) < 1e-9) {
                    if (oneRow == -1) oneRow = i; else { ok = false; break; }
                } else if (Math.abs(v) > 1e-9) {
                    ok = false; break;
                }
            }
            isBasic[j] = ok && oneRow != -1;
            basicRowForCol[j] = oneRow;
        }
        for (int j = 0; j < n; j++) {
            if (isBasic[j]) {
                double coeff = TP2[m][j];
                if (Math.abs(coeff) > 1e-12) {
                    int br = basicRowForCol[j];
                    for (int k = 0; k < cols2; k++) TP2[m][k] -= coeff * TP2[br][k];
                }
            }
        }

        // Phase II simplex
        java.util.function.IntSupplier chooseEntering2 = () -> {
            int enterCol = -1;
            double best = 1e-12; // need negative to improve (maximize)
            for (int j = 0; j < n; j++) {
                double cj = TP2[m][j];
                if (cj < best - 1e-12) {
                    best = cj;
                    enterCol = j;
                }
            }
            return enterCol;
        };
        java.util.function.IntUnaryOperator chooseLeaving2 = (enter) -> {
            int leaveRow = -1;
            double bestRatio2 = Double.POSITIVE_INFINITY;
            for (int i = 0; i < m; i++) {
                double aie = TP2[i][enter];
                if (aie > 1e-12) {
                    double rhs = TP2[i][n];
                    double ratio = rhs / aie;
                    if (ratio >= 0 && ratio < bestRatio2 - 1e-12) {
                        bestRatio2 = ratio;
                        leaveRow = i;
                    }
                }
            }
            return leaveRow;
        };
        java.util.function.BiConsumer<Integer, Integer> pivot2 = (row, col) -> {
            double piv = TP2[row][col];
            for (int j = 0; j < cols2; j++) TP2[row][j] /= piv;
            for (int i = 0; i < m + 1; i++) {
                if (i == row) continue;
                double factor = TP2[i][col];
                if (Math.abs(factor) > 1e-12) {
                    for (int j = 0; j < cols2; j++) {
                        TP2[i][j] -= factor * TP2[row][j];
                    }
                }
            }
        };

        iter = 0;
        while (true) {
            int enter = chooseEntering2.getAsInt();
            if (enter == -1) break; // optimal Phase II
            int leave = chooseLeaving2.applyAsInt(enter);
            if (leave == -1) throw new RuntimeException("Unbounded LP in Phase II");
            pivot2.accept(leave, enter);
            if (++iter > maxIter) throw new RuntimeException("Simplex exceeded max iterations in Phase II");
        }

        // Extract solution vector x from basic columns
        double[] x = new double[n];
        for (int j = 0; j < n; j++) {
            int oneRow = -1;
            boolean ok = true;
            for (int i = 0; i < m; i++) {
                double v = TP2[i][j];
                if (Math.abs(v - 1.0) < 1e-9) {
                    if (oneRow == -1) oneRow = i; else { ok = false; break; }
                } else if (Math.abs(v) > 1e-9) {
                    ok = false; break;
                }
            }
            x[j] = (ok && oneRow != -1) ? TP2[oneRow][n] : 0.0;
        }

        // Clamp tiny negatives, round to integers, enforce non-negativity
        SimpleMatrix sol = new SimpleMatrix(n, 1);
        for (int j = 0; j < n; j++) {
            double val = x[j];
            if (val < 0 && Math.abs(val) < 1e-9) val = 0.0;
            long iv = Math.round(val);
            if (iv < 0) iv = 0;
            sol.set(j, 0, iv);
        }

        return sol;
    }
}
