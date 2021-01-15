package com.e.arena.Model;

public class GymModel {

    private String title;
    private String image;
    private String id;
    private String location;
    private long rating;
    private long price;
    private String lat;
    private String lon;

    public GymModel(String title, String image, String id, String location, long rating, long price, String lat, String lon) {
        this.title = title;
        this.image = image;
        this.id = id;
        this.location = location;
        this.rating = rating;
        this.price = price;
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public long getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public long getRating() {
        return rating;
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
