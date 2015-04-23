package edu.amd.spbstu.cg.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class EditorPanel extends JPanel {

    private final DrawPanel drawPanel;
    private final JTextPane textPane;

    public EditorPanel() {
        super(new BorderLayout());
        drawPanel = new DrawPanel();

        textPane = new JTextPane();
        final JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, drawPanel, paneScrollPane);
        splitPane.setResizeWeight(0.9d);
        splitPane.setEnabled(false);

        add(splitPane, BorderLayout.CENTER);
    }

    public String getText() {
        return textPane.getText();
    }

    public void setText(String text) {
        textPane.setText(text);
    }
}
