package com.example.game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A JsonFormatter class that represents a JsonFormatter.
 * This class is used to store the data of the user's progress.
 *
 * @author Muyang Li
 * @version 2023
 */
public class JsonFormatter implements Serializable {
    private Integer scoreNum = 0;
    private Integer comboCount = 0;

    private Integer level = 1;

    private int[][] grid;

    private Integer scoreAchieved = 0;

    private List<Mino> minos = new ArrayList<>();  // minos on the board
    private Mino selected; // the mino that is going to be moved
    private Mino minoInQueue; // the mino that is going to be selected on the board
    private Mino minoPreview; // the mino that is going to be placed on the board

    /**
     * Get scoreNum.
     *
     * @return Integer score number
     */
    public Mino getMinoPreview() {
        return minoPreview;
    }
    /**
     * Set scoreNum.
     *
     * @param minoPreview minoPreview
     */
    public void setMinoPreview(final Mino minoPreview) {
        this.minoPreview = minoPreview;
    }

    /**
     * Get minoInQueue.
     *
     * @return Mino minoInQueue
     */
    public Mino getMinoInQueue() {
        return minoInQueue;
    }

    /**
     * Set minoInQueue.
     *
     * @param minoInQueue minoInQueue
     */
    public void setMinoInQueue(final Mino minoInQueue) {
        this.minoInQueue = minoInQueue;
    }

    /**
     * Get selected.
     *
     * @return Mino selected
     */
    public Mino getSelected() {
        return selected;
    }

    /**
     * Set selected mino.
     *
     * @param selected selected mino
     */
    public void setSelected(final Mino selected) {
        this.selected = selected;
    }

    /**
     * Get minos.
     *
     * @return List<Mino> minos
     */
    public List<Mino> getMinos() {
        return minos;
    }

    /**
     * Set minos.
     *
     * @param minos minos
     */
    public void setMinos(final List<Mino> minos) {
        this.minos = minos;
    }

    /**
     * Get scoreAchieved.
     *
     * @return Integer scoreAchieved
     */
    public Integer getScoreAchieved() {
        return scoreAchieved;
    }

    /**
     * Set scoreAchieved.
     *
     * @param scoreAchieved scoreAchieved
     */
    public void setScoreAchieved(final Integer scoreAchieved) {
        this.scoreAchieved = scoreAchieved;
    }

    /**
     * Get grid.
     *
     * @return int[][] grid
     */
    public int[][] getGrid() {
        return grid;
    }

    /**
     * Set grid.
     *
     * @param grid grid
     */
    public void setGrid(final int[][] grid) {
        this.grid = grid;
    }

    /**
     * Get level.
     *
     * @return Integer level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * Set level.
     *
     * @param level level
     */
    public void setLevel(final Integer level) {
        this.level = level;
    }

    /**
     * Get comboCount.
     *
     * @return Integer comboCount
     */
    public Integer getComboCount() {
        return comboCount;
    }

    /**
     * Set comboCount.
     *
     * @param comboCount comboCount
     */
    public void setComboCount(final Integer comboCount) {
        this.comboCount = comboCount;
    }

    /**
     * Get scoreNum.
     *
     * @return Integer scoreNum
     */
    public Integer getScoreNum() {
        return scoreNum;
    }

    /**
     * Set scoreNum.
     *
     * @param scoreNum scoreNum
     */
    public void setScoreNum(final Integer scoreNum) {
        this.scoreNum = scoreNum;
    }
    /**
     * Generate a string representation of the JsonFormatter object.
     *
     * @return String representation of the JsonFormatter object
     */
    @Override
    public String toString() {
        return "JsonFormatter{"
                + "scoreNum=" + scoreNum
                + ", comboCount=" + comboCount
                + ", level=" + level
                + ", scoreAchieved=" + scoreAchieved
                + '}';
    }
    /**
     * Compare two JsonFormatter objects.
     *
     * @param o The object to be compared
     * @return boolean whether the two objects are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof JsonFormatter that)) {
            return false;
        } else {
            return scoreNum.equals(that.scoreNum)
                    && comboCount.equals(that.comboCount)
                    && level.equals(that.level)
                    && scoreAchieved.equals(that.scoreAchieved);
        }
    }
    /**
     * Generate a hashcode for the JsonFormatter object.
     *
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return scoreNum.hashCode() + comboCount.hashCode() + level.hashCode() + scoreAchieved.hashCode();
    }
}
