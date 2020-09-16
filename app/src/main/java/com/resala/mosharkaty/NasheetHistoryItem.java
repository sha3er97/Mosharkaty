package com.resala.mosharkaty;

import androidx.annotation.NonNull;

class NasheetHistoryItem implements Comparable {
    private String Username;
    private String history;
    private int count;
    private int monthsCount;

    public NasheetHistoryItem(String username, String history, int count, int monthsCount) {
        Username = username;
        this.history = history;
        this.count = count;
        this.monthsCount = monthsCount;
    }

    public int getMonthsCount() {
        return monthsCount;
    }

    public void setMonthsCount(int monthsCount) {
        this.monthsCount = monthsCount;
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

    public void setHistory(String history) {
        this.history = history;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        String compared = ((NasheetHistoryItem) o).Username;
        /* For Ascending order do like this */
        return this.Username.compareTo(compared);
    }
}
