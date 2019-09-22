package com.example.runtrackerfragment.models;

import android.location.Location;
import android.util.Log;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converters {

    private static final String TAG = "ConvertersLog";

    @TypeConverter
    public static List<LatLng> fromString(String value) {
        Type type = new TypeToken<List<LatLng>>() {}.getType();
        return new Gson().fromJson(value, type);
    }

    @TypeConverter
    public static String fromList(List<LatLng> list) {
        return new Gson().toJson(list);
    }
}
