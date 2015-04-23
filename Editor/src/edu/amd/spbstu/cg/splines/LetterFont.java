package edu.amd.spbstu.cg.splines;

import edu.amd.spbstu.cg.geom.PointFloat;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksandra
 * @since 23.04.2015.
 */
public class LetterFont {
    private final List<HermiteSpline> splines;

    public LetterFont(List<HermiteSpline> hermiteSplines) {
        splines = hermiteSplines;
    }

    private static Area getResultingArea(List<Shape> shapes) {
        final Area result = new Area();
        for (Shape shape : shapes) {
            result.exclusiveOr(new Area(shape));
        }
        return result;
    }

    public Area getArea(float x, float y, float width, float height, int amountLinePoints) {
        List<List<PointFloat>> scaledFont = new ArrayList<>();
        for (HermiteSpline hermiteSpline : splines) {
            List<PointFloat> scaledPoints = hermiteSpline.getSpline(amountLinePoints);
            for (PointFloat scaledPoint : scaledPoints) {
                scaledPoint.x = scaledPoint.x * width + x;
                scaledPoint.y = scaledPoint.y * height + y;
            }
            scaledFont.add(scaledPoints);
        }
        final List<Shape> shapes = new ArrayList<>(splines.size());
        for (List<PointFloat> line : scaledFont) {
            final Path2D.Float path = new Path2D.Float();
            path.moveTo(line.get(0).x, line.get(0).y);
            for (int i = 1; i < line.size(); ++i) {
                path.lineTo(line.get(i).x, line.get(i).y);
            }
            path.closePath();
            shapes.add(path);
        }
        return getResultingArea(shapes);
    }
}
