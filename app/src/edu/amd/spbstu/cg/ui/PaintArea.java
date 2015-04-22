package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.splines.HermiteSpline;
import edu.amd.spbstu.cg.splines.PointFloat;
import edu.amd.spbstu.cg.splines.UserSelectionLine;

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
    private static final int POINT_DIAMETER = 14;
    private static final int MIN_POINTS_IN_LINE = 2;
    private static final int POINTS_PER_SPLINE = 10;

    private final List<UserSelectionLine> selectionLines;
    private ActionType actionType;
    private int numPointMoved = -1;
    private int selectedLine;

    public PaintArea() {
        selectionLines = new ArrayList<>();
        actionType = ActionType.NO_ACTION;

        selectionLines.add(new UserSelectionLine());
        selectedLine = 0;

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

    public void setSelectedLine(int selectedLine) {
        this.selectedLine = selectedLine;
    }

    private void drawJPanelMousePressed(MouseEvent event) {
        PointFloat p = new PointFloat(event.getPoint());
        final UserSelectionLine line = selectionLines.get(selectedLine);
        if (!event.isMetaDown()) {
            for (int i = 0; i < line.getPoints().size(); ++i) {
                if (isInCircle(p, line.get(i), POINT_DIAMETER)) {
                    actionType = ActionType.MOVE_POINT;
                    numPointMoved = i;
                    selectedLine = selectionLines.indexOf(line);
                    return;
                }

            }

            if (isInCircle(p, line.getFakeStart(), POINT_DIAMETER)) {
                actionType = ActionType.CHANGE_VECTOR;
                numPointMoved = 0;
                selectedLine = selectionLines.indexOf(line);
                return;
            }

            if (isInCircle(p, line.getFakeEnd(), POINT_DIAMETER)) {
                actionType = ActionType.CHANGE_VECTOR;
                numPointMoved = 1;
                selectedLine = selectionLines.indexOf(line);
            }
        } else {
            for (int i = 0; i < line.getPoints().size(); ++i) {
                if (isInCircle(p, line.get(i), POINT_DIAMETER)) {
                    actionType = ActionType.DELETE_POINT;
                    numPointMoved = i;
                    selectedLine = selectionLines.indexOf(line);
                    return;
                }
            }
        }
    }

    private void drawJPanelMouseReleased(MouseEvent event) {
        final PointFloat p = new PointFloat(event.getPoint());
        if (selectedLine == -1) {
            return;
        }
        final UserSelectionLine line = selectionLines.get(selectedLine);
        if (!event.isMetaDown()) {
            switch (actionType) {
                case MOVE_POINT:
                    line.set(numPointMoved, p);
                    actionType = ActionType.NO_ACTION;
                    break;
                case CHANGE_VECTOR:
                    if (numPointMoved == 0) {
                        line.setStartTangent(p.sub(line.getFirstPoint()));
                    }
                    if (numPointMoved == 1) {
                        line.setEndTangent(p.sub(line.getLastPoint()));
                    }
                    actionType = ActionType.NO_ACTION;
                    break;
                default:
                    line.add(p);
                    break;
            }

        } else {
            switch (actionType) {
                case DELETE_POINT:
                    if (line.getPoints().size() <= MIN_POINTS_IN_LINE) {
                        break;
                    }
                    line.getPoints().remove(numPointMoved);
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
        g.setColor(Color.RED);

        for (UserSelectionLine line : selectionLines) {
            for (PointFloat p : line.getPoints()) {
                g.drawOval((int) (p.x - POINT_DIAMETER / 2), (int) (p.y - POINT_DIAMETER / 2), POINT_DIAMETER, POINT_DIAMETER);
            }

            if (line.size() >= MIN_POINTS_IN_LINE) {
                final List<PointFloat> spline = new HermiteSpline(line.getPoints(), line.getStartTangent().neg(), line.getEndTangent()).getSpline(POINTS_PER_SPLINE);
                for (int i = 0; i < spline.size() - 1; ++i) {
                    g.drawLine((int) spline.get(i).x, (int) spline.get(i).y, (int) spline.get(i + 1).x, (int) spline.get(i + 1).y);
                }

            }
            g.setColor(Color.green);
            g.drawOval((int) line.getFakeStart().x - POINT_DIAMETER / 2, (int) line.getFakeStart().y - POINT_DIAMETER / 2, POINT_DIAMETER, POINT_DIAMETER);
            g.drawLine((int) line.getFirstPoint().x, (int) line.getFirstPoint().y, (int) line.getFakeStart().x, (int) line.getFakeStart().y);
            g.drawOval((int) line.getFakeEnd().x - POINT_DIAMETER / 2, (int) line.getFakeEnd().y - POINT_DIAMETER / 2, POINT_DIAMETER, POINT_DIAMETER);
            g.drawLine((int) line.getLastPoint().x, (int) line.getLastPoint().y, (int) line.getFakeEnd().x, (int) line.getFakeEnd().y);
        }

    } // end method paintComponent

    private enum ActionType {
        MOVE_POINT, CHANGE_VECTOR, NO_ACTION, DELETE_POINT
    }

}
