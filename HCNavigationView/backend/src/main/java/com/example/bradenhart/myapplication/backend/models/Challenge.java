package com.example.bradenhart.myapplication.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by bradenhart on 4/09/15.
 */
@Entity
public class Challenge {

    @Id
    private Long id;
    private Integer batch;
    private String name;
    private String description;
    private String difficulty;
    private Boolean completed;
    private Integer groupMin;
    private Integer groupmax;
    private Integer completedWith;


    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getCompletedWith() {
        return completedWith;
    }

    public void setCompletedWith(Integer completedWith) {
        this.completedWith = completedWith;
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

    public Integer getGroupmax() {
        return groupmax;
    }

    public void setGroupmax(Integer groupmax) {
        this.groupmax = groupmax;
    }

    public Integer getGroupMin() {
        return groupMin;
    }

    public void setGroupMin(Integer groupMin) {
        this.groupMin = groupMin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
