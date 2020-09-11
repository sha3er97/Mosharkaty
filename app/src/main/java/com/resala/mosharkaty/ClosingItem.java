package com.resala.mosharkaty;

class ClosingItem {
    private String day;
    private int isClosed;

    public ClosingItem(String day, int isClosed) {
        this.day = day;
        this.isClosed = isClosed;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(int isClosed) {
        this.isClosed = isClosed;
    }
}