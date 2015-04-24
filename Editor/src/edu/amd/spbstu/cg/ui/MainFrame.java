package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;

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

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

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
    private static final String MENU_FONT_OPEN = "Open font";
    private static final String MENU_TEXT_INC = "Increase size";
    private static final String MENU_TEXT_DEC = "Decrease size";
    private static final String DEFAULT_FONT_FOLDER = "res/defaultFont";


    private final EditorPanel editorPanel;
    private JMenuItem textSizeInc;
    private JMenuItem textSizeDec;

    public MainFrame() {
        super(TITLE);

        add(editorPanel = new EditorPanel(this));
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

        newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
        openFontMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
        saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('L', CTRL_DOWN_MASK));
        exitFileMenuItem.setAccelerator(KeyStroke.getKeyStroke('Q', CTRL_DOWN_MASK));

        fileMenu.add(newFileMenuItem);
        fileMenu.add(openFontMenuItem);
        fileMenu.add(saveFileMenuItem);
        fileMenu.add(openFileMenuItem);
        fileMenu.add(exitFileMenuItem);

        textSizeInc = new JMenuItem(MENU_TEXT_INC);
        textSizeDec = new JMenuItem(MENU_TEXT_DEC);

        textSizeInc.addActionListener(new IncFontListener());
        textSizeDec.addActionListener(new DecFontListener());

        textSizeDec.setAccelerator(KeyStroke.getKeyStroke('D', CTRL_DOWN_MASK));
        textSizeInc.setAccelerator(KeyStroke.getKeyStroke('I', CTRL_DOWN_MASK));

        editMenu.add(textSizeInc);
        editMenu.add(textSizeDec);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        loadFont(new File(DEFAULT_FONT_FOLDER));
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
        String s = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
        return s.equals(".fnttp");
    }

    public void updateMenuButtons(boolean canInc, boolean canDec) {
        textSizeInc.setEnabled(canInc);
        textSizeDec.setEnabled(canDec);
    }

    private final class OnNewListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            editorPanel.reset();
        }
    }

    private final class OnSaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {


            final JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                try (final PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                    saveText(writer);
                    writer.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    private void saveText(PrintWriter writer) {
        String text = editorPanel.getText();
        int textSize = editorPanel.getTextSize();
        writer.write(textSize + "\n" + text);
    }

    private final class OnOpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                loadText(fileChooser.getSelectedFile());
            }
        }
    }

    private void loadText(File loadFile) {
        List<String> linesInfo = new ArrayList<>();
        try {
            linesInfo = Files.readAllLines(Paths.get(loadFile.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editorPanel.setFontSize(Integer.valueOf(linesInfo.get(0)));
        editorPanel.setText(linesInfo.get(1));
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