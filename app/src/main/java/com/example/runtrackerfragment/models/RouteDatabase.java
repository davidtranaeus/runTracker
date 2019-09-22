package com.example.runtrackerfragment.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = Route.class, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class RouteDatabase extends RoomDatabase {

    private static RouteDatabase instance;

    // We use this method to access our dao, room takes care of the code body
    // when we use the Room.databaseBuilder below
    public abstract RouteDao routeDao();

    // Only one thread at a time can access this method, so we don't get two instances
    public static synchronized RouteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RouteDatabase.class, "route_database")
                    .fallbackToDestructiveMigration() // handle migration crashes
                    .build(); // build instance of this database
        }
        return instance;
    }
}
