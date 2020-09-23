package com.resala.mosharkaty.utility.classes;

import androidx.annotation.NonNull;

public class UserHistoryItem implements Comparable {
  private String Username;
  private String history;
  private int count;

  public UserHistoryItem(String username, String history, int count) {
    Username = username;
    this.history = history;
    this.count = count;
  }

  public String getUsername() {
    return Username;
  }

  public void setUsername(String username) {
    Username = username;
  }

  public String getHistory() {
    return history;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public int compareTo(@NonNull Object o) {
    int compared = ((UserHistoryItem) o).getCount();
    /* For Ascending order*/
    //        return this.count-compared;

    /* For Descending order do like this */
    return compared - this.count;
  }
}
