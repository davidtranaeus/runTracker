package com.example.runtrackerfragment.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TrackerViewModel extends AndroidViewModel {

    public static final String TAG = "TrackerViewModelLog";
    private boolean isTracking;
    private RouteRepository repository;
    private LiveData<Route> route;
    private LiveData<List<Route>> allRoutes;
    private int activeRouteId;


    public TrackerViewModel(@NonNull Application application) {
        super(application);
        isTracking = false;
        repository = new RouteRepository(application);
        allRoutes = repository.getAllRoutes();
    }

    public int startNewRoute() {
        isTracking = true;
        activeRouteId = (int) repository.insert(new Route());
        route = repository.getRouteById(activeRouteId);
        return activeRouteId;
    }

    public void loadTrackedRoute() {
        route = repository.getRouteById(activeRouteId);
    }

    public int getActiveRouteId() {
        return activeRouteId;
    }

    public void finishRoute() {
        isTracking = false;
    }

    public boolean isTracking() {
        return isTracking;
    }

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }

    public LiveData<Route> getRoute() {
        return route;
    }

    public void setRouteById(int routeId) {
        route = repository.getRouteById(routeId);
    }

    public void deleteRoute(Route route) {
        repository.deleteRoute(route);
    }

}
