package com.example.runtrackerfragment.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.runtrackerfragment.R;
import com.example.runtrackerfragment.models.Route;
import com.example.runtrackerfragment.models.TrackerViewModel;
import com.example.runtrackerfragment.services.RunTrackerService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import static com.example.runtrackerfragment.services.RunTrackerService.ACTIVE_ROUTE_ID;

public class TrackerFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "TrackerFragmentLog";

    private GoogleMap mMap;
    private Button button;
    private TextView distanceTextView;
    private TextView durationTextView;
    private static final int REQUEST_FINE_LOCATION = 1;
    private TrackerViewModel viewModel;
    private TrackerFragmentListener listener;
    private Polyline polyline;
    private Observer<Route> routeObserver;


    public interface TrackerFragmentListener {
        void onFinishedButtonPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TrackerFragmentListener) {
            listener = (TrackerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LandingFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tracker, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) v.findViewById(R.id.toolbar));
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(TrackerViewModel.class);
        setGUI(view);
        setMapFragment();
    }

    private void setGUI(View view) {
        distanceTextView = view.findViewById(R.id.distance_text_view);
        durationTextView = view.findViewById(R.id.duration_text_view);

        button = view.findViewById(R.id.stop_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking();
            }
        });
    }

    private void setMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        trackLocation();
    }

    private void trackLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        } else {
            startTracking();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Toast.makeText(getActivity(), "RunTracker requires permission to track your location" +
                        " in order to start a new route.", Toast.LENGTH_LONG).show();
                getFragmentManager().popBackStack();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startTracking() {
        if (viewModel.isTracking()) {
            viewModel.loadTrackedRoute();
        } else {
            int routeId = viewModel.startNewRoute();
            startService(routeId);
        }
        createObserver();
        viewModel.getRoute().observe(getActivity(), routeObserver);
    }

    private void startService(int routeId) {
        Intent serviceIntent = new Intent(getActivity(), RunTrackerService.class);
        serviceIntent.putExtra(ACTIVE_ROUTE_ID, routeId);
        serviceIntent.setAction(RunTrackerService.START_FOREGROUND);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }

    private void createObserver() {
        routeObserver = new Observer<Route>() {
            @Override
            public void onChanged(Route route) {
                if (route.getLocations().size() == 0) {
                    return;
                }

                if (polyline != null) {
                    polyline.remove();
                }

                polyline = mMap.addPolyline(new PolylineOptions()
                        .addAll(route.getLocations())
                        .width(5)
                        .color(Color.RED));

                LatLng position = route.getLocations().get(route.getLocations().size() - 1);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

                distanceTextView.setText(getString(R.string.distance, ""+route.getDistance()));
                durationTextView.setText(getString(R.string.duration, ""+route.getDuration() / 1000));
            }
        };
    }

    private void stopTracking() {
        viewModel.finishRoute();
        Intent serviceIntent = new Intent(getActivity(), RunTrackerService.class);
        serviceIntent.setAction(RunTrackerService.STOP_FOREGROUND);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
        removeObserver();
        listener.onFinishedButtonPressed();
    }

    private void removeObserver() {
        viewModel.getRoute().removeObserver(routeObserver);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (viewModel.isTracking()) {
            removeObserver();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
