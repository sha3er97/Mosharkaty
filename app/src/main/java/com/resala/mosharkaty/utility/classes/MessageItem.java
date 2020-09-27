package com.resala.mosharkaty.utility.classes;

public class MessageItem {
    public String author;
    public String content;
    public String date;
    public String key;

    public MessageItem() {
        //in case of error
        this.author = "error";
        this.content = "error";
        this.date = "1/1/2020";
    }

    public MessageItem(String author, String content, String date) {
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
}
