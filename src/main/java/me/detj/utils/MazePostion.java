package me.detj.utils;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class MazePostion {
    @NonNull Point point;
    int direction;
    int score;
    @NonNull List<MazePostion> path;
}
