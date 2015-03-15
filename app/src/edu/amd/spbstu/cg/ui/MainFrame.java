/*
 * Created by JFormDesigner on Sat Mar 14 17:43:45 MSK 2015
 */

package edu.amd.spbstu.cg.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;

/**
 * @author iAnton
 * @since 14/03/15
 */
public class MainFrame extends JFrame {

    private static final String TITLE = "Font editor";
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    private static final String MENU_FILE = "File";
    private static final String MENU_EDIT = "Edit";
    private static final String MENU_LINE = "Line";
    private static final String MENU_SUB_LINE = "Edit";
    private static final String MENU_ITEM_NEW = "New";
    private static final String MENU_ITEM_OPEN = "Open";
    private static final String MENU_ITEM_SAVE = "Save";
    private static final String MENU_ITEM_EXIT = "Exit";
    private static final String MENU_ITEM_UNDO = "Undo";
    private static final String MENU_ITEM_REDO = "Redo";
    private static final String MENU_ITEM_COPY = "Copy";
    private static final String MENU_ITEM_PASTE = "Paste";
    private static final String MENU_ITEM_ADD = "Add";
    private static final String MENU_ITEM_REMOVE = "Remove";
    private static final String MENU_ITEM_SET_VECTOR = "Set Vector";
    private static final String MENU_ITEM_ADD_POINT = "Add Point";
    private static final String MENU_ITEM_REMOVE_POINT = "Remove Point";
    private static final String MENU_ITEM_MOVE_POINT = "Move Point";

    private static final String EDITOR_PANE = "Editor";
    private static final String DESIGNER_PANE = "Designer";

    private final EditorPanel editorPanel;
    private final DesignerPanel designerPanel;


    public MainFrame() {
        super(TITLE);

        editorPanel = new EditorPanel();
        designerPanel = new DesignerPanel();

        createMenu();
        createTabbedPane(editorPanel, designerPanel);
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
        final JMenu lineSubMenu = new JMenu(MENU_SUB_LINE);

        final JMenuItem newFileMenuItem = new JMenuItem(MENU_ITEM_NEW);
        final JMenuItem saveFileMenuItem = new JMenuItem(MENU_ITEM_SAVE);
        final JMenuItem openFileMenuItem = new JMenuItem(MENU_ITEM_OPEN);
        final JMenuItem exitFileMenuItem = new JMenuItem(MENU_ITEM_EXIT);
        saveFileMenuItem.addActionListener(new OnSaveListener());
        openFileMenuItem.addActionListener(new OnOpenListener());
        exitFileMenuItem.addActionListener(new OnExitListener());

        fileMenu.add(newFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        fileMenu.add(openFileMenuItem);
        fileMenu.add(exitFileMenuItem);

        final JMenuItem redoEditItem = new JMenuItem(MENU_ITEM_REDO);
        final JMenuItem undoEditItem = new JMenuItem(MENU_ITEM_UNDO);
        final JMenuItem copyEditItem = new JMenuItem(MENU_ITEM_COPY);
        final JMenuItem pasteEditItem = new JMenuItem(MENU_ITEM_PASTE);

        editMenu.add(redoEditItem);
        editMenu.add(undoEditItem);
        editMenu.add(copyEditItem);
        editMenu.add(pasteEditItem);

        final JMenuItem addPointItem = new JMenuItem(MENU_ITEM_ADD_POINT);
        final JMenuItem removePointItem = new JMenuItem(MENU_ITEM_REMOVE_POINT);
        final JMenuItem movePointItem = new JMenuItem(MENU_ITEM_MOVE_POINT);

        lineSubMenu.add(addPointItem);
        lineSubMenu.add(removePointItem);
        lineSubMenu.add(movePointItem);

        final JMenuItem addLineItem = new JMenuItem(MENU_ITEM_ADD);
        final JMenuItem removeLineItem = new JMenuItem(MENU_ITEM_REMOVE);
        final JMenuItem setVectorLineItem = new JMenuItem(MENU_ITEM_SET_VECTOR);

        lineMenu.add(addLineItem);
        lineMenu.add(removeLineItem);
        lineMenu.add(lineSubMenu);
        lineMenu.add(setVectorLineItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(lineMenu);
        setJMenuBar(menuBar);
    }

    private void createTabbedPane(EditorPanel editorPanel, DesignerPanel designerPanel) {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(EDITOR_PANE, editorPanel);
        tabbedPane.addTab(DESIGNER_PANE, designerPanel);

        add(tabbedPane);
    }

    private final class OnSaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                try (final PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                    writer.print(editorPanel.getText());
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private final class OnOpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                try {
                    editorPanel.setText(new String(Files.readAllBytes(Paths.get(fileChooser.getSelectedFile().toURI()))));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private final class OnExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (Frame frame : Frame.getFrames())
            {
                if (frame.isActive())
                {
                    WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
                    frame.dispatchEvent(windowClosing);
                }
            }
        }
    }
}
