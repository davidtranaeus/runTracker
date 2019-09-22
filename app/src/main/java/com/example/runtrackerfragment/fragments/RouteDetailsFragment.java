package com.example.runtrackerfragment.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.runtrackerfragment.R;
import com.example.runtrackerfragment.models.Route;
import com.example.runtrackerfragment.models.TrackerViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteDetailsFragment extends Fragment implements OnMapReadyCallback{


    private final static String TAG = "RouteDetailsLog";
    private GoogleMap mMap;
    private TextView distanceTextView;
    private TextView durationTextView;
    private Button deleteButton;
    private TrackerViewModel viewModel;
    private Route mRoute;
    private Observer<Route> routeObserver;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_route_details, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) v.findViewById(R.id.toolbar));
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.route_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                viewModel.deleteRoute(mRoute);
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(TrackerViewModel.class);
        distanceTextView = view.findViewById(R.id.distance_text_view);
        durationTextView = view.findViewById(R.id.duration_text_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        createObserver();
        viewModel.getRoute().observe(getActivity(), routeObserver);
    }

    private void createObserver() {
        routeObserver = new Observer<Route>() {
            @Override
            public void onChanged(Route route) {

                mRoute = route;

                distanceTextView.setText(getString(R.string.distance, ""+ route.getDistance()));
                durationTextView.setText(getString(R.string.duration, ""+ route.getDuration() / 1000));

                mMap.addPolyline(new PolylineOptions()
                        .addAll(route.getLocations())
                        .width(5)
                        .color(Color.RED));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng location : route.getLocations()) {
                    builder.include(location);
                }

                LatLngBounds bounds = builder.build();

                int padding = 200; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                mMap.moveCamera(cu);
            }
        };
    }

    @Override
    public void onStop() {
        viewModel.getRoute().removeObserver(routeObserver);
        super.onStop();
    }
}
