package edu.amd.spbstu.cg.ui;

import edu.amd.spbstu.cg.geom.PointFloat;
import edu.amd.spbstu.cg.util.UserSelectionLine;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * @author iAnton
 * @since 14/03/15
 */
class DesignerPanel extends JPanel implements ListSelectionListener {

    public static final Map<Color, String> ALL_COLORS_MAP;
    private static final Dimension MINIMUM_SIZE = new Dimension(100, 50);
    private static final int DIVIDER_LOCATION = 150;
    private static final String REMOVE_ICON = "Designer/res/remove.png";
    private static final String ADD_ICON = "Designer/res/add.png";
    private static final double LEFT_PANE_DIVIDER = 0.9;
    private static final Color[] ALL_COLORS = {Color.blue, Color.cyan, Color.green, Color.orange, Color.pink, Color.darkGray};
    private static final String[] COLOR_NAMES = {"Blue", "Cyan", "Green", "Orange", "Pink", "Gray"};
    private final DefaultListModel<String> curvesListModel = new DefaultListModel<>();
    private final JList<String> curvesList;
    private final PaintArea paintArea;
    private final MainFrame mainFrame;
    private final JButton addLineButton;
    private final JButton removeLineButton;

    private Set<Color> availableColors;

    static {
        ALL_COLORS_MAP = new HashMap<>();
        for (int i = 0; i < ALL_COLORS.length; ++i) {
            ALL_COLORS_MAP.put(ALL_COLORS[i], COLOR_NAMES[i]);
        }
    }

    public DesignerPanel(MainFrame mainFrame) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;

        curvesList = new JList<>(curvesListModel);
        curvesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        curvesList.addListSelectionListener(this);
        final JScrollPane listScrollPane = new JScrollPane(curvesList);
        final JPanel buttonsPanel = new JPanel();

        final ImageIcon addIcon = new ImageIcon();
        try {
            final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(ADD_ICON);
            if (inputStream != null) {
                addIcon.setImage(ImageIO.read(inputStream));
            } else {
                addIcon.setImage(ImageIO.read(new File(ADD_ICON)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buttonsPanel.add(addLineButton = new JButton(addIcon));
        addLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCurve();
            }
        });

        final ImageIcon removeIcon = new ImageIcon();
        try {
            final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(REMOVE_ICON);
            if (inputStream != null) {
                removeIcon.setImage(ImageIO.read(inputStream));
            } else {
                removeIcon.setImage(ImageIO.read(new File(REMOVE_ICON)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buttonsPanel.add(removeLineButton = new JButton(removeIcon));
        removeLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeCurve();
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
        addCurve();
        paintArea.grabFocus();
    }


    public void addCurve() {
        if (!availableColors.isEmpty()) {
            final Color color = availableColors.iterator().next();
            availableColors.remove(color);
            paintArea.addLine(color);
            curvesListModel.add(curvesListModel.size(), ALL_COLORS_MAP.get(color));
            sortCurvesList();
        }
        updatePanel();
    }

    public void restoreCurvesListWithColors(List<UserSelectionLine> lines) {
        availableColors = new HashSet<>(ALL_COLORS_MAP.keySet());
        curvesListModel.clear();
        for (UserSelectionLine line : lines) {
            final Color color = line.getColor();
            availableColors.remove(color);
            curvesListModel.add(curvesListModel.size(), ALL_COLORS_MAP.get(color));
        }
        updatePanel();
    }

    private void sortCurvesList() {
        final List<String> elements = new ArrayList<>(curvesListModel.size());
        for (int i = 0; i < curvesListModel.size(); ++i) {
            elements.add(curvesListModel.get(i));
        }
        Collections.sort(elements);
        curvesListModel.removeAllElements();
        for (String element : elements) {
            curvesListModel.add(curvesListModel.size(), element);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        final String selectedColor = curvesList.getSelectedValue();
        if (selectedColor != null) {
            paintArea.setActiveLine(selectedColor);
        }
    }

    public void updateMenuButtons() {
        addLineButton.setEnabled(!availableColors.isEmpty());
        removeLineButton.setEnabled(curvesListModel.size() > 1);
        mainFrame.updateMenuButtons(paintArea.hasPrevState(), paintArea.hasNextState(), !availableColors.isEmpty(), curvesListModel.size() > 1);
    }

    public void copy() {
        paintArea.saveCurrentCurve();
        updatePanel();
    }

    public void paste() {
        final Color color = availableColors.iterator().next();
        paintArea.doubleSavedCurve(color);
        updatePanel();
    }

    public void undo() {
        paintArea.loadPrevState();
    }

    public void redo() {
        paintArea.loadNextState();
    }

    public void removeCurve() {
        if (curvesListModel.size() > 1) {
            final Color selectedColor = paintArea.getActiveColor();
            paintArea.removeActiveLine();
            availableColors.add(selectedColor);
            curvesListModel.removeElementAt(curvesList.getSelectedIndex());
            updatePanel();
        }
    }

    public void restoreCurveListWithoutColors(List<UserSelectionLine> lines) {
        availableColors = new HashSet<>(ALL_COLORS_MAP.keySet());
        curvesListModel.clear();
        for (UserSelectionLine line : lines) {
            final Color color = availableColors.iterator().next();
            availableColors.remove(color);
            curvesListModel.add(curvesListModel.size(), ALL_COLORS_MAP.get(color));
            line.setColor(color);
        }
        paintArea.setLines(lines);
        updatePanel();
    }

    private void updatePanel() {
        sortCurvesList();
        final String selectedColorName = ALL_COLORS_MAP.get(paintArea.getActiveColor());
        curvesList.setSelectedIndex(curvesListModel.indexOf(selectedColorName));
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
