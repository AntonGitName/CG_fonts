package edu.amd.spbstu.cg.ui;

import javax.swing.*;

/**
 * @author iAnton
 * @since 14/03/15
 */
public class MainClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
            }
        });
    }
}
