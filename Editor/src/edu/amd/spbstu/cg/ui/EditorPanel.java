package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.splines.HermiteSpline;
import edu.amd.spbstu.cg.splines.LetterFont;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class EditorPanel extends JPanel implements ListSelectionListener {
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 48;
    private final MainFrame mainFrame;
    private final Map<String, LetterFont> letterFonts = new HashMap<>();
    private final DefaultListModel<String> lineListModel = new DefaultListModel<>();
    private final JList<String> linelist;
    private final JTextPane textPane;
    private final SpinnerModel fontSpinnerModel = new SpinnerNumberModel(PaintArea.DEFAULT_FONT_SIZE, MIN_VALUE, MAX_VALUE, 1);
    private PaintArea paintArea;
    private ArrayList<String> activeLetters = new ArrayList<>();
    private Integer fontSize;

    public EditorPanel(MainFrame mainFrame) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;
        linelist = new JList<>(lineListModel);
        linelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linelist.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(linelist);

        textPane = new JTextPane();
        textPane.getDocument().addDocumentListener(new TextChangedListener());
        textPane.addKeyListener(new TextKeyListener());
        final JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        paintArea = new PaintArea(this, letterFonts);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, paintArea, paneScrollPane);
        splitPane.setDividerLocation(0.9);
        splitPane.setResizeWeight(0.9);
        splitPane.setEnabled(false);

        final JPanel fontPanel = new JPanel();
        fontPanel.add(new JLabel("Font Size"));
        fontPanel.add(new JSpinner(fontSpinnerModel));
        fontSpinnerModel.addChangeListener(new FontChangeListener());

        final JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, fontPanel);
        leftSplitPane.setDividerLocation(0.9);
        leftSplitPane.setResizeWeight(0.9);
        leftSplitPane.setEnabled(false);

        final JSplitPane resultLayout = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, splitPane);
        resultLayout.setDividerLocation(75);
        resultLayout.setEnabled(false);

        add(resultLayout, BorderLayout.CENTER);
        paintArea.setFontSize((Integer) fontSpinnerModel.getValue());
    }

    public Map<String, LetterFont> getLetterFonts() {
        return letterFonts;
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

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    public void incTextSize() {
        paintArea.incFontSize();
        fontSpinnerModel.setValue((int) fontSpinnerModel.getValue() + 1);
    }

    public void decTextSize() {
        paintArea.decFontSize();
        fontSpinnerModel.setValue((int) fontSpinnerModel.getValue() - 1);
    }

    public void reset() {
        textPane.setText("");
    }

    public String getText() {
        return textPane.getText();
    }

    public void setText(String text) {
        textPane.setText(text);
    }

    public void setFontSize(Integer fontSize) {
        fontSpinnerModel.setValue(fontSize);
    }

    public int getTextSize() {
        return (int) fontSpinnerModel.getValue();
    }

    private class TextChangedListener implements DocumentListener {
        private void processText() {
            paintArea.printText(textPane.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processText();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processText();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processText();
        }
    }

    private final class FontChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            final int fontSize = (int) fontSpinnerModel.getValue();
            paintArea.setFontSize(fontSize);
            mainFrame.updateMenuButtons(fontSize != MIN_VALUE, fontSize != MAX_VALUE);
        }
    }

    private final class TextKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (c != ' ' && !letterFonts.keySet().contains("" + c)) {
                e.consume();
            }
        }
    }
}