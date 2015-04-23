package edu.amd.spbstu.cg;

import edu.amd.spbstu.cg.ui.MainFrame;

import javax.swing.*;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class MainClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}