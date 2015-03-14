package edu.amd.spbstu.cg;

import edu.amd.spbstu.cg.ui.MainFrame;

import javax.swing.*;

/**
 * @author iAnton
 * @since 14/03/15
 */
public class MainClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.showGUI();
            }
        });
    }
}
