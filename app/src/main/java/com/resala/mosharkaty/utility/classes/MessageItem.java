package com.resala.mosharkaty.utility.classes;

public class MessageItem {
    private String author;
    private String content;
    private String date;
    private String key;

    public MessageItem(String author, String content, String date) {
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public MessageItem() {
        this.author = "error";
        this.content = "error";
        this.date = "error";
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

    public void setAuthor(String author) {
        this.author = author;
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
