package edu.amd.spbstu.cg.splines;

/**
 * @author Aleksandra
 * @since 14.03.2015.
 */
public class HermiteSpline {
    private void findQ(float q0, float qm, float[] p, int m, float[] q) {
        final float[] a = new float[m];
        final float[] b = new float[m];
        a[1] = 0.f;
        b[1] = q0;
        q[m] = qm;
        for(int i = 1; i < m; ++i){
            a[i + 1] = - 0.25f + a[i];
            b[i + 1] = (b[i] - 3 * (p[i + 1] - p[i - 1])) * a[i + 1];
        }
        for(int i = m - 1; i >= 0; ++i){
            q[i] = a[i + 1] * q[i + 1] + b[i + 1];
        }
    }

    private float hermiteVal(float p1, float p2, float q1, float q2, float t) {
        final float t2 = t * t;
        final float t3 = t2 * t;

        return (1.f - 3 * t2 + 2 * t3) * p1 + t2 * (3.f - 2 * t) * p2 + t * (1.f - 2 * t + t2) * q1 - t2 * (t - 1.f) * q2;
    }

    public void hermite(float[] p, int m, float q0, float qm, int div, float[] out) {
        float t, dt = 1.f / div;
        final float[] q = new float[m];
        int  start = 0;

        findQ(q0, qm, p, m - 1, q);
        for(int i = 1; i < m; ++i){
            t = 0.f;
            for(int j = 0; j <= div; ++j){
                out[start + j] = hermiteVal(p[i - 1], p[i], q[i - 1], q[i], t);
                t += dt;
            }
            start += div;
        }
    }
}
