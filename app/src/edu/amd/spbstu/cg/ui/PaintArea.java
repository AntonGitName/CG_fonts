package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.splines.HermiteSpline;
import edu.amd.spbstu.cg.splines.PointFloat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author iAnton
 * @since 15/03/15
 */
public class PaintArea extends JPanel {
    private static final Color DRAW_COLOR = Color.RED;
    private static final int DRAW_DIAMETER = 14;
    private final List<PointFloat> selectedPoints = new ArrayList<>();
    private final Color drawColor;
    private final int drawDiameter;
    private PointFloat fakeStart = new PointFloat();
    private PointFloat fakeEnd = new PointFloat();
    private PointFloat startTangent = new PointFloat(-100, 0);
    private PointFloat endTangent = new PointFloat(100, 0);
    private ActionType actionType;
    private int numPointMoved = -1;

    public PaintArea() {
        drawColor = DRAW_COLOR;
        drawDiameter = DRAW_DIAMETER;
        final PointFloat p1 = new PointFloat(250.f, 250.f);
        final PointFloat p2 = new PointFloat(450.f, 250.f);
        selectedPoints.add(p1);
        selectedPoints.add(p2);
        fakeStart.x = (int) (p1.x + startTangent.x);
        fakeStart.y = (int) (p1.y + startTangent.y);
        fakeEnd.x = (int) (p2.x + endTangent.x);
        fakeEnd.y = (int) (p2.y + endTangent.y);
        actionType = ActionType.NO_ACTION;

        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        drawJPanelMouseReleased(e);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        drawJPanelMousePressed(e);
                    }
                }
        );
    }

    private static boolean isInCircle(PointFloat p1, PointFloat p2, float d) {
        return isInCircle(p1.x, p1.y, p2.x, p2.y, d / 2);
    }

    private static boolean isInCircle(float x, float y, float x0, float y0, float r) {
        return (x - x0) * (x - x0) + (y - y0) * (y - y0) < r * r;
    }

    private void drawJPanelMousePressed(MouseEvent event) {
        PointFloat p = new PointFloat(event.getPoint());

        if (!event.isMetaDown()) {
            for (int i = 0; i < selectedPoints.size(); ++i) {
                if (isInCircle(p, selectedPoints.get(i), DRAW_DIAMETER)) {
                    actionType = ActionType.MOVE_POINT;
                    numPointMoved = i;
                    return;
                }

            }

            if (isInCircle(p, fakeStart, DRAW_DIAMETER)) {
                actionType = ActionType.CHANGE_VECTOR;
                numPointMoved = 0;
                return;
            }

            if (isInCircle(p, fakeEnd, DRAW_DIAMETER)) {
                actionType = ActionType.CHANGE_VECTOR;
                numPointMoved = 1;
            }
        } else {
            for (int i = 0; i < selectedPoints.size(); ++i) {
                if (isInCircle(p, selectedPoints.get(i), DRAW_DIAMETER)) {
                    actionType = ActionType.DELETE_POINT;
                    numPointMoved = i;
                    return;
                }

            }
        }
    }

    private void drawJPanelMouseReleased(MouseEvent event) {
        final PointFloat p = new PointFloat(event.getPoint());
        if (!event.isMetaDown()) {
            switch (actionType) {
                case MOVE_POINT:
                    if (numPointMoved == 0) {
                        fakeStart = p.add(startTangent);
                    }
                    if (numPointMoved == selectedPoints.size() - 1) {
                        fakeStart = p.add(endTangent);
                    }
                    selectedPoints.set(numPointMoved, p);
                    actionType = ActionType.NO_ACTION;
                    break;
                case CHANGE_VECTOR:
                    if (numPointMoved == 0) {
                        fakeStart = p;
                        startTangent = fakeStart.sub(selectedPoints.get(0));
                    }
                    if (numPointMoved == 1) {
                        fakeEnd = p;
                        endTangent = fakeEnd.sub(selectedPoints.get(selectedPoints.size() - 1));
                    }
                    actionType = ActionType.NO_ACTION;
                    break;
                default:
                    selectedPoints.add(p);
                    fakeEnd = p.add(endTangent);
                    break;
            }

        } else {
            switch (actionType) {
                case DELETE_POINT:
                    if (selectedPoints.size() == 2) {
                        break;
                    }
                    if (numPointMoved == 0) {
                        fakeStart = selectedPoints.get(1).add(startTangent);
                    }
                    if (numPointMoved == selectedPoints.size() - 1) {
                        fakeEnd = selectedPoints.get(selectedPoints.size() - 2).add(endTangent);
                    }
                    selectedPoints.remove(numPointMoved);
                    actionType = ActionType.NO_ACTION;
                    break;
                default:
                    break;
            }
        }
        repaint();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(drawColor);

        for (PointFloat p : selectedPoints) {
            g.drawOval((int) (p.x - drawDiameter / 2), (int) (p.y - drawDiameter / 2), drawDiameter, drawDiameter);
        }

        if (selectedPoints.size() > 1) {
            List<PointFloat> line = new HermiteSpline(selectedPoints, startTangent.neg(), endTangent).getSpline(10);
            for (int i = 0; i < line.size() - 1; ++i) {
                g.drawLine((int) line.get(i).x, (int) line.get(i).y, (int) line.get(i + 1).x, (int) line.get(i + 1).y);
            }

        }
        g.setColor(Color.green);
        g.drawOval((int) fakeStart.x - drawDiameter / 2, (int) fakeStart.y - drawDiameter / 2, drawDiameter, drawDiameter);
        g.drawLine((int) selectedPoints.get(0).x, (int) selectedPoints.get(0).y, (int) fakeStart.x, (int) fakeStart.y);
        g.drawOval((int) fakeEnd.x - drawDiameter / 2, (int) fakeEnd.y - drawDiameter / 2, drawDiameter, drawDiameter);
        g.drawLine((int) selectedPoints.get(selectedPoints.size() - 1).x, (int) selectedPoints.get(selectedPoints.size() - 1).y, (int) fakeEnd.x, (int) fakeEnd.y);

    } // end method paintComponent

    private enum ActionType {
        MOVE_POINT, CHANGE_VECTOR, NO_ACTION, DELETE_POINT
    }

}
