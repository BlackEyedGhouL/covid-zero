package com.myhealthplusplus.app.Models;

public class DeathDate {
    int date , value;

    public DeathDate() {
    }

    public DeathDate(int date, int value) {
        this.date = date;
        this.value = value;
    }

    public int getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }
}
