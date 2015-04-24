package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.util.UserSelectionLine;

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
    private static final double LEFT_PANE_DIVIDER = 0.9;
    private static final Map<Color, String> ALL_COLORS_MAP;
    private static Color[] ALL_COLORS = {Color.blue, Color.cyan, Color.green, Color.orange, Color.pink, Color.darkGray};
    private static String[] COLOR_NAMES = {"Blue", "Cyan", "Green", "Orange", "Pink", "Gray"};
    private final DefaultListModel<String> lineListModel = new DefaultListModel<>();
    private final JList<String> linelist;
    private final PaintArea paintArea;
    private final MainFrame mainFrame;
    static {
        ALL_COLORS_MAP = new HashMap<>();
        for (int i = 0; i < ALL_COLORS.length; ++i) {
            ALL_COLORS_MAP.put(ALL_COLORS[i], COLOR_NAMES[i]);
        }
    }

    private final JButton addLineButton;
    private final JButton removeLineButton;

    private Set<Color> availableColors;

    public DesignerPanel(MainFrame mainFrame) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;

        linelist = new JList<>(lineListModel);
        linelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linelist.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(linelist);
        final JPanel buttonsPanel = new JPanel();

        buttonsPanel.add(addLineButton = new JButton(new ImageIcon(ADD_ICON)));
        addLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLine();
            }
        });
        buttonsPanel.add(removeLineButton = new JButton(new ImageIcon(REMOVE_ICON)));
        removeLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeLine();
            }
        });

        final JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, buttonsPanel);


        paintArea = new PaintArea(this);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, paintArea);

        splitPane.setDividerLocation(DIVIDER_LOCATION);
        splitPane.setEnabled(false);

        listScrollPane.setMinimumSize(MINIMUM_SIZE);
        paintArea.setMinimumSize(MINIMUM_SIZE);

        add(splitPane);

        leftPanel.setDividerLocation(LEFT_PANE_DIVIDER);
        leftPanel.setResizeWeight(LEFT_PANE_DIVIDER);
        leftPanel.setEnabled(false);


        availableColors = new HashSet<>(ALL_COLORS_MAP.keySet());
        addLine();
        linelist.setSelectedIndex(0);
        paintArea.grabFocus();
    }


    public void addLine() {
        if (!availableColors.isEmpty()) {
            final Color color = availableColors.iterator().next();
            availableColors.remove(color);
            paintArea.addLine(color);
            lineListModel.add(lineListModel.size(), ALL_COLORS_MAP.get(color));
            sortLinelist();
            linelist.setSelectedIndex(lineListModel.size() - 1);
        }
        updateMenuButtons();
    }

    public void restoreLinelist(List<UserSelectionLine> lines, Color activeColor) {
        availableColors = new HashSet<>(ALL_COLORS_MAP.keySet());
        lineListModel.clear();
        for (UserSelectionLine line : lines) {
            final Color color = line.getColor();
            availableColors.remove(color);
            lineListModel.add(lineListModel.size(), ALL_COLORS_MAP.get(color));
        }
        linelist.setSelectedIndex(lineListModel.indexOf(ALL_COLORS_MAP.get(activeColor)));
        updateMenuButtons();
    }

    private void sortLinelist() {
        final List<String> elements = new ArrayList<>(lineListModel.size());
        for (int i = 0; i < lineListModel.size(); ++i) {
            elements.add(lineListModel.get(i));
        }
        Collections.sort(elements);
        lineListModel.removeAllElements();
        for (String element : elements) {
            lineListModel.add(lineListModel.size(), element);
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

    public void updateMenuButtons() {
        addLineButton.setEnabled(!availableColors.isEmpty());
        removeLineButton.setEnabled(lineListModel.size() > 1);
        mainFrame.updateMenuButtons(paintArea.hasPrevState(), paintArea.hasNextState(), !availableColors.isEmpty(), lineListModel.size() > 1);
    }

    public void copy() {
        paintArea.saveCurrentCurve();
        updateMenuButtons();
    }

    public void paste() {
        final Color color = availableColors.iterator().next();
        paintArea.doubleSavedCurve(color);
        updateMenuButtons();
    }

    public void undo() {
        paintArea.loadPrevState();
    }

    public void redo() {
        paintArea.loadNextState();
    }

    public void removeLine() {
        if (lineListModel.size() > 1) {
            final int x = linelist.getSelectedIndex();
            final Color color = paintArea.removeLine(x);
            availableColors.add(color);
            lineListModel.removeElementAt(x);
            sortLinelist();
            linelist.setSelectedIndex(0);
            updateMenuButtons();
        }
    }

    public void resetLines(List<UserSelectionLine> lines) {
        availableColors = new HashSet<>(ALL_COLORS_MAP.keySet());
        lineListModel.clear();
        for (UserSelectionLine line : lines) {
            final Color color = availableColors.iterator().next();
            paintArea.addLine(color);
            availableColors.remove(color);
            lineListModel.add(lineListModel.size(), "Curve " + ALL_COLORS_MAP.get(color));
            line.setColor(color);
        }
        sortLinelist();
        linelist.setSelectedIndex(0);
        paintArea.setLines(lines);
        updateMenuButtons();
    }


    public List<UserSelectionLine> getLinesInfo() {
        return paintArea.getLineInfo();
    }

    public List<PointFloat> getBoundingBox() {
        return paintArea.getBoundingBox();
    }

    public void setBoundingBox(List<PointFloat> bBox) {
        paintArea.setBoundingBox(bBox);
    }

    public void moveActiveLine() {
        paintArea.moveActiveLine();
    }
}
