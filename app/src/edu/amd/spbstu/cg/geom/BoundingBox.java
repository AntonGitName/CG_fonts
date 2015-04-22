package edu.amd.spbstu.cg.geom;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author iAnton
 * @since 23/04/15
 */
public final class BoundingBox {
    private PointFloat leftTopPoint;
    private PointFloat rightBottomPoint;

    public BoundingBox(float x1, float y1, float x2, float y2) {
        leftTopPoint = new PointFloat(x1, y1);
        rightBottomPoint = new PointFloat(x2, y2);
    }

    public List<PointFloat> getPoints() {
        final List<PointFloat> result = new ArrayList<>(4);
        result.add(leftTopPoint);
        result.add(new PointFloat(rightBottomPoint.x, leftTopPoint.y));
        result.add(rightBottomPoint);
        result.add(new PointFloat(leftTopPoint.x, rightBottomPoint.y));
        return result;
    }

    public void setPoint(PointFloat point, int x) {
        if (x == 0) {
            leftTopPoint = point;
        } else if (x == 1) {
            rightBottomPoint.x = point.x;
            leftTopPoint.y = point.y;
        } else if (x == 2) {
            rightBottomPoint = point;
        } else {
            leftTopPoint.x = point.x;
            rightBottomPoint.y = point.y;
        }
    }

    public Shape getShape() {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(leftTopPoint.x, leftTopPoint.y);
        path.lineTo(leftTopPoint.x, rightBottomPoint.y);
        path.lineTo(rightBottomPoint.x, rightBottomPoint.y);
        path.lineTo(rightBottomPoint.x, leftTopPoint.y);
        path.closePath();
        return path;
    }
}
