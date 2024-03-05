package com.resala.mosharkaty.utility.classes;

import androidx.annotation.NonNull;

public class VolunteerHistoryItem implements Comparable {
    private String Username;
    private final String history;
    private int count;
    private final int monthsCount;

    public VolunteerHistoryItem(String username, String history, int count, int monthsCount) {
        Username = username;
        this.history = history;
        this.count = count;
        this.monthsCount = monthsCount;
    }

    public int getMonthsCount() {
        return monthsCount;
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
        String compared = ((VolunteerHistoryItem) o).Username;
        /* For Ascending order do like this */
        return this.Username.compareTo(compared);
    }
}
