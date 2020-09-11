package com.resala.mosharkaty;

import androidx.annotation.NonNull;

class MosharkaItem implements Comparable {
    private String volname;
    private String mosharkaDate;
    private String mosharkaType;
    private String key;

    public MosharkaItem() {
        this.volname = "error";
        this.mosharkaDate = "1/1/2020";
        this.mosharkaType = "error";
    }

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

    public void setMosharkaDate(String mosharkaDate) {
        this.mosharkaDate = mosharkaDate;
    }

    public String getMosharkaType() {
        return mosharkaType;
    }

    public void setMosharkaType(String mosharkaType) {
        this.mosharkaType = mosharkaType;
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
        return compared - Integer.parseInt(other[0]);
    }
}
