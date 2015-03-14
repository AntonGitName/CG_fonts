/*
 * Created by JFormDesigner on Sat Mar 14 17:43:45 MSK 2015
 */

package edu.amd.spbstu.cg.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author unknown
 */
public class MainFrame extends JFrame {

    private static final String TITLE = "Font editor";
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

    private static final String EDITOR_PANE = "Editor";
    private static final String DESIGNER_PANE = "Designer";

    private final EditorPanel editorPanel;
    private final DesignerPanel designerPanel;


    public MainFrame() throws HeadlessException {
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

        final JMenuItem newFileMenuItem = new JMenuItem(MENU_ITEM_NEW);
        final JMenuItem saveFileMenuItem = new JMenuItem(MENU_ITEM_SAVE);
        final JMenuItem openFileMenuItem = new JMenuItem(MENU_ITEM_OPEN);
        final JMenuItem exitFileMenuItem = new JMenuItem(MENU_ITEM_EXIT);

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

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    private void createTabbedPane(EditorPanel editorPanel, DesignerPanel designerPanel) {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(EDITOR_PANE, editorPanel);
        tabbedPane.addTab(DESIGNER_PANE, designerPanel);

        add(tabbedPane);
    }
}
