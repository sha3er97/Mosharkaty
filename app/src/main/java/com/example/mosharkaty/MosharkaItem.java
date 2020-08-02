package com.example.mosharkaty;

class MosharkaItem {
  private String volname;
  private String mosharkaDate;
  private String mosharkaType;

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
