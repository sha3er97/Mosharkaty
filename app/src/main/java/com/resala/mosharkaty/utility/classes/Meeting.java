package com.resala.mosharkaty.utility.classes;

import androidx.annotation.NonNull;

public class Meeting implements Comparable {
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

  public Meeting() {}

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public int compareTo(@NonNull Object o) {
    String[] comparedString = ((Meeting) o).getDate().split("/", 2);
    int compared = Integer.parseInt(comparedString[0]); // day
    /* For Ascending order*/
    //        return this.count-compared;

    String[] other = this.date.split("/", 2);

    /* For Descending order*/
    return compared - Integer.parseInt(other[0]);
    //        return Integer.parseInt(other[0]) - compared;
  }
}
