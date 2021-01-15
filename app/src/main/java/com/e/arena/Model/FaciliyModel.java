package com.e.arena.Model;

import java.lang.ref.SoftReference;

public class FaciliyModel {
    private String image;
    private String title;

    public FaciliyModel(String image, String title) {
        this.image = image;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
