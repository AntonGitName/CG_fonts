package edu.amd.spbstu.cg.splines;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Aleksandra
 * @since 14.03.2015.
 */
public class HermiteSpline {
    private static void findQ(Point q0, Point qm, List<Point> p, float[] qx, float[] qy) {
        final int m = p.size() - 1;
        final float[] ax = new float[m + 1];
        final float[] bx = new float[m + 1];
        final float[] ay = new float[m + 1];
        final float[] by = new float[m + 1];
        ax[1] = 0.f;
        bx[1] = q0.x;
        qx[m] = qm.x;

        ay[1] = 0.f;
        by[1] = q0.y;
        qy[m] = qm.y;
        for (int i = 1; i < m; ++i) {
            ax[i + 1] = -0.25f + ax[i];
            bx[i + 1] = (bx[i] - 3 * (p.get(i + 1).x - p.get(i - 1).x)) * ax[i + 1];
            ay[i + 1] = -0.25f + ay[i];
            by[i + 1] = (by[i] - 3 * (p.get(i + 1).y - p.get(i - 1).y)) * ay[i + 1];
        }
        for (int i = m - 1; i >= 0; --i) {
            qx[i] = ax[i + 1] * qx[i + 1] + bx[i + 1];
            qy[i] = ay[i + 1] * qy[i + 1] + by[i + 1];
        }
    }

    private static float hermiteVal(float p1, float p2, float q1, float q2, float t) {
        final float t2 = t * t;
        final float t3 = t2 * t;

        return (1.f - 3 * t2 + 2 * t3) * p1 + t2 * (3.f - 2 * t) * p2 + t * (1.f - 2 * t + t2) * q1 - t2 * (t - 1.f) * q2;
    }

    public static List<Point> hermite(List<Point> p, Point q0, Point qm, int div) {
        float t, dt = 1.f / div;
        final int m = p.size();
        final float[] qx = new float[m];
        final float[] qy = new float[m];
        List<Point> out = new ArrayList<>();
        findQ(q0, qm, p, qx, qy);
        for (int i = 1; i < m; ++i) {
            t = 0.f;
            for (int j = 0; j <= div; ++j) {
                Point point = new Point();
                point.x = (int) hermiteVal(p.get(i - 1).x, p.get(i).x, qx[i - 1], qx[i], t);
                point.y = (int) hermiteVal(p.get(i - 1).y, p.get(i).y, qy[i - 1], qy[i], t);
                out.add(point);
                t += dt;
            }
        }
        return out;
    }
}
