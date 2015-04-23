package edu.amd.spbstu.cg.splines;

import edu.amd.spbstu.cg.geom.PointFloat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iAnton
 * @since 21/04/15
 */
public class HermiteSpline {

    private final int n;
    private final float[] pointsX;
    private final float[] pointsY;
    private final float[] tangentsX;
    private final float[] tangentsY;

    public HermiteSpline(List<PointFloat> points, PointFloat startTangent, PointFloat endTangent) {
        n = points.size() + 1;
        pointsX = new float[n];
        pointsY = new float[n];
        tangentsX = new float[n];
        tangentsY = new float[n];
        for (int i = 0; i < n - 1; ++i) {
            pointsX[i] = points.get(i).x;
            pointsY[i] = points.get(i).y;
        }

        pointsX[n - 1] = pointsX[0];
        pointsY[n - 1] = pointsY[0];

        calcTangents(tangentsX, pointsX, startTangent.x, endTangent.x);
        calcTangents(tangentsY, pointsY, startTangent.y, endTangent.y);
    }

    private static float calcHermiteFunc(float p1, float p2, float q1, float q2, float t) {
        final float t2 = t * t;
        final float t3 = t2 * t;
        return (1.f - 3.f * t2 + 2.f * t3) * p1 + t2 * (3.f - 2.f * t) * p2 + t * (1.f - 2.f * t + t2) * q1 + t2 * (t - 1.f) * q2;
    }

    private static void addPoint(List<PointFloat> where, float x, float y) {
        where.add(new PointFloat(x, y));
    }

    private void calcTangents(float[] tangentsXY, float[] pointsXY, float startTangentXY, float endTangentXY) {
        final float[] a = new float[n];
        final float[] b = new float[n];
        a[1] = 0.0f;
        b[1] = startTangentXY;
        for (int i = 1; i < n - 1; ++i) {
            a[i + 1] = -1.0f / (4.f + a[i]);
            b[i + 1] = (b[i] - 3 * (pointsXY[i + 1] - pointsXY[i - 1])) * a[i + 1];
        }
        tangentsXY[n - 1] = endTangentXY;
        for (int i = n - 2; i >= 0; --i) {
            tangentsXY[i] = a[i + 1] * tangentsXY[i + 1] + b[i + 1];
        }
    }

    private float hermiteValX(int i, float t) {
        return calcHermiteFunc(pointsX[i], pointsX[i + 1], tangentsX[i], tangentsX[i + 1], t);
    }

    private float hermiteValY(int i, float t) {
        return calcHermiteFunc(pointsY[i], pointsY[i + 1], tangentsY[i], tangentsY[i + 1], t);
    }

    public List<PointFloat> getSpline(int div) {
        div += 1;
        final List<PointFloat> result = new ArrayList<>((n - 1) * div);
        final float dt = 1.0f / (float) div;
        for (int i = 0; i < n - 1; ++i) {
            addPoint(result, pointsX[i], pointsY[i]);
            for (int j = 1; j < div; ++j) {
                final float x = hermiteValX(i, j * dt);
                final float y = hermiteValY(i, j * dt);
                result.add(new PointFloat((int) x, (int) y));
            }
        }
        addPoint(result, pointsX[n - 1], pointsY[n - 1]);
        return result;
    }
}
