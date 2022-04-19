package com.myhealthplusplus.app.Models;

import java.util.List;

public class PlacesCheckInUser {
    String date;
    List<String> guests;

    public PlacesCheckInUser(String date, List<String> guests) {
        this.date = date;
        this.guests = guests;
    }

    public PlacesCheckInUser() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }
}
