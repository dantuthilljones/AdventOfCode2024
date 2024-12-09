package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver02 {

    public static void main(String[] args) {
        var reports = Inputs.parseListOfList("input_02.txt");
        int safeReports = Utils.countSafeReports(reports, false);
        System.out.printf("Safe Reports: %d\n", safeReports);


        int safeReportsWithDampener = Utils.countSafeReports(reports, true);
        System.out.printf("Safe Reports With Dampener: %d\n", safeReportsWithDampener);
    }
}
