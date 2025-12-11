package me.detj.utils;

import lombok.Value;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

@Value
public class BinaryGrid {

    long width;
    long height;
    MemorySegment seg;

    private BinaryGrid(MemorySegment seg, long width, long height) {
        this.seg = seg;
        this.width = width;
        this.height = height;
    }

    public static BinaryGrid create(long width, long height) {
        Arena arena = Arena.ofShared();

        long bytes = width * height / 8;
        if ((width * height) % 8 != 0) {
            bytes += 1;
        }
        MemorySegment seg = arena.allocate(bytes);
        return new BinaryGrid(seg, width, height);
    }

    private void setBoolean(long index, boolean value) {
        long byteIndex = index >>> 3;
        byte b = seg.get(ValueLayout.JAVA_BYTE, byteIndex);
        byte mask = (byte) (1 << (index & 7));

        if (value) {
            b |= mask;  // set bit
        } else {
            b &= ~mask; // clear bit
        }

        seg.set(ValueLayout.JAVA_BYTE, byteIndex, b);
    }

    private boolean getBoolean(long index) {
        long byteIndex = index >>> 3;
        byte b = seg.get(ValueLayout.JAVA_BYTE, byteIndex);
        byte mask = (byte) (1 << (index & 7));
        return (b & mask) != 0;
    }

    private long getIndex(int x, int y) {
        return y * width + x;
    }

    public boolean get(Point p) {
        return get(p.getX(), p.getY());
    }

    public boolean get(int x, int y) {
        return getBoolean(getIndex(x, y));
    }

    public void set(Point p, boolean value) {
        set(p.getX(), p.getY(), value);
    }

    public void set(int x, int y, boolean value) {
        setBoolean(getIndex(x, y), value);
    }


    public boolean inBounds(Point p) {
        if (p.getX() < 0 || p.getX() >= width) {
            return false;
        } else if (p.getY() < 0 || p.getY() >= height) {
            return false;
        }
        return true;
    }

    public void print() {
        System.out.println();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(get(x, y) ? '#' : '.');
            }
            System.out.println();
        }
    }

    public long countTrue() {
        long size = seg.byteSize();
        long longs = size >>> 3; // number of full 8-byte chunks
        long remainder = size & 7; // leftover bytes (0–7)

        long count = 0;

        // --- process 8 bytes at a time ---
        for (long i = 0; i < longs; i++) {
            long value = seg.get(ValueLayout.JAVA_LONG, i * 8);
            count += Long.bitCount(value);
        }

        // --- process leftover bytes ---
        long offset = longs * 8;
        for (long j = 0; j < remainder; j++) {
            byte b = seg.get(ValueLayout.JAVA_BYTE, offset + j);
            count += Integer.bitCount(b & 0xFF);
        }

        return count;
    }
}
