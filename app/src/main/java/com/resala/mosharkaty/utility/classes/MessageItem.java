package com.resala.mosharkaty.utility.classes;

public class MessageItem {
  public String author;
  public String content;
  public String date;
  public String key;

  public MessageItem() {}

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
