package com.resala.mosharkaty;

class Meeting {
    public String count;
    public String date;
    public String description;
    public String from;
    public String head;
    public String location;
    public String reason;
    public String to;
    public String type;
    public String key;

    public Meeting(
            String count,
            String date,
            String description,
            String from,
            String head,
            String location,
            String reason,
            String to,
            String type) {
        this.count = count;
        this.date = date;
        this.description = description;
        this.from = from;
        this.head = head;
        this.location = location;
        this.reason = reason;
        this.to = to;
        this.type = type;
    }

    public Meeting() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
