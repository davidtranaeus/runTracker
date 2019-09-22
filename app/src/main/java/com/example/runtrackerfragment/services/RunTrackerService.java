package com.example.runtrackerfragment.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.runtrackerfragment.R;
import com.example.runtrackerfragment.models.Route;
import com.example.runtrackerfragment.models.RouteRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import static com.example.runtrackerfragment.App.CHANNEL_ID;

public class RunTrackerService extends Service {

    public static final String TAG = "RunTrackerServiceLog";
    public static final String START_FOREGROUND = "startForeGround";
    public static final String STOP_FOREGROUND = "stopForeGround";
    public static final String ACTIVE_ROUTE_ID = "activeRouteId";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private RouteRepository repository;
    private Route route;
    private Location previousLocation;
    private LiveData<Route> routeObservable;
    private Observer<Route> routeObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission") // Checked in TrackerFragment
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(START_FOREGROUND)) {
            int routeId = intent.getIntExtra(ACTIVE_ROUTE_ID, 0);
            routeObservable = repository.getRouteById(routeId);
            routeObserver = new Observer<Route>() {
                @Override
                public void onChanged(Route r) {
                    route = r;
                }
            };
            routeObservable.observeForever(routeObserver);

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null);

        } else if (intent.getAction().equals(STOP_FOREGROUND)) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            routeObservable.removeObserver(routeObserver);
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        repository = new RouteRepository(getApplication());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();

    }

    private void createNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RunTracker")
                .setContentText("RunTracker is currently tracking your location.")
                .setSmallIcon(R.drawable.ic_directions)
                .build();
        startForeground(1, notification);
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback =  new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    if (route.getLocations().size() == 0) {
                        route.setStartTime(location.getTime());
                    } else {
                        route.setDuration(location.getTime() - route.getStartTime());
                        route.setDistance(route.getDistance()
                                + previousLocation.distanceTo(location));
                    }

                    route.getLocations().add(new LatLng(
                            location.getLatitude(),
                            location.getLongitude()
                    ));

                    previousLocation = location;

                    repository.updateRoute(route);
                }
            }
        };
    }
}
