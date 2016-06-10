package com.example.lsoco_user.app.afpromotion;

/**
 * Holds the feed
 */
public class Promotion {
    private String title;
    private String image;

    public Promotion(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public Promotion() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title: ").append(title).append('\n').append("Image: ").append(image);
        return stringBuilder.toString();
    }
}
