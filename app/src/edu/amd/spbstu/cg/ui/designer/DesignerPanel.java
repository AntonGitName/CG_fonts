package edu.amd.spbstu.cg.ui.designer;

import edu.amd.spbstu.cg.splines.UserSelectionLine;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

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
    private static Color[] ALL_COLORS = {Color.blue, Color.cyan, Color.green, Color.orange, Color.pink, Color.darkGray};
    private static String[] COLOR_NAMES = {"Blue", "Cyan", "Green", "Orange", "Pink", "Gray"};
    private final DefaultListModel<String> lineListModel = new DefaultListModel<>();
    private final JList<String> linelist;
    private final PaintArea paintArea;
    private final Map<String, Color> availableColorsMap;
    private final Map<Integer, Map.Entry<String, Color>> usedColors = new HashMap<>();


    public DesignerPanel() {
        super(new BorderLayout());
        linelist = new JList<>(lineListModel);
        linelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linelist.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(linelist);
        final JPanel buttonsPanel = new JPanel();
        //buttonsPanel.add(new JButton(new ImageIcon(ADD_ICON)));
        // buttonsPanel.add(new JButton(new ImageIcon(REMOVE_ICON)));


        JButton button;
        buttonsPanel.add(button = new JButton(new ImageIcon(ADD_ICON)));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLine();
            }
        });
        buttonsPanel.add(button = new JButton(new ImageIcon(REMOVE_ICON)));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeLine();
            }
        });

        final JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, buttonsPanel);


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

        availableColorsMap = new HashMap<>();
        for (int i = 0; i < ALL_COLORS.length; ++i) {
            availableColorsMap.put(COLOR_NAMES[i], ALL_COLORS[i]);
        }

        addLine();
        linelist.setSelectedIndex(0);
    }

    private void resetColorMap() {
        availableColorsMap.clear();
        for (int i = 0; i < ALL_COLORS.length; ++i) {
            availableColorsMap.put(COLOR_NAMES[i], ALL_COLORS[i]);
        }
    }

    public void addLine() {
        if (!availableColorsMap.isEmpty()) {
            final Map.Entry<String, Color> colorEntry = availableColorsMap.entrySet().iterator().next();
            final int i = paintArea.addLine(colorEntry.getValue());
            availableColorsMap.remove(colorEntry.getKey());
            usedColors.put(i, colorEntry);
            lineListModel.add(lineListModel.size(), "Curve " + colorEntry.getKey());
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        final JList list = (JList) e.getSource();
        final int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            paintArea.setActiveLine(selectedIndex);
        }
    }

    public void removeLine() {
        if (lineListModel.size() > 1) {
            final int x = linelist.getSelectedIndex();
            final Map.Entry<String, Color> colorEntry = usedColors.get(x);
            paintArea.removeLine(x);
            availableColorsMap.put(colorEntry.getKey(), colorEntry.getValue());
            lineListModel.removeElementAt(x);
            linelist.setSelectedIndex(0);
        }
    }

    public void resetLines(List<UserSelectionLine> lines) {
        usedColors.clear();
        resetColorMap();
        lineListModel.clear();
        for (UserSelectionLine line : lines) {
            final Map.Entry<String, Color> colorEntry = availableColorsMap.entrySet().iterator().next();
            final int i = paintArea.addLine(colorEntry.getValue());
            availableColorsMap.remove(colorEntry.getKey());
            usedColors.put(i, colorEntry);
            lineListModel.add(lineListModel.size(), "Curve " + colorEntry.getKey());
            line.setColor(colorEntry.getValue());
        }
        linelist.setSelectedIndex(0);
        paintArea.setLines(lines);

    }


    public List<UserSelectionLine> getLinesInfo() {

        return paintArea.getLineInfo();
    }
}
