package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.splines.HermiteSpline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;

/**
 * @author iAnton
 * @since 15/03/15
 */
public class PaintArea extends JPanel {
    // Point to hold the mouse cursor's location
    float scaleFactor = 0.5f;
    private List<Point> v = new ArrayList<>();

    enum TypeAction {MOVE_POINT, CHANGE_VECTOR, NO_ACTION}

    ;
    TypeAction action;
    int num_point_moved = -1;
    Point vec_coord_0 = new Point();
    Point vec_coord_m = new Point();
    // constants for the drawn circle
    private final Color DRAW_COLOR = Color.RED;
    private final int DRAW_DIAMETER = 14;
    private List<Point> line = new ArrayList<>();

    Point q0 = new Point();
    Point qm = new Point();
    // instance variables for the circle
    private Color drawColor;
    private int drawDiameter;

    // constructor
    public PaintArea() {
        drawColor = DRAW_COLOR;
        drawDiameter = DRAW_DIAMETER;
        q0.x = -100;
        q0.y = 0;
        qm.x = 100;
        qm.y = 0;
        Point p1 = new Point(), p2 = new Point();
        p1.x = 250;
        p1.y = 250;
        v.add(p1);
        p2.x = 400;
        p2.y = 250;
        v.add(p2);
        vec_coord_0.x = (int) (p1.x + q0.x * scaleFactor);
        vec_coord_0.y = (int) (p1.y + q0.y * scaleFactor);
        vec_coord_m.x = (int) (p2.x + qm.x * scaleFactor);
        vec_coord_m.y = (int) (p2.y + qm.y * scaleFactor);
        action = TypeAction.NO_ACTION;
        addMouseListener(

                new MouseListener() // anonymous inner class
                {
                    // event handler called when a mouse button is clicked
                    public void mouseClicked(MouseEvent event) {
                    }

                    // event handler called when mouse enters this DrawJPanel
                    public void mouseEntered(MouseEvent event) {
                    }

                    // event handler called when mouse exits this DrawJPanel
                    public void mouseExited(MouseEvent event) {
                    }

                    // event handler called when a mouse button is pressed
                    public void mousePressed(MouseEvent event) {
                        drawJPanelMousePressed(event);
                    }

                    // event handler called when a mouse button is released
                    public void mouseReleased(MouseEvent event) {
                        drawJPanelMouseReleased(event);

                    }


                } // end anonymous inner class

        ); // end call to addMouseListener

    }  // end constructor

    // draw a circle on this DrawJPanel
    private void drawJPanelMousePressed(MouseEvent event) {
        // store the location of the mouse
        Point p = event.getPoint();
        for (int i = 0; i < v.size(); ++i) {
            if (pow((p.x - v.get(i).x), 2) + pow(p.y - v.get(i).y, 2) < pow(DRAW_DIAMETER / 2, 2)) {
                action = TypeAction.MOVE_POINT;
                num_point_moved = i;
                return;
            }

        }

        if (pow((p.x - vec_coord_0.x), 2) + pow(p.y - vec_coord_0.y, 2) < pow(DRAW_DIAMETER / 2, 2)) {
            action = TypeAction.CHANGE_VECTOR;
            num_point_moved = 0;
            return;
        }

        if (pow((p.x - vec_coord_m.x), 2) + pow(p.y - vec_coord_m.y, 2) < pow(DRAW_DIAMETER / 2, 2)) {
            action = TypeAction.CHANGE_VECTOR;
            num_point_moved = 1;
            return;
        }
        // repaint this DrawJPanel

    } // end method drawJPanelMousePressed

    private void drawJPanelMouseReleased(MouseEvent event) {
        // store the location of the mouse

        Point p = event.getPoint();
        if (event.isMetaDown()) // right mouse button is pressed
        {

        } else // left mouse button is pressed
        {
            switch (action) {
                case MOVE_POINT:
                    if (num_point_moved == 0) {
                        vec_coord_0.x = (int) (p.x + q0.x * scaleFactor);
                        vec_coord_0.y = (int) (p.y + q0.y * scaleFactor);
                    }
                    if (num_point_moved == v.size() - 1) {
                        vec_coord_m.x = (int) (p.x + qm.x * scaleFactor);
                        vec_coord_m.y = (int) (p.y + qm.y * scaleFactor);
                    }
                    v.get(num_point_moved).x = p.x;
                    v.get(num_point_moved).y = p.y;
                    action = TypeAction.NO_ACTION;
                    break;
                case CHANGE_VECTOR:
                    if (num_point_moved == 0) {
                        vec_coord_0.x = p.x;
                        vec_coord_0.y = p.y;
                        q0.x = (int) ((vec_coord_0.x - v.get(0).x) / scaleFactor);
                        q0.y = (int) ((vec_coord_0.y - v.get(0).y) / scaleFactor);
                    }
                    if (num_point_moved == 1) {
                        vec_coord_m.x = p.x;
                        vec_coord_m.y = p.y;
                        qm.x = (int) ((vec_coord_m.x - v.get(v.size() - 1).x) / scaleFactor);
                        qm.y = (int) ((vec_coord_m.y - v.get(v.size() - 1).y) / scaleFactor);
                    }
                    action = TypeAction.NO_ACTION;
                    break;
                default:
                    v.add(p);
                    vec_coord_m.x = (int) (p.x + qm.x * scaleFactor);
                    vec_coord_m.y = (int) (p.y + qm.y * scaleFactor);
                    break;
            }
            if (v.size() > 5) {

                line = HermiteSpline.hermite(v, q0, qm, 10);
            }
        }

        repaint(); // repaint this DrawJPanel

    } // end method drawJPanelMousePressed


    // draw a small circle at the mouse's location
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(drawColor); // set the color

        for (Point p : v) {
            // draw a filled circle at the mouse's location
            g.drawOval(p.x - drawDiameter / 2, p.y - drawDiameter / 2, drawDiameter, drawDiameter);
        }
        if (v.size() > 5) {
            for (int i = 0; i < line.size() - 1; ++i) {
                g.drawLine(line.get(i).x, line.get(i).y, line.get(i + 1).x, line.get(i + 1).y);
            }

        }
        g.setColor(Color.green);
        g.drawOval(vec_coord_0.x - drawDiameter / 2, vec_coord_0.y - drawDiameter / 2, drawDiameter, drawDiameter);
        g.drawLine(v.get(0).x, v.get(0).y, vec_coord_0.x, vec_coord_0.y);
        g.drawOval(vec_coord_m.x - drawDiameter / 2, vec_coord_m.y - drawDiameter / 2, drawDiameter, drawDiameter);
        g.drawLine(v.get(v.size() - 1).x, v.get(v.size() - 1).y, vec_coord_m.x, vec_coord_m.y);

    } // end method paintComponent

}
