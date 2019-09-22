package com.example.runtrackerfragment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runtrackerfragment.R;
import com.example.runtrackerfragment.adapters.RouteAdapter;
import com.example.runtrackerfragment.models.Route;
import com.example.runtrackerfragment.models.TrackerViewModel;

import java.util.ArrayList;
import java.util.List;

public class RouteListFragment extends Fragment {

    public static final String TAG = "RouteListFragmentLog";
    private RecyclerView recyclerView;
    private TrackerViewModel viewModel;
    private RouteAdapter routeAdapter;
    private RouteListFragmentListener listener;
    private Observer<List<Route>> observer;

    public interface RouteListFragmentListener {
        void onItemClicked();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_route_list, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) v.findViewById(R.id.toolbar));
        return v;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(TrackerViewModel.class);
        setRecyclerView(view);
        setRouteAdapter();
        createObserver();
        recyclerView.setAdapter(routeAdapter);
    }

    private void setRecyclerView(View v) {
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
    }

    private void setRouteAdapter() {
        routeAdapter = new RouteAdapter();
        routeAdapter.setOnItemClickListener(new RouteAdapter.onItemClickListener() {
            @Override
            public void onItemClicked(Route route) {
                viewModel.setRouteById(route.getRouteId());
                listener.onItemClicked();
            }
        });
    }

    private void createObserver() {
        observer = new Observer<List<Route>>() {
            @Override
            public void onChanged(List<Route> routes) {
                List<Route> adapterRoutes = new ArrayList<>(routes);
                if (viewModel.isTracking()) {
                    adapterRoutes.remove(adapterRoutes.size() - 1);
                }
                routeAdapter.setRoutes(adapterRoutes);
            }
        };
        viewModel.getAllRoutes().observe(getActivity(), observer);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.getAllRoutes().removeObserver(observer);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RouteListFragmentListener) {
            listener = (RouteListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RouteListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
