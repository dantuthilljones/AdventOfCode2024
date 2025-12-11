package me.detj.utils;

import lombok.Value;

@Value
public class Point3D {
    int x;
    int y;
    int z;

    public double distanceTo(Point3D p2) {
        return Math.sqrt(Math.pow(this.x - p2.x, 2) + Math.pow(this.y - p2.y, 2) + Math.pow(this.z - p2.z, 2));
    }
}
