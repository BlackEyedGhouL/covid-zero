package com.myhealthplusplus.app.Models;

public class NewCasesDate {
    int date , value;

    public NewCasesDate() {
    }

    public NewCasesDate(int date, int value) {
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
