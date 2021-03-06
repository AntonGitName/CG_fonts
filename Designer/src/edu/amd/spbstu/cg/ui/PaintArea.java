package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.BoundingBox;
import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.splines.HermiteSpline;
import edu.amd.spbstu.cg.util.PaintAreaState;
import edu.amd.spbstu.cg.util.UserSelectionLine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private static final int MAX_SAVED_STATES = 30;
    private static final int GRID_WIDTH = 40;
    private static final int GRID_HEIGHT = 40;
    private static final Stroke GRID_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
    private static final String PATTERN_IMAGE_FILENAME = "Designer/res/patternImage.png";

    private static final Color START_TANGENT_COLOR = Color.getColor("bronze", 0xA9A121);
    private static final Color END_TANGENT_COLOR = Color.getColor("violet", 0x8E21A9);
    private static final Color BOUNDING_BOX_COLOR = Color.getColor("gray", 0x4d4646);

    private final Paint texturePaint;
    private final DesignerPanel designerPanel;
    private final List<PaintAreaState> savedStates = new ArrayList<>();
    private BoundingBox boundingBox = new BoundingBox(50, 50, 450, 450);
    private List<UserSelectionLine> selectionLines;
    private UserSelectionLine activeLine;
    private ActionType actionType;
    private int numPointMoved = -1;
    private int currentState = -1;
    private UserSelectionLine savedLine;
    private boolean readyToMoveLine;
    private PointFloat startLineMovingPoint;

    public PaintArea(DesignerPanel designerPanel) {
        this.designerPanel = designerPanel;
        selectionLines = new ArrayList<>();
        actionType = ActionType.NO_ACTION;

        final MouseListener mouseListener = new MouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        BufferedImage imagePattern = null;
        try {
            final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PATTERN_IMAGE_FILENAME);
            if (inputStream != null) {
                imagePattern = ImageIO.read(inputStream);
            } else {
                imagePattern = ImageIO.read(new File(PATTERN_IMAGE_FILENAME));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        texturePaint = new TexturePaint(imagePattern, new Rectangle(60, 60));
    }

    private static boolean isInCircle(PointFloat p1, PointFloat p2, float d) {
        return isInCircle(p1.x, p1.y, p2.x, p2.y, d / 2);
    }

    private static boolean isInCircle(float x, float y, float x0, float y0, float r) {
        return (x - x0) * (x - x0) + (y - y0) * (y - y0) < r * r;
    }

    private static Area getResultingArea(List<Shape> shapes) {
        final Area result = new Area();
        for (Shape shape : shapes) {
            result.exclusiveOr(new Area(shape));
        }
        return result;
    }

    public Color getActiveColor() {
        return activeLine.getColor();
    }

    private void loadState(PaintAreaState state) {
        selectionLines.clear();
        for (UserSelectionLine line : state.getSelectionLines()) {
            this.selectionLines.add(new UserSelectionLine(line));
        }
        boundingBox = new BoundingBox(state.getBoundingBox());
        activeLine = selectionLines.get(state.getActiveLineIndex());
        designerPanel.restoreCurvesListWithColors(selectionLines);
        designerPanel.updateMenuButtons();
        repaint();
    }

    public void loadPrevState() {
        loadState(savedStates.get(--currentState));
    }

    public void loadNextState() {
        loadState(savedStates.get(++currentState));
    }

    public boolean hasNextState() {
        return currentState < savedStates.size() - 1;
    }

    public boolean hasPrevState() {
        return currentState > 0;
    }

    private void saveState() {
        final PaintAreaState state = new PaintAreaState(boundingBox, selectionLines, selectionLines.indexOf(activeLine));
        if (currentState != savedStates.size() - 1) {
            savedStates.removeAll(savedStates.subList(currentState + 1, savedStates.size()));
        }
        if (savedStates.size() > MAX_SAVED_STATES * 2) {
            final List<PaintAreaState> lastStates = new ArrayList<>(savedStates.subList(MAX_SAVED_STATES, savedStates.size()));
            savedStates.clear();
            savedStates.addAll(lastStates);
        }
        currentState = savedStates.size();
        savedStates.add(state);
        designerPanel.updateMenuButtons();
    }

    public List<PointFloat> getBoundingBox() {
        return boundingBox.getPoints();
    }

    public void setBoundingBox(List<PointFloat> bBox) {
        for (int i = 0; i < 4; ++i) {
            boundingBox.setPoint(bBox.get(i), i);
        }
    }

    public void setActiveLine(String colorName) {
        for (UserSelectionLine line : selectionLines) {
            if (DesignerPanel.ALL_COLORS_MAP.get(line.getColor()).equals(colorName)) {
                activeLine = line;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawFontArea(g2);
        drawCurves(g2);
        drawBoundingBox(g2);
    }

    private void drawFontArea(Graphics2D g2) {
        final List<Shape> shapes = new ArrayList<>(selectionLines.size());
        for (UserSelectionLine line : selectionLines) {
            final Path2D.Float path = new Path2D.Float();
            g2.setPaint(texturePaint);
            path.moveTo(line.getFirstPoint().x, line.getFirstPoint().y);
            if (line.size() >= MIN_POINTS_IN_LINE) {
                final List<PointFloat> spline = new HermiteSpline(line.getPoints(), line.getStartTangent().neg(), line.getEndTangent()).getSpline(POINTS_PER_SPLINE);
                for (int i = 0; i < spline.size() - 1; ++i) {
                    path.lineTo(spline.get(i).x, spline.get(i).y);
                }
            }
            path.closePath();
            shapes.add(path);
            g2.setColor(Color.BLACK);
            g2.draw(path);
        }
        g2.setPaint(texturePaint);
        g2.fill(getResultingArea(shapes));
    }

    private void drawGrid(Graphics2D g2) {
        final int width = getWidth();
        final int height = getHeight();
        int y = -GRID_HEIGHT / 2;
        int x = -GRID_WIDTH / 2;
        g2.setColor(Color.lightGray);
        g2.setStroke(GRID_STROKE);
        while (y < height) {
            g2.drawLine(0, y, width, y);
            y += GRID_HEIGHT;
        }
        while (x < width) {
            g2.drawLine(x, 0, x, height);
            x += GRID_WIDTH;
        }
    }

    private void drawCurves(Graphics2D g2) {
        for (UserSelectionLine line : selectionLines) {
            g2.setColor(line.getColor());
            for (PointFloat p : line.getPoints()) {
                g2.fillOval((int) (p.x - POINT_DIAMETER / 2), (int) (p.y - POINT_DIAMETER / 2), POINT_DIAMETER, POINT_DIAMETER);
            }
            g2.setColor(START_TANGENT_COLOR);
            g2.fillOval((int) line.getFakeStart().x - POINT_DIAMETER / 2, (int) line.getFakeStart().y - POINT_DIAMETER / 2, POINT_DIAMETER, POINT_DIAMETER);
            g2.drawLine((int) line.getFirstPoint().x, (int) line.getFirstPoint().y, (int) line.getFakeStart().x, (int) line.getFakeStart().y);
            g2.setColor(END_TANGENT_COLOR);
            g2.fillOval((int) line.getFakeEnd().x - POINT_DIAMETER / 2, (int) line.getFakeEnd().y - POINT_DIAMETER / 2, POINT_DIAMETER, POINT_DIAMETER);
            g2.drawLine((int) line.getFirstPoint().x, (int) line.getFirstPoint().y, (int) line.getFakeEnd().x, (int) line.getFakeEnd().y);
        }
    }

    private void drawBoundingBox(Graphics2D g2) {
        g2.setColor(BOUNDING_BOX_COLOR);
        g2.draw(boundingBox.getShape());
        List<PointFloat> bbPoints = boundingBox.getPoints();
        for (PointFloat p : bbPoints) {
            g2.setColor(Color.white);
            g2.fillOval((int) (p.x - POINT_DIAMETER / 2), (int) (p.y - POINT_DIAMETER / 2), POINT_DIAMETER, POINT_DIAMETER);
            g2.setColor(BOUNDING_BOX_COLOR);
            g2.drawOval((int) (p.x - POINT_DIAMETER / 2), (int) (p.y - POINT_DIAMETER / 2), POINT_DIAMETER, POINT_DIAMETER);
        }
    }

    public void addLine(Color color) {
        selectionLines.add(activeLine = new UserSelectionLine(color));
        saveState();
        repaint();
    }

    public void removeActiveLine() {
        selectionLines.remove(activeLine);
        activeLine = selectionLines.get(0);
        saveState();
        repaint();
    }

    public List<UserSelectionLine> getLineInfo() {
        return selectionLines;
    }

    public void setLines(List<UserSelectionLine> lines) {
        selectionLines = lines;
        activeLine = selectionLines.get(0);
        saveState();
        repaint();
    }

    public void doubleSavedCurve(Color color) {
        savedLine.setColor(color);
        selectionLines.add(activeLine = new UserSelectionLine(savedLine));
        designerPanel.restoreCurvesListWithColors(selectionLines);
        saveState();
        repaint();
    }

    public void saveCurrentCurve() {
        savedLine = new UserSelectionLine(activeLine);
    }

    public void moveActiveLine() {
        Cursor hourglassCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        designerPanel.setCursor(hourglassCursor);
        readyToMoveLine = true;
    }

    private enum ActionType {
        MOVE_POINT, CHANGE_VECTOR, NO_ACTION, DELETE_POINT, MOVE_BOUNDING_BOX, MOVE_LINE
    }


    private final class MouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent event) {
            final PointFloat p = new PointFloat(event.getPoint());
            if (!event.isMetaDown()) {
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
                    case MOVE_BOUNDING_BOX:
                    case MOVE_LINE:
                        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                        designerPanel.setCursor(normalCursor);
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
                        break;
                    default:
                        break;
                }
            }
            actionType = ActionType.NO_ACTION;
            saveState();
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent event) {
            PointFloat p = new PointFloat(event.getPoint());
            if (readyToMoveLine) {

                readyToMoveLine = false;
                startLineMovingPoint = p;
                actionType = ActionType.MOVE_LINE;
                return;
            }

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

            final List<PointFloat> bbPoints = boundingBox.getPoints();
            for (int i = 0; i < bbPoints.size(); ++i) {
                if (isInCircle(p, bbPoints.get(i), POINT_DIAMETER)) {
                    actionType = ActionType.MOVE_BOUNDING_BOX;
                    numPointMoved = i;
                    return;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            final PointFloat p = new PointFloat(e.getPoint());
            if (!e.isMetaDown()) {
                switch (actionType) {
                    case MOVE_LINE:
                        activeLine.moveLine(p.sub(startLineMovingPoint));
                        startLineMovingPoint = p;
                        break;
                    case MOVE_BOUNDING_BOX:
                        boundingBox.setPoint(p, numPointMoved);
                        break;
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
