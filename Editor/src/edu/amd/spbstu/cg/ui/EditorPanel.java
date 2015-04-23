package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.splines.LetterFonts;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class EditorPanel extends JPanel {
    private List<LetterFonts> letterFonts = new ArrayList<>();
    private final DrawPanel drawPanel;
    private static final int MAX_TEXT_SIZE = 50;
    private static final int MIN_TEXT_SIZE = 5;
    private final JTextPane textPane;
    private int textSize;
    private ArrayList<String> activeLetters = new ArrayList<>();
    public EditorPanel() {
        super(new BorderLayout());
        drawPanel = new DrawPanel();

        textPane = new JTextPane();
        final JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, drawPanel, paneScrollPane);
        splitPane.setResizeWeight(0.9d);
        splitPane.setEnabled(false);

        add(splitPane, BorderLayout.CENTER);
        textSize = 15;
    }

    public void addLetter(String letter, List<List<PointFloat>> points, List<PointFloat> startTangent, List<PointFloat> endTangent) {
        if (activeLetters.contains(letter)) {
            return;
        } else {
            activeLetters.add(letter);
            letterFonts.add(new LetterFonts(letter, points, startTangent, endTangent));
        }
    }

    public String getText() {
        return textPane.getText();
    }

    public void setText(String text) {
        textPane.setText(text);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void incTextSize() {
        if (textSize + 5 > MAX_TEXT_SIZE) {
            textSize = MAX_TEXT_SIZE;
        } else {
            textSize += 5;
        }
    }

    public void decTextSize() {
        if (textSize - 5 < MIN_TEXT_SIZE) {
            textSize = MIN_TEXT_SIZE;
        } else {
            textSize -= 5;
        }
    }
}
