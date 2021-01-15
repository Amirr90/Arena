package com.e.arena.Model;

public class Banner {

    private String title;
    private String image;
    private String id;

    public Banner(String title, String image, String id) {
        this.title = title;
        this.image = image;
        this.id = id;
    }

    public Banner(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
}
