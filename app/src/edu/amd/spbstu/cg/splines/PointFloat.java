package edu.amd.spbstu.cg.splines;

import java.awt.*;

/**
 * @author iAnton
 * @since 22/04/15
 */
public class PointFloat {
    public float x;
    public float y;

    public PointFloat(Point p) {
        this(p.x, p.y);
    }

    public PointFloat() {
        this(0.0f, 0.0f);
    }

    public PointFloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointFloat add(PointFloat other) {
        return new PointFloat(x + other.x, y + other.y);
    }

    public PointFloat sub(PointFloat other) {
        return new PointFloat(x - other.x, y - other.y);
    }

    public PointFloat neg() {
        return new PointFloat(-x, -y);
    }
}
