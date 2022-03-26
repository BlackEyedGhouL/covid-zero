package com.myhealthplusplus.app.Models;

public class VaccineCenter {
    private String center;
    private String district;
    private String police_area;
    private String lat;
    private String lon;

    public VaccineCenter(String center, String district, String police_area, String lat, String lon) {
        this.center = center;
        this.district = district;
        this.police_area = police_area;
        this.lat = lat;
        this.lon = lon;
    }

    public String getCenter() {
        return center;
    }

    public String getDistrict() {
        return district;
    }

    public String getPolice_area() {
        return police_area;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
