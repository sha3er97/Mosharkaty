package com.resala.mosharkaty.utility.classes;

public class Signature {
    private String comment;
    private String name;
    private String time;

    public Signature(String comment, String name, String time) {
        this.comment = comment;
        this.name = name;
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
