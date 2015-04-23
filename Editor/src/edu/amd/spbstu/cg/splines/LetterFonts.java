package edu.amd.spbstu.cg.splines;

import edu.amd.spbstu.cg.geom.PointFloat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksandra
 * @since 23.04.2015.
 */
public class LetterFonts {

    private String letter;
    private List<List<PointFloat>> points = new ArrayList<>();
    private List<PointFloat> startTangent;
    private List<PointFloat> endTangent;


    public LetterFonts(String letter, List<List<PointFloat>> points, List<PointFloat> startTangent, List<PointFloat> endTangent) {
        setLetter(letter);
        setPoints(points);
        setStartTangent(startTangent);
        setEndTangent(endTangent);
    }

    public List<List<PointFloat>> getPoints() {
        return points;
    }

    public void setPoints(List<List<PointFloat>> points) {
        this.points = points;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public List<PointFloat> getStartTangent() {
        return startTangent;
    }

    public void setStartTangent(List<PointFloat> startTangent) {
        this.startTangent = startTangent;
    }

    public List<PointFloat> getEndTangent() {
        return endTangent;
    }

    public void setEndTangent(List<PointFloat> endTangent) {
        this.endTangent = endTangent;
    }
}
