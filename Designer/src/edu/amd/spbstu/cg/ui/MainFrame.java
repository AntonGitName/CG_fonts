/*
 * Created by JFormDesigner on Sat Mar 14 17:43:45 MSK 2015
 */

package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.util.UserSelectionLine;

import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author iAnton
 * @since 14/03/15
 */
public class MainFrame extends JFrame {

    private static final String TITLE = "Font Designer";
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    private static final String MENU_FILE = "File";
    private static final String MENU_EDIT = "Edit";
    private static final String MENU_LINE = "Line";
    private static final String MENU_ITEM_NEW = "New";
    private static final String MENU_ITEM_MOVE = "Move";
    private static final String MENU_ITEM_OPEN = "Open";
    private static final String MENU_ITEM_SAVE = "Save";
    private static final String MENU_ITEM_EXIT = "Exit";
    private static final String MENU_ITEM_UNDO = "Undo";
    private static final String MENU_ITEM_REDO = "Redo";
    private static final String MENU_ITEM_COPY = "Copy";
    private static final String MENU_ITEM_PASTE = "Paste";
    private static final String MENU_ITEM_ADD = "Add";
    private static final String MENU_ITEM_REMOVE = "Remove";

    private static final List<String> alphabet = new ArrayList<>();
    private final DesignerPanel designerPanel;

    static {
        for (char i = 'a'; i <= 'z'; ++i) {
            alphabet.add("" + i);
        }
        for (char i = 'A'; i <= 'Z'; ++i) {
            alphabet.add("" + i);
        }
    }

    private JMenuItem redoEditItem;
    private JMenuItem undoEditItem;
    private JMenuItem pasteEditItem;
    private JMenuItem addLineItem;
    private JMenuItem removeLineItem;

    public MainFrame() {
        super(TITLE);

        createMenu();
        add(designerPanel = new DesignerPanel(this));
        showGUI();
    }

