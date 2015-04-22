package edu.amd.spbstu.cg.splines;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iAnton
 * @since 22/04/15
 */
public class UserSelectionLine {

    private static final int START_POINT_COUNT = 4;

    private final List<PointFloat> points;
    private PointFloat startTangent;
    private PointFloat endTangent;

    public UserSelectionLine() {
        startTangent = new PointFloat(-50, 50);
        endTangent = new PointFloat(50, 50);
        points = new ArrayList<>(START_POINT_COUNT);
        points.add(new PointFloat(100, 300));
        points.add(new PointFloat(300, 300));
        points.add(new PointFloat(300, 100));
        points.add(new PointFloat(100, 100));
    }

    private static float dist(PointFloat p1, PointFloat p2) {
        return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
    }

    private float dist(int i, PointFloat point) {
        final int n = size();
        return dist(points.get(i % n), point) + dist(points.get((i + 1) % n), point);
    }

    public void addBestFit(PointFloat point) {
        final int n = size();
        int bestIndex = 0;
        for (int i = 0; i < n; ++i) {
            if (dist(i, point) < dist(bestIndex, point)) {
                bestIndex = i;
            }
        }
        points.add(bestIndex + 1, point);
    }

    public int size() {
        return points.size();
    }

    public boolean add(PointFloat pointFloat) {
        return points.add(pointFloat);
    }

    public PointFloat get(int index) {
        return points.get(index);
    }

    public PointFloat remove(int index) {
        return points.remove(index);
    }

    public PointFloat set(int index, PointFloat element) {
        return points.set(index, element);
    }

    public PointFloat getFakeStart() {
        return getFirstPoint().add(startTangent);
    }

    public PointFloat getFakeEnd() {
        return getFirstPoint().add(endTangent);
    }

    public List<PointFloat> getPoints() {
        return points;
    }

    public PointFloat getStartTangent() {
        return startTangent;
    }

    public void setStartTangent(PointFloat startTangent) {
        this.startTangent = startTangent;
    }

    public PointFloat getFirstPoint() {
        return points.get(0);
    }

    public PointFloat getLastPoint() {
        return points.get(points.size() - 1);
    }

    public PointFloat getEndTangent() {
        return endTangent;
    }

    public void setEndTangent(PointFloat endTangent) {
        this.endTangent = endTangent;
    }
}
