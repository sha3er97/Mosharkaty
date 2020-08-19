package com.resala.mosharkaty;

class UserHistoryItem {
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

    public void setHistory(String history) {
        this.history = history;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
