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

    public static final int DEFAULT_FONT_SIZE = 14;
    private static final int DEFAULT_LETTER_WIDTH = 50;
    private static final int DEFAULT_LETTER_HEIGHT = 80;
    private static final int DEFAULT_LETTER_DW = 25;
    private static final int DEFAULT_LETTER_DH = 30;

    private final EditorPanel editorPanel;
    private final java.util.List<Area> lettersArea = new ArrayList<>();
    private final Map<String, LetterFont> letterFonts;
    private int fontSize;
    private String savedText;

    public PaintArea(EditorPanel editorPanel, Map<String, LetterFont> letterFonts) {
        this.editorPanel = editorPanel;
        this.letterFonts = letterFonts;
    }


    public void printText(String text) {
        savedText = text;
        lettersArea.clear();
        final int width = getWidth();
        final int letterWidth = fontSize * DEFAULT_LETTER_WIDTH / DEFAULT_FONT_SIZE;
        final int letterHeight = fontSize * DEFAULT_LETTER_HEIGHT / DEFAULT_FONT_SIZE;
        final int letterDW = fontSize * DEFAULT_LETTER_DW / DEFAULT_FONT_SIZE;
        final int letterDH = fontSize * DEFAULT_LETTER_DH / DEFAULT_FONT_SIZE;
        float x = 0;
        float y = 0;
        for (char ch : text.toCharArray()) {
            if (ch == ' ') {
                x += letterWidth / 2;
                if (x + letterWidth >= width) {
                    x = 0;
                    y += letterHeight + letterDH;
                }
                continue;
            }
            if (editorPanel.getLetterFonts().containsKey("" + ch)) {
                lettersArea.add(letterFonts.get("" + ch).getArea(x, y, letterWidth, letterHeight, fontSize * 2));
                x += letterWidth + letterDW;
                if (x + letterWidth >= width) {
                    x = 0;
                    y += letterHeight + letterDH;
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.black);
        for (Area area : lettersArea) {
            g2.fill(area);
        }
    }

    public void decFontSize() {
        setFontSize(fontSize - 1);
    }

    public void incFontSize() {
        setFontSize(fontSize + 1);
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        if (savedText != null) {
            printText(savedText);
        }
    }
}
