package com.bradenhart.hcdemoui.database;

import java.util.Date;

/**
 * Created by bradenhart on 17/12/15.
 */
public class Challenge {

    private String objectId;
    private String name;
    private String description;
    private String difficulty;
    private Integer groupMin;
    private Integer groupMax;
    private Date createdAt;
    private Boolean completed;

    public Challenge() {
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public Integer getGroupMax() {
        return groupMax;
    }

    public void setGroupMax(Integer groupMax) {
        this.groupMax = groupMax;
    }

    public Integer getGroupMin() {
        return groupMin;
    }

    public void setGroupMin(Integer groupMin) {
        this.groupMin = groupMin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "completed=" + completed +
                ", objectId='" + objectId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", groupMin=" + groupMin +
                ", groupMax=" + groupMax +
                ", createdAt=" + createdAt +
                '}';
    }

}
