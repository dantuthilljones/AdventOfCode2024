package me.detj.utils;

import lombok.NonNull;
import lombok.Value;

@Value
public class Pair<T> {
    @NonNull
    T left;
    @NonNull
    T right;
}
