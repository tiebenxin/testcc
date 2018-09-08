package com.lensim.fingerchat.commons.map;

/**
 * Created by haoyuban111 on 2017/2/20.
 */

public class Gps {
    private double latitude;
    private double longitude;

    public Gps(double wgLat, double wgLon) {
        setLatitude(wgLat);
        setLongitude(wgLon);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
