package com.resala.mosharkaty;

class MosharkaItem {
  private String volname;
  private String mosharkaDate;
  private String mosharkaType;
  private String key;

  public MosharkaItem() {
    this.volname = "error";
    this.mosharkaDate = "error";
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
}
