package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.splines.HermiteSpline;
import edu.amd.spbstu.cg.splines.LetterFont;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class EditorPanel extends JPanel implements ListSelectionListener {
    private Map<String, LetterFont> letterFonts = new HashMap<>();
    private static final int MAX_TEXT_SIZE = 50;
    private static final int MIN_TEXT_SIZE = 5;
    private PaintArea paintArea;
    private final DefaultListModel<String> lineListModel = new DefaultListModel<>();
    private final JList<String> linelist;
    private final JTextPane textPane;
    private int textSize;
    private ArrayList<String> activeLetters = new ArrayList<>();

    public Map<String, LetterFont> getLetterFonts() {
        return letterFonts;
    }

    public EditorPanel() {
        super(new BorderLayout());
        linelist = new JList<>(lineListModel);
        linelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linelist.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(linelist);

        textPane = new JTextPane();
        textPane.getDocument().addDocumentListener(new TextChangedListener());
        final JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        paintArea = new PaintArea(this, letterFonts);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, paintArea, paneScrollPane);
        splitPane.setResizeWeight(0.9d);
        splitPane.setEnabled(false);

        final JSplitPane resultLayout = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, splitPane);
        resultLayout.setEnabled(false);

        add(resultLayout, BorderLayout.CENTER);
        textSize = 15;
    }

    public void addLetter(String letter, List<List<PointFloat>> points, List<PointFloat> startTangent, List<PointFloat> endTangent) {
        if (!activeLetters.contains(letter)) {
            activeLetters.add(letter);
            lineListModel.add(lineListModel.size(), letter);
            List<HermiteSpline> hermiteSplines = new ArrayList<>();
            for (int i = 0; i < points.size(); ++i) {
                hermiteSplines.add(new HermiteSpline(points.get(i), startTangent.get(i), endTangent.get(i)));
            }
            letterFonts.put(letter, new LetterFont(hermiteSplines));
        }
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

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    private class TextChangedListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            paintArea.printText(textPane.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            paintArea.printText(textPane.getText());

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            paintArea.printText(textPane.getText());
        }
    }
}
