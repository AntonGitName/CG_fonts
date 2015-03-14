package edu.amd.spbstu.cg.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author iAnton
 * @since 14/03/15
 */
public class MainFrame extends JFrame {

    private static final String TITLE = "Font editor";
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    public MainFrame() throws HeadlessException {
        super(TITLE);
    }

    public void showGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(DEFAULT_SIZE);
        setVisible(true);
    }
}
