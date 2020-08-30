package com.resala.mosharkaty;

class EventItem {
    private String title;
    private String day;
    private String imgUrl;
    private String description;
    private String location;
    private String time;

    public EventItem(
            String title, String day, String imgUrl, String description, String location, String time) {
        this.title = title;
        this.day = day;
        this.imgUrl = imgUrl;
        this.description = description;
        this.location = location;
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
