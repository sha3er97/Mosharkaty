package com.resala.mosharkaty.utility.classes;

public class Event {
    public String Eventname;
    public String date;
    public String description;
    public String type;
    public String location;
    public String time;

    public Event() {
    }

    public Event(String eventname, String date, String description, String type, String location, String time) {
        Eventname = eventname;
        this.date = date;
        this.description = description;
        this.type = type;
        this.location = location;
        this.time = time;
    }
}
