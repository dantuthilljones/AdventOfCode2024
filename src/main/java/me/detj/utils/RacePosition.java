package me.detj.utils;

import lombok.NonNull;
import lombok.Value;

@Value
public class RacePosition {
    @NonNull
    Point point;
    int score;
}
