package com.resala.mosharkaty.utility.classes;

import androidx.annotation.NonNull;

public class EventItem implements Comparable {
    private String title;
    private String day;
    private String imgUrl;
    private String description;
    private String location;
    private String time;
    private String key;

    public EventItem(
            String title,
            String day,
            String imgUrl,
            String description,
            String location,
            String time,
            String key) {
        this.title = title;
        this.day = day;
        this.imgUrl = imgUrl;
        this.description = description;
        this.location = location;
        this.time = time;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    @Override
    public int compareTo(@NonNull Object o) {
        String[] comparedString = ((EventItem) o).getDay().split("/", 2);
        int compared = Integer.parseInt(comparedString[0]); // day
        /* For Ascending order*/
        //        return this.count-compared;

        String[] other = this.day.split("/", 2);

        /* For Descending order*/
        //        return compared - Integer.parseInt(other[0]);
        return Integer.parseInt(other[0]) - compared;
    }
}
