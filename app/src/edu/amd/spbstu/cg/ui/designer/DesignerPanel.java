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

    private final JList linelist;
    private final PaintArea paintArea;

    public DesignerPanel() {
        super(new BorderLayout());
        linelist = new JList();
        linelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linelist.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(linelist);

        paintArea = new PaintArea();

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, paintArea);

        splitPane.setDividerLocation(DIVIDER_LOCATION);
        splitPane.setEnabled(false);

        listScrollPane.setMinimumSize(MINIMUM_SIZE);
        paintArea.setMinimumSize(MINIMUM_SIZE);

        add(splitPane);
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
