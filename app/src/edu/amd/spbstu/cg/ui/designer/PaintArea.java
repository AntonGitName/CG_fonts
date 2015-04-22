package edu.amd.spbstu.cg.ui.designer;

import edu.amd.spbstu.cg.splines.HermiteSpline;
import edu.amd.spbstu.cg.splines.PointFloat;
import edu.amd.spbstu.cg.splines.UserSelectionLine;

import javax.imageio.ImageIO;
import javax.sound.sampled.ReverbType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private static final String PATTERN_IMAGE_FILENAME = "res/patternImage.jpg";

    private final List<UserSelectionLine> selectionLines;
    private final MouseListener mouseListener = new MouseListener();
    private final Paint texturePaint;
    private UserSelectionLine activeLine;
    private ActionType actionType;
    private int numPointMoved = -1;
    private int selectedLine;

    public PaintArea() {
        selectionLines = new ArrayList<>();
        actionType = ActionType.NO_ACTION;

        selectionLines.add(activeLine = new UserSelectionLine());

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        BufferedImage imagePattern = null;
        try {
            imagePattern = ImageIO.read(new File(PATTERN_IMAGE_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        texturePaint = new TexturePaint(imagePattern, new Rectangle(200, 200));
    }

    private static boolean isInCircle(PointFloat p1, PointFloat p2, float d) {
        return isInCircle(p1.x, p1.y, p2.x, p2.y, d / 2);
    }

    private static boolean isInCircle(float x, float y, float x0, float y0, float r) {
        return (x - x0) * (x - x0) + (y - y0) * (y - y0) < r * r;
    }

    public void setActiveLine(int activeLine) {
        this.activeLine = selectionLines.get(activeLine);
    }


    private void RebuildLine(PointFloat p, UserSelectionLine line) {

        switch (actionType) {
            case MOVE_POINT:
                line.set(numPointMoved, p);
                break;
            case CHANGE_VECTOR:
                if (numPointMoved == 0) {
                    line.setStartTangent(p.sub(line.getFirstPoint()));
                }
                if (numPointMoved == 1) {
                    line.setEndTangent(p.sub(line.getFirstPoint()));
                }
                break;
            default:
                break;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//        g2.setColor(Color.RED);
        g2.setPaint(texturePaint);

        final Path2D.Float path = new Path2D.Float();

        for (UserSelectionLine line : selectionLines) {
            for (PointFloat p : line.getPoints()) {
                g2.drawOval((int) (p.x - POINT_DIAMETER / 2), (int) (p.y - POINT_DIAMETER / 2), POINT_DIAMETER, POINT_DIAMETER);
            }

            path.moveTo(line.getFirstPoint().x, line.getFirstPoint().y);

            if (line.size() >= MIN_POINTS_IN_LINE) {
                final List<PointFloat> spline = new HermiteSpline(line.getPoints(), line.getStartTangent().neg(), line.getEndTangent()).getSpline(POINTS_PER_SPLINE);
                for (int i = 0; i < spline.size() - 1; ++i) {
//                    g2.drawLine((int) spline.get(i).x, (int) spline.get(i).y, (int) spline.get(i + 1).x, (int) spline.get(i + 1).y);
                    path.lineTo(spline.get(i).x, spline.get(i).y);
                }
            }

            path.closePath();
            g2.fill(path);

            g2.setColor(Color.green);
            g2.drawOval((int) line.getFakeStart().x - POINT_DIAMETER / 2, (int) line.getFakeStart().y - POINT_DIAMETER / 2, POINT_DIAMETER, POINT_DIAMETER);
            g2.drawLine((int) line.getFirstPoint().x, (int) line.getFirstPoint().y, (int) line.getFakeStart().x, (int) line.getFakeStart().y);
            g2.setColor(Color.blue);
            g2.drawOval((int) line.getFakeEnd().x - POINT_DIAMETER / 2, (int) line.getFakeEnd().y - POINT_DIAMETER / 2, POINT_DIAMETER, POINT_DIAMETER);
            g2.drawLine((int) line.getFirstPoint().x, (int) line.getFirstPoint().y, (int) line.getFakeEnd().x, (int) line.getFakeEnd().y);
        }

    }

    private enum ActionType {
        MOVE_POINT, CHANGE_VECTOR, NO_ACTION, DELETE_POINT
    }

    private final class MouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent event) {
            final PointFloat p = new PointFloat(event.getPoint());
            if (!event.isMetaDown()) {
                switch (actionType) {
                    case MOVE_POINT:
                        activeLine.set(numPointMoved, p);
                        actionType = ActionType.NO_ACTION;
                        break;
                    case CHANGE_VECTOR:
                        if (numPointMoved == 0) {
                            activeLine.setStartTangent(p.sub(activeLine.getFirstPoint()));
                        }
                        if (numPointMoved == 1) {
                            activeLine.setEndTangent(p.sub(activeLine.getFirstPoint()));
                        }
                        actionType = ActionType.NO_ACTION;
                        break;
                    default:
                        activeLine.addBestFit(p);
                        break;
                }

            } else {
                switch (actionType) {
                    case DELETE_POINT:
                        if (activeLine.getPoints().size() <= MIN_POINTS_IN_LINE) {
                            break;
                        }
                        activeLine.getPoints().remove(numPointMoved);
                        actionType = ActionType.NO_ACTION;
                        break;
                    default:
                        break;
                }
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent event) {
            PointFloat p = new PointFloat(event.getPoint());
            if (!event.isMetaDown()) {
                for (int i = 0; i < activeLine.getPoints().size(); ++i) {
                    if (isInCircle(p, activeLine.get(i), POINT_DIAMETER)) {
                        actionType = ActionType.MOVE_POINT;
                        numPointMoved = i;
                        return;
                    }

                }

                if (isInCircle(p, activeLine.getFakeStart(), POINT_DIAMETER)) {
                    actionType = ActionType.CHANGE_VECTOR;
                    numPointMoved = 0;
                    return;
                }

                if (isInCircle(p, activeLine.getFakeEnd(), POINT_DIAMETER)) {
                    actionType = ActionType.CHANGE_VECTOR;
                    numPointMoved = 1;
                }
            } else {
                for (int i = 0; i < activeLine.getPoints().size(); ++i) {
                    if (isInCircle(p, activeLine.get(i), POINT_DIAMETER)) {
                        actionType = ActionType.DELETE_POINT;
                        numPointMoved = i;
                        return;
                    }
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            final PointFloat p = new PointFloat(e.getPoint());

            if (!e.isMetaDown()) {
                switch (actionType) {
                    case MOVE_POINT:
                        activeLine.set(numPointMoved, p);
                        break;
                    case CHANGE_VECTOR:
                        if (numPointMoved == 0) {
                            activeLine.setStartTangent(p.sub(activeLine.getFirstPoint()));
                        }
                        if (numPointMoved == 1) {
                            activeLine.setEndTangent(p.sub(activeLine.getFirstPoint()));
                        }
                        break;
                    default:
                        break;
                }

            }
            repaint();
        }
    }

}
