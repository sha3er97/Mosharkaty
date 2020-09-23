package com.resala.mosharkaty.utility.classes;

import androidx.annotation.NonNull;

public class MosharkaItem implements Comparable {
  private String volname;
  private String mosharkaDate;
  private String mosharkaType;
  private String key;

  public MosharkaItem() {}

  public MosharkaItem(String volname, String mosharkaDate, String mosharkaType) {
    this.volname = volname;
    this.mosharkaDate = mosharkaDate;
    this.mosharkaType = mosharkaType;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getVolname() {
    return volname;
  }

  public void setVolname(String volname) {
    this.volname = volname;
  }

  public String getMosharkaDate() {
    return mosharkaDate;
  }

  public String getMosharkaType() {
    return mosharkaType;
  }

  @Override
  public int compareTo(@NonNull Object o) {
    String[] comparedString = ((MosharkaItem) o).getKey().split("&", 3);
    int compared = Integer.parseInt(comparedString[0]);
    /* For Ascending order*/
    //        return this.count-compared;

    /* For Descending order do like this */
    //    return compared.compareTo(this.mosharkaType);
    String[] other = this.key.split("&", 3);

    String[] comparedDate = ((MosharkaItem) o).getMosharkaDate().split("/", 3);
    int comparedDay = Integer.parseInt(comparedDate[0]);
    String[] otherDate = this.mosharkaDate.split("/", 3);
    if (comparedDay == Integer.parseInt(otherDate[0])) return compared - Integer.parseInt(other[0]);
    else return comparedDay - Integer.parseInt(otherDate[0]);
  }
}
