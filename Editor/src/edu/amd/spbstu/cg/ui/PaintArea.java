package edu.amd.spbstu.cg.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Aleksandra
 * @since 23.04.2015.
 */
public class PaintArea extends JPanel implements ActionListener {
    EditorPanel editorPanel;

    public PaintArea(EditorPanel editorPanel) {
        addKeyListener(new TAdapter());
        this.editorPanel = editorPanel;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            System.out.print("KeyReleased\n");
        }

        public void keyPressed(KeyEvent e) {
            System.out.print("KeyPressed\n");
        }
    }
}
