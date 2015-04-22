package edu.amd.spbstu.cg.ui.designer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
/**
 * @author iAnton
 * @since 14/03/15
 */
public class DesignerPanel extends JPanel implements ListSelectionListener {

    private static final Dimension MINIMUM_SIZE = new Dimension(100, 50);
    private static final int DIVIDER_LOCATION = 150;

    private static final String REMOVE_ICON = "res/remove.png";
    private static final String ADD_ICON = "res/add.png";

    private static final double LEFT_PANEL_DIVIDER = 0.9;

    private final JList linelist;
    private final PaintArea paintArea;

    public DesignerPanel() {
        super(new BorderLayout());
        linelist = new JList();
        linelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linelist.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(linelist);
        final JPanel bottomPanel = new JPanel();
        final JPanel buttonsPanel = new JPanel();
        final JPanel textPanel = new JPanel();
        bottomPanel.add(buttonsPanel);
        bottomPanel.add(textPanel);
        buttonsPanel.add(new JButton(new ImageIcon(ADD_ICON)));
        buttonsPanel.add(new JButton(new ImageIcon(REMOVE_ICON)));
        textPanel.add(new JTextArea("Current letter"));
        textPanel.setEnabled(false);
        final JSplitPane leftPanelB = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonsPanel, textPanel);
        final JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, leftPanelB);

        paintArea = new PaintArea();
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, paintArea);

        splitPane.setDividerLocation(DIVIDER_LOCATION);
        splitPane.setEnabled(false);

        listScrollPane.setMinimumSize(MINIMUM_SIZE);
        paintArea.setMinimumSize(MINIMUM_SIZE);

        add(splitPane);

        leftPanel.setDividerLocation(LEFT_PANEL_DIVIDER);
        leftPanel.setResizeWeight(LEFT_PANEL_DIVIDER);
        leftPanel.setEnabled(false);
        leftPanelB.setEnabled(false);
    }

    public void addLine() {
        paintArea.addLine();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        final JList list = (JList) e.getSource();
        final int selectedIndex = list.getSelectedIndex();

        paintArea.setActiveLine(selectedIndex);
    }

    public void removeLine() {
        paintArea.removeLine();
    }
}
