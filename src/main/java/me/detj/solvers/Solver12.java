package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver12 {
    public static void main(String[] args) {
        var garden = Inputs.parseCharGrid("input_12.txt");

        long price = Utils.priceFence(garden);
        System.out.printf("Solution Part 1: %d\n", price);

        long priceWithDiscount = Utils.priceFenceWithDiscount(garden);
        System.out.printf("Solution Part 2: %d\n", priceWithDiscount);
    }
}
