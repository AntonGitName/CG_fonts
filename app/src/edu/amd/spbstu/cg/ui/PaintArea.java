package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.splines.HermiteSpline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
/**
 * @author iAnton
 * @since 15/03/15
 */
public class PaintArea extends JPanel {
    // Point to hold the mouse cursor's location
    private List<Point> v = new ArrayList<>();

    // constants for the drawn circle
    private final Color DRAW_COLOR = Color.RED;
    private final int DRAW_DIAMETER = 15;
    private List<Point> line = new ArrayList<>();

    // instance variables for the circle
    private Color drawColor;
    private int drawDiameter;

    // constructor
    public PaintArea() {
        drawColor = DRAW_COLOR;
        drawDiameter = DRAW_DIAMETER;
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
                    }

                } // end anonymous inner class

        ); // end call to addMouseListener

    }  // end constructor

    // draw a circle on this DrawJPanel
    private void drawJPanelMousePressed(MouseEvent event) {
        // store the location of the mouse
        Point p = event.getPoint();

        if (event.isMetaDown()) // right mouse button is pressed
        {

        } else // left mouse button is pressed
        {
            v.add(p);
            if (v.size() > 5) {
                Point q0 = new Point();
                Point qm = new Point();
                q0.x = -1;
                q0.y = 1;
                qm.x = 1;
                qm.y = 0;
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

    } // end method paintComponent

}
