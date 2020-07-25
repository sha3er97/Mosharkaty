package com.example.mosharkaty;

class EventItem {
    private String title;
    private String day;
    private String imgUrl;

    public EventItem(String title, String day, String imgUrl) {
        this.title = title;
        this.day = day;
        this.imgUrl = imgUrl;
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
