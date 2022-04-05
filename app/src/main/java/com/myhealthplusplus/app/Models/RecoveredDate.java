package com.myhealthplusplus.app.Models;

public class RecoveredDate {
    int date , value;

    public RecoveredDate() {
    }

    public RecoveredDate(int date, int value) {
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
