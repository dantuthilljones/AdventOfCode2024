package me.detj.utils;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class TowelProblem {
    @NonNull
    List<String> towels;
    @NonNull
    List<String> patterns;
}
