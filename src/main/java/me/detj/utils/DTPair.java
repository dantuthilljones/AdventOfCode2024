package me.detj.utils;

import lombok.NonNull;
import lombok.Value;

@Value
public class DTPair<L, R> {
    @NonNull
    L left;
    @NonNull
    R right;
}