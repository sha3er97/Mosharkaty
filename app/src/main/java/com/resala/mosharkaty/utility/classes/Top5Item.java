package com.resala.mosharkaty.utility.classes;

import androidx.annotation.NonNull;

public class Top5Item implements Comparable {
    public String name;
    public int total;
    public int progress; // based on percent
    public boolean isDay;

    public Top5Item(String name, int total, int progress, boolean isDay) {
        this.name = name;
        this.total = total;
        this.progress = progress;
        this.isDay = isDay;
    }

    public boolean isDay() {
        return isDay;
    }

    public void setDay(boolean day) {
        isDay = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int compared = ((Top5Item) o).total;
        /* For Ascending order*/
        //        return this.count-compared;

        /* For Descending order*/
        return compared - this.total;
    }
}
