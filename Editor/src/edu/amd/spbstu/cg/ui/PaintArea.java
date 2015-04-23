package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.splines.LetterFont;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Aleksandra
 * @since 23.04.2015.
 */
public class PaintArea extends JPanel {
    private final EditorPanel editorPanel;
    private final java.util.List<Area> lettersArea = new ArrayList<>();
    private final Map<String, LetterFont> letterFonts;

    public PaintArea(EditorPanel editorPanel, Map<String, LetterFont> letterFonts) {
        this.editorPanel = editorPanel;
        this.letterFonts = letterFonts;
    }


    public void printText(String text) {
        lettersArea.clear();
        float x = 0;
        float y = 0;
        for (char ch : text.toCharArray()) {
            if (editorPanel.getLetterFonts().containsKey("" + ch)) {
                lettersArea.add(letterFonts.get("" + ch).getArea(x, y, 50, 70, 10));
                x += 50;
                if (x == 300) {
                    x = 0;
                    y += 50;
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        for (Area area : lettersArea) {
            g2.fill(area);
        }
    }
}
