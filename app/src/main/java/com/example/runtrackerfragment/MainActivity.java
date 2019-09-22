package com.example.runtrackerfragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.runtrackerfragment.fragments.LandingFragment;
import com.example.runtrackerfragment.fragments.RouteDetailsFragment;
import com.example.runtrackerfragment.fragments.RouteListFragment;
import com.example.runtrackerfragment.fragments.TrackerFragment;
import com.example.runtrackerfragment.models.TrackerViewModel;
import com.example.runtrackerfragment.services.RunTrackerService;
import com.google.android.material.appbar.AppBarLayout;

import static com.example.runtrackerfragment.services.RunTrackerService.ACTIVE_ROUTE_ID;

public class MainActivity extends AppCompatActivity implements
        LandingFragment.LandingFragmentListener,
        TrackerFragment.TrackerFragmentListener,
        RouteListFragment.RouteListFragmentListener {

    public static final String TAG = "MainActivityLog";
    private final String SAVED_FRAGMENT = "fragment_name";
    private Fragment landingFragment;
    private TrackerFragment trackerFragment;
    private RouteDetailsFragment routeDetailsFragment;
    private RouteListFragment routeListFragment;
    private TrackerViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        landingFragment = new LandingFragment();
        trackerFragment = new TrackerFragment();
        routeDetailsFragment = new RouteDetailsFragment();
        routeListFragment = new RouteListFragment();

        viewModel = ViewModelProviders.of(this).get(TrackerViewModel.class);

        Fragment frag;

        if (savedInstanceState != null) {
            frag = getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAGMENT);
        } else {
            frag = landingFragment;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, frag)
                .commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.d("RQ", ""+frag);
        getSupportFragmentManager().putFragment(outState, SAVED_FRAGMENT, frag);
    }

    @Override
    public void onTrackButtonPressed() {

        trackerFragment = new TrackerFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, trackerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFinishedButtonPressed() {

        routeDetailsFragment = new RouteDetailsFragment();

        getSupportFragmentManager().popBackStack();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, routeDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onTrackListButtonPressed() {

        routeListFragment = new RouteListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, routeListFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onItemClicked() {
        routeDetailsFragment = new RouteDetailsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, routeDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDestroy() {

        if (viewModel.isTracking()) {
            Intent serviceIntent = new Intent(this, RunTrackerService.class);
            serviceIntent.setAction(RunTrackerService.STOP_FOREGROUND);
            ContextCompat.startForegroundService(this, serviceIntent);
        }

        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
