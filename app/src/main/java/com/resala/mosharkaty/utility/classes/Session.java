package com.resala.mosharkaty.utility.classes;

public class Session {
  public String link;
  public String description;
  private String session_num;
  private String parentCourse;
  private boolean isFinished;

  public String getParentCourse() {
    return parentCourse;
  }

  public void setParentCourse(String parentCourse) {
    this.parentCourse = parentCourse;
  }

  public boolean isFinished() {
    return isFinished;
  }

  public void setFinished(boolean finished) {
    isFinished = finished;
  }

  public String getSession_num() {
    return session_num;
  }

  public void setSession_num(String session_num) {
    this.session_num = session_num;
  }
}
