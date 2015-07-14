package com.bradenhart.hcnavigationview;

import java.io.Serializable;

/**
 * Created by bradenhart on 25/06/15.
 */
public class Challenge implements Serializable {

    private String title;
    private String description;
    private String difficulty;
    private boolean completed;
    private int groupSize;
    private int groupMin;
    private int groupMax;

    public Challenge() {}

    public Challenge(boolean completed, String title, String description, String difficulty, int groupMax, int groupMin) {
        this.completed = completed;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.groupMax = groupMax;
        this.groupMin = groupMin;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getGroupMax() {
        return groupMax;
    }

    public void setGroupMax(int groupMax) {
        this.groupMax = groupMax;
    }

    public int getGroupMin() {
        return groupMin;
    }

    public void setGroupMin(int groupMin) {
        this.groupMin = groupMin;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Challenge challenge = (Challenge) o;

        if (completed != challenge.completed) return false;
        if (groupSize != challenge.groupSize) return false;
        if (groupMin != challenge.groupMin) return false;
        if (groupMax != challenge.groupMax) return false;
        if (!description.equals(challenge.description)) return false;
        return difficulty.equals(challenge.difficulty);

    }

    @Override
    public int hashCode() {
        int result = description.hashCode();
        result = 31 * result + difficulty.hashCode();
        result = 31 * result + (completed ? 1 : 0);
        result = 31 * result + groupSize;
        result = 31 * result + groupMin;
        result = 31 * result + groupMax;
        return result;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "completed=" + completed +
                ", description='" + description + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", groupSize=" + groupSize +
                ", groupMin=" + groupMin +
                ", groupMax=" + groupMax +
                '}';
    }
}
