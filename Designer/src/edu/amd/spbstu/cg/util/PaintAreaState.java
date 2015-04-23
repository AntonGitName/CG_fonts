package edu.amd.spbstu.cg.util;

import edu.amd.spbstu.cg.geom.BoundingBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iAnton
 * @since 23/04/15
 */
public class PaintAreaState {
    private final BoundingBox boundingBox;
    private final List<UserSelectionLine> selectionLines;
    private final int activeLineIndex;

    public PaintAreaState(BoundingBox boundingBox, List<UserSelectionLine> selectionLines, int activeLineIndex) {
        this.activeLineIndex = activeLineIndex;
        this.boundingBox = new BoundingBox(boundingBox);
        this.selectionLines = new ArrayList<>(selectionLines.size());
        for (UserSelectionLine line : selectionLines) {
            this.selectionLines.add(new UserSelectionLine(line));
        }
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public List<UserSelectionLine> getSelectionLines() {
        return selectionLines;
    }

    public int getActiveLineIndex() {
        return activeLineIndex;
    }
}
