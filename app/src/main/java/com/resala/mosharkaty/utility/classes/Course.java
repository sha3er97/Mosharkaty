package com.resala.mosharkaty.utility.classes;

public class Course {
    public String description;
    public String instructor;
    public String level;
    public String name;
    public String style;
    public String type;

    public Course() {
    }

    public Course(String description, String instructor, String level, String name, String style, String type) {
        this.description = description;
        this.instructor = instructor;
        this.level = level;
        this.name = name;
        this.style = style;
        this.type = type;
    }
}
