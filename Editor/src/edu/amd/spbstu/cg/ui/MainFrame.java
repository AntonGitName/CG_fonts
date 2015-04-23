package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class MainFrame extends JFrame {

    private static final String TITLE = "Font Editor";
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    private static final String MENU_FILE = "File";
    private static final String MENU_EDIT = "Edit";
    private static final String MENU_ITEM_NEW = "New";
    private static final String MENU_ITEM_OPEN = "Open";
    private static final String MENU_ITEM_SAVE = "Save";
    private static final String MENU_ITEM_EXIT = "Exit";
    private static final String MENU_ITEM_UNDO = "Undo";
    private static final String MENU_ITEM_REDO = "Redo";
    private static final String MENU_ITEM_COPY = "Copy";
    private static final String MENU_ITEM_PASTE = "Paste";
    private static final String MENU_FONT_OPEN = "Paste";
    private static final String MENU_TEXT_INC = "Increase font size";
    private static final String MENU_TEXT_DEC = "Decrease font size";


    private final EditorPanel editorPanel;

    public MainFrame() {
        super(TITLE);

        add(editorPanel = new EditorPanel());
        createMenu();
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

        final JMenuItem newFileMenuItem = new JMenuItem(MENU_ITEM_NEW);
        final JMenuItem openFontMenuItem = new JMenuItem(MENU_FONT_OPEN);
        final JMenuItem saveFileMenuItem = new JMenuItem(MENU_ITEM_SAVE);
        final JMenuItem openFileMenuItem = new JMenuItem(MENU_ITEM_OPEN);
        final JMenuItem exitFileMenuItem = new JMenuItem(MENU_ITEM_EXIT);
        newFileMenuItem.addActionListener(new OnNewListener());
        saveFileMenuItem.addActionListener(new OnSaveListener());
        openFileMenuItem.addActionListener(new OnOpenListener());
        exitFileMenuItem.addActionListener(new OnExitListener());
        openFontMenuItem.addActionListener(new OnOpenFontListener());

        fileMenu.add(newFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        fileMenu.add(openFileMenuItem);
        fileMenu.add(exitFileMenuItem);

        final JMenuItem redoEditItem = new JMenuItem(MENU_ITEM_REDO);
        final JMenuItem undoEditItem = new JMenuItem(MENU_ITEM_UNDO);
        final JMenuItem copyEditItem = new JMenuItem(MENU_ITEM_COPY);
        final JMenuItem pasteEditItem = new JMenuItem(MENU_ITEM_PASTE);
        final JMenuItem textSizeInc = new JMenuItem(MENU_TEXT_INC);
        final JMenuItem textSizeDec = new JMenuItem(MENU_TEXT_DEC);

        textSizeInc.addActionListener(new IncFontListener());
        textSizeDec.addActionListener(new DecFontListener());

        editMenu.add(redoEditItem);
        editMenu.add(undoEditItem);
        editMenu.add(copyEditItem);
        editMenu.add(pasteEditItem);
        editMenu.add(textSizeInc);
        editMenu.add(textSizeDec);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    private final class OnNewListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    private final class OnSaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    private final class OnOpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
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

    private class OnOpenFontListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser dirChooser = new JFileChooser();
            dirChooser.setDialogTitle("Select folder");
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (dirChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                loadFont(dirChooser.getSelectedFile());
            }
        }
    }

    private void loadFont(File selectedFolder) {
        for (final File fileEntry : selectedFolder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (checkTypeFile(fileEntry)) {

                    List<String> linesInfo = new ArrayList<>();
                    try {
                        linesInfo = Files.readAllLines(Paths.get(fileEntry.toURI()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String letter = linesInfo.get(0);
                    linesInfo.remove(0);
                    linesInfo.remove(0); // deleting info about bounding box
                    final int shift = 4;

                    int numLines = Integer.valueOf(linesInfo.get(0));
                    linesInfo.remove(0);
                    List<List<PointFloat>> lines = new ArrayList<>();
                    List<PointFloat> startTangents = new ArrayList<>();
                    List<PointFloat> endTangents = new ArrayList<>();
                    for (int i = 0; i < numLines; ++i) {
                        List<PointFloat> line = new ArrayList<>();
                        String[] x = linesInfo.get(i * shift).split(" ");
                        String[] y = linesInfo.get(i * shift + 1).split(" ");
                        for (int j = 0; j < x.length; ++j) {
                            line.add(new PointFloat(Float.valueOf(x[j]), Float.valueOf(y[j])));
                        }
                        String[] s = linesInfo.get(i * shift + 2).split(" "); // first tangent
                        startTangents.add(new PointFloat(Float.valueOf(s[0]) - line.get(0).x, Float.valueOf(s[1]) - line.get(0).y));
                        s = linesInfo.get(i * shift + 3).split(" "); // first tangent
                        endTangents.add(new PointFloat(Float.valueOf(s[0]) - line.get(0).x, Float.valueOf(s[1]) - line.get(0).y));
                        lines.add(line);
                    }
                    editorPanel.addLetter(letter, lines, startTangents, endTangents);
                }
            }
        }
    }

    private boolean checkTypeFile(File fileEntry) {
        String fileName = fileEntry.getName();
        String[] s = fileName.split(".");
        if (s[s.length - 1] == "fnttp") {
            return true;
        }
        return false;
    }


    private class IncFontListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editorPanel.incTextSize();
        }
    }

    private class DecFontListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editorPanel.decTextSize();
        }
    }
}