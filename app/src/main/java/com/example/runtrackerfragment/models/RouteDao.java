package com.example.runtrackerfragment.models;


import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.runtrackerfragment.models.Route;

import java.util.List;

@Dao
public interface RouteDao {

    @Insert
    long insert(Route route);

    @Query("SELECT * FROM route_table WHERE routeId == :id")
    LiveData<Route> getRouteById(int id);

    @Query("SELECT * FROM route_table")
        // LiveData now observes all changes in route_table and activity will be notified with Room
    LiveData<List<Route>> getAllRoutes();

    @Delete
    void delete(Route route);

    @Update
    void update(Route route);
}
