package com.bradenhart.hcnavigationview;

/**
 * Created by bradenhart on 24/06/15.
 */
public class Achievement {

    private String title;
    private int imageId;
    private boolean earned;

    public Achievement() {}

    public Achievement(String title, int imageId, boolean earned) {
        this.title = title;
        this.imageId = imageId;
        this.earned = earned;
    }

    public boolean isEarned() {
        return earned;
    }

    public void setEarned(boolean earned) {
        this.earned = earned;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
