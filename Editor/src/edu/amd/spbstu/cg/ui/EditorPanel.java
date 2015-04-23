package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.splines.HermiteSpline;
import edu.amd.spbstu.cg.splines.LetterFont;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class EditorPanel extends JPanel implements ListSelectionListener, KeyListener {
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 48;
    private final MainFrame mainFrame;
    private final Map<String, LetterFont> letterFonts = new HashMap<>();
    private final DefaultListModel<String> lineListModel = new DefaultListModel<>();
    private final JList<String> linelist;
    private final JTextPane textPane;
    private PaintArea paintArea;
    private ArrayList<String> activeLetters = new ArrayList<>();
    private SpinnerModel fontSpinnerModel = new SpinnerNumberModel(PaintArea.DEFAULT_FONT_SIZE, MIN_VALUE, MAX_VALUE, 1);

    public EditorPanel(MainFrame mainFrame) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;
        linelist = new JList<>(lineListModel);
        linelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linelist.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(linelist);

        textPane = new JTextPane();
        textPane.getDocument().addDocumentListener(new TextChangedListener());
        final JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        paintArea = new PaintArea(this, letterFonts);
        paintArea.addKeyListener(this);

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
        paintArea.grabFocus();
        add(resultLayout, BorderLayout.CENTER);
        paintArea.setFontSize((Integer) fontSpinnerModel.getValue());
    }

    public Map<String, LetterFont> getLetterFonts() {
        return letterFonts;
    }

    public void keyTyped(KeyEvent e) {
        displayInfo(e, "KEY TYPED: ");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        displayInfo(e, "KEY TYPED: ");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        displayInfo(e, "KEY TYPED: ");
    }

    private void displayInfo(KeyEvent e, String keyStatus){

        //You should only rely on the key char if the event
        //is a key typed event.
        int id = e.getID();
        String keyString;
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            int keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode
                    + " ("
                    + KeyEvent.getKeyText(keyCode)
                    + ")";
        }

        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }

        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }

        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }

        System.out.print(keyString);

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

    private final class FontChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            final int fontSize = (int) fontSpinnerModel.getValue();
            paintArea.setFontSize(fontSize);
            mainFrame.updateMenuButtons(fontSize != MIN_VALUE, fontSize != MAX_VALUE);
        }
    }
}