    private void showGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(DEFAULT_SIZE);
        setVisible(true);
    }


    private void createMenu() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu(MENU_FILE);
        final JMenu editMenu = new JMenu(MENU_EDIT);
        final JMenu lineMenu = new JMenu(MENU_LINE);

        final JMenuItem newFileMenuItem = new JMenuItem(MENU_ITEM_NEW);
        final JMenuItem saveFileMenuItem = new JMenuItem(MENU_ITEM_SAVE);
        final JMenuItem openFileMenuItem = new JMenuItem(MENU_ITEM_OPEN);
        final JMenuItem exitFileMenuItem = new JMenuItem(MENU_ITEM_EXIT);
        newFileMenuItem.addActionListener(new OnNewListener());
        saveFileMenuItem.addActionListener(new OnSaveListener());
        openFileMenuItem.addActionListener(new OnOpenListener());
        exitFileMenuItem.addActionListener(new OnExitListener());

        newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('N', SHIFT_DOWN_MASK));
        saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', SHIFT_DOWN_MASK));
        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('L', SHIFT_DOWN_MASK));
        exitFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('Q', SHIFT_DOWN_MASK));

        fileMenu.add(newFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        fileMenu.add(openFileMenuItem);
        fileMenu.add(exitFileMenuItem);

        redoEditItem = new JMenuItem(MENU_ITEM_REDO);
        undoEditItem = new JMenuItem(MENU_ITEM_UNDO);

        final JMenuItem copyEditItem = new JMenuItem(MENU_ITEM_COPY);

        pasteEditItem = new JMenuItem(MENU_ITEM_PASTE);

        redoEditItem.addActionListener(new OnRedoListener());
        undoEditItem.addActionListener(new OnUndoListener());
        copyEditItem.addActionListener(new OnCopyListener());
        pasteEditItem.addActionListener(new OnPasteListener());


        undoEditItem.setAccelerator(KeyStroke.getKeyStroke('Z', SHIFT_DOWN_MASK));
        redoEditItem.setAccelerator(KeyStroke.getKeyStroke('Y', SHIFT_DOWN_MASK));
        pasteEditItem.setAccelerator(KeyStroke.getKeyStroke('V', SHIFT_DOWN_MASK));
        copyEditItem.setAccelerator(KeyStroke.getKeyStroke('C', SHIFT_DOWN_MASK));

        editMenu.add(redoEditItem);
        editMenu.add(undoEditItem);
        editMenu.add(copyEditItem);
        editMenu.add(pasteEditItem);

        addLineItem = new JMenuItem(MENU_ITEM_ADD);
        removeLineItem = new JMenuItem(MENU_ITEM_REMOVE);
        final JMenuItem moveLineItem = new JMenuItem(MENU_ITEM_MOVE);
        addLineItem.addActionListener(new OnAddLineListener());
        removeLineItem.addActionListener(new OnRemoveLineListener());
        moveLineItem.addActionListener(new OnMoveLineListener());

        addLineItem.setAccelerator(KeyStroke.getKeyStroke('A', SHIFT_DOWN_MASK));
        removeLineItem.setAccelerator(KeyStroke.getKeyStroke('D', SHIFT_DOWN_MASK));
        moveLineItem.setAccelerator(KeyStroke.getKeyStroke('M', SHIFT_DOWN_MASK));


        lineMenu.add(addLineItem);
        lineMenu.add(removeLineItem);
        lineMenu.add(moveLineItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(lineMenu);
        setJMenuBar(menuBar);
    }

    private boolean CheckBBox() {
        List<PointFloat> bBox;
        bBox = designerPanel.getBoundingBox();
        float leftX = bBox.get(0).x;
        float topY = bBox.get(0).y;
        float rightX = bBox.get(2).x;
        float bottomY = bBox.get(2).y;
        java.util.List<UserSelectionLine> lines = designerPanel.getLinesInfo();
        for (UserSelectionLine line : lines) {
            for (PointFloat p : line.getPoints()) {
                if (p.x > rightX || p.x < leftX || p.y > bottomY || p.y < topY) {
                    return false;
                }
            }
        }
        return true;
    }

    private void saveLetter(String s, PrintWriter writer) {

        writer.write(s + "\n"); // first string -- letter
        List<PointFloat> bBox;
        bBox = designerPanel.getBoundingBox();
        float bBoxWidth = bBox.get(1).x - bBox.get(0).x;
        float bBoxHeight = bBox.get(3).y - bBox.get(0).y;
        writer.write(bBox.get(0).x + " " + bBox.get(0).y + " " + bBoxWidth + " " + bBoxHeight + "\n");
        java.util.List<UserSelectionLine> lines = designerPanel.getLinesInfo();
        writer.write(lines.size() + "\n"); // second string -- amount of lines
        for (UserSelectionLine line : lines) {
            List<String> lineX = new ArrayList<>();
            List<String> lineY = new ArrayList<>();
            for (PointFloat p : line.getPoints()) {
                lineX.add((p.x - bBox.get(0).x) / bBoxWidth + "");
                lineY.add((p.y - bBox.get(0).y) / bBoxHeight + "");
            }
            for (String str : lineX) {
                writer.write(str + " "); // x coord of points
            }
            writer.write("\n");
            for (String str : lineY) {
                writer.write(str + " "); // y coord of points
            }
            writer.write("\n");
            writer.write((line.getFakeStart().x - bBox.get(0).x) / bBoxWidth + " " + (line.getFakeStart().y - bBox.get(0).y) / bBoxHeight + "\n"); // first string for each line if start tangent
            writer.write((line.getFakeEnd().x - bBox.get(0).x) / bBoxWidth + " " + (line.getFakeEnd().y - bBox.get(0).y) / bBoxHeight + "\n"); // second string for each line if end tangent
        }
    }

    private void loadFont(File loadFile) {
        List<UserSelectionLine> lines = new ArrayList<>();
        List<String> linesInfo = new ArrayList<>();
        try {
            linesInfo = Files.readAllLines(Paths.get(loadFile.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        linesInfo.remove(0);
        List<PointFloat> bBox = new ArrayList<>();
        String[] bBoxData = linesInfo.get(0).split(" ");
        float bBoxWidth = Float.valueOf(bBoxData[2]);
        float bBoxHeight = Float.valueOf(bBoxData[3]);
        bBox.add(new PointFloat(Float.valueOf(bBoxData[0]), Float.valueOf(bBoxData[1])));
        bBox.add(new PointFloat(Float.valueOf(bBoxData[0]) + bBoxWidth, Float.valueOf(bBoxData[1])));
        bBox.add(new PointFloat(Float.valueOf(bBoxData[0]) + bBoxWidth, Float.valueOf(bBoxData[1]) + bBoxHeight));
        bBox.add(new PointFloat(Float.valueOf(bBoxData[0]), Float.valueOf(bBoxData[1]) + bBoxHeight));
        final int shift = 4;
        linesInfo.remove(0);

        int numLines = Integer.valueOf(linesInfo.get(0));
        linesInfo.remove(0);
        for (int i = 0; i < numLines; ++i) {
            UserSelectionLine line = new UserSelectionLine();
            String[] x = linesInfo.get(i * shift).split(" ");
            String[] y = linesInfo.get(i * shift + 1).split(" ");
            for (int j = 0; j < x.length; ++j) {
                line.add(new PointFloat(Float.valueOf(x[j]) * bBoxWidth + bBox.get(0).x, Float.valueOf(y[j]) * bBoxHeight + bBox.get(0).y));
            }
            String[] s = linesInfo.get(i * shift + 2).split(" "); // first tangent

            line.setStartTangent(new PointFloat(Float.valueOf(s[0]) * bBoxWidth + bBox.get(0).x - line.get(0).x, Float.valueOf(s[1]) * bBoxHeight + bBox.get(0).y - line.get(0).y));
            s = linesInfo.get(i * shift + 3).split(" "); // first tangent
            line.setEndTangent(new PointFloat(Float.valueOf(s[0]) * bBoxWidth + bBox.get(0).x - line.get(0).x, Float.valueOf(s[1]) * bBoxHeight + bBox.get(0).y - line.get(0).y));
            lines.add(line);
        }
        designerPanel.setBoundingBox(bBox);
        designerPanel.resetLines(lines);
    }

    public void updateMenuButtons(boolean canUndo, boolean canRedo, boolean canAdd, boolean canRemove) {
        undoEditItem.setEnabled(canUndo);
        redoEditItem.setEnabled(canRedo);
        pasteEditItem.setEnabled(canAdd);
        addLineItem.setEnabled(canAdd);
        removeLineItem.setEnabled(canRemove);
    }

    private final class OnSaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!CheckBBox()) {
                return; /// SHOW MESSAGE
            }
            Object[] possibilities = alphabet.toArray();
            Frame frame = new Frame();
            String s = (String) JOptionPane.showInputDialog(
                    frame,
                    "Choose letter:",
                    "Save letter font",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    "a");

            if ((s != null) && (s.length() > 0)) {

                final JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try (final PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                        saveLetter(s, writer);
                        writer.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
    }

    private final class OnOpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                loadFont(fileChooser.getSelectedFile());
            }
        }
    }

    private final class OnExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (Frame frame : Frame.getFrames()) {
                if (frame.isActive()) {
                    WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
                    frame.dispatchEvent(windowClosing);
                }
            }
        }
    }

    private final class OnAddLineListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            designerPanel.addLine();
        }
    }

    private final class OnRemoveLineListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            designerPanel.removeLine();
        }
    }

    private final class OnNewListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            List<UserSelectionLine> lines = new ArrayList<>();
            lines.add(new UserSelectionLine(Color.RED));
            designerPanel.resetLines(lines);
        }
    }

    private final class OnRedoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            designerPanel.redo();
        }
    }

    private final class OnUndoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            designerPanel.undo();
        }
    }

    private final class OnPasteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            designerPanel.paste();
        }
    }

    private final class OnCopyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            designerPanel.copy();
        }
    }

    private final class OnMoveLineListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            designerPanel.moveActiveLine();
        }
    }
}
