package com.example.refresher2.Model;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.refresher2.MainActivity;

public class Loc {
    private double Latitude;
    private double Longitude;
    private double Altitude;
    private float Direction;


    public void registerToilet(Location location){
        if(location == null) return;
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();
        Altitude = location.getAltitude();
        Direction = location.getBearing();
        MainActivity.locList.add(this);
    }

    public Loc() {
    }

    public Loc(double latitude, double longitude, double altitude, float direction) {
        Latitude = latitude;
        Longitude = longitude;
        Altitude = altitude;
        Direction = direction;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getAltitude() {
        return Altitude;
    }

    public void setAltitude(double altitude) {
        Altitude = altitude;
    }

    public float getDirection() {
        return Direction;
    }

    public void setDirection(float direction) {
        Direction = direction;
    }
}
