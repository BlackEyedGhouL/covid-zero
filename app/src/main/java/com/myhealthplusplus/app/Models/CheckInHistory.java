package com.myhealthplusplus.app.Models;

import java.util.List;

public class CheckInHistory {
    String date, location;
    List<String> guests;

    public CheckInHistory(String date, String location, List<String> guests) {
        this.date = date;
        this.location = location;
        this.guests = guests;
    }

    public CheckInHistory() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }
}
