package com.example.runtrackerfragment.models;

import android.location.Location;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.List;

@Entity(tableName = "route_table")
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int routeId;

    @TypeConverters(Converters.class)
    private List<LatLng> locations;

    private float distance;
    private long duration;
    private long startTime;
    private boolean hasStarted;

    public Route(List<LatLng> locations, float distance, long duration, long startTime,
                 boolean hasStarted) {
        this.locations = locations;
        this.distance = distance;
        this.duration = duration;
        this.startTime = startTime;
        this.hasStarted = hasStarted;
    }

    @Ignore
    public Route() {
        this.locations = new ArrayList<>();
        this.distance = 0;
        this.duration = 0;
        this.startTime = 0;
        this.hasStarted = false;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getRouteId() {
        return routeId;
    }

    public List<LatLng> getLocations() {
        return locations;
    }

    public void setLocations(List<LatLng> locations) {
        this.locations = locations;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
}