package com.example.runtrackerfragment.models;

import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.runtrackerfragment.models.Route;
import com.example.runtrackerfragment.models.RouteDao;
import com.example.runtrackerfragment.models.RouteDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RouteRepository {

    // Repo is an abstract layer to handle database and network resources, an API for the ViewModel
    private RouteDao routeDao;
    private LiveData<List<Route>> allRoutes;
    private LiveData<Route> route;
    public static final String TAG = "RouteRepositoryLog";

    // Application is the (subclass of) context passed from the ViewModel
    public RouteRepository(Application application) {
        RouteDatabase database = RouteDatabase.getInstance(application);
        routeDao = database.routeDao();
        allRoutes = routeDao.getAllRoutes(); // Room generates the code for this method
    }

    public long insert(Route route) {
        try {
            return new InsertRouteAsyncTask(routeDao).execute(route).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteRoute(Route route) {
        new deleteRouteAsyncTask(routeDao).execute(route);
    }

    public void updateRoute(Route route) {
        new updateRouteAsyncTask(routeDao).execute(route);
    }

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }

    public LiveData<Route> getRouteById(int id) {
        route = routeDao.getRouteById(id);
        return route;
    }

    private static class InsertRouteAsyncTask extends AsyncTask<Route, Void, Long> {
        // The query operations needs to be handled async, so we pass the dao to the async task
        private RouteDao routeDao;

        private InsertRouteAsyncTask(RouteDao routeDao) {
            this.routeDao = routeDao;
        }

        @Override
        protected Long doInBackground(Route... routes) {
            return routeDao.insert(routes[0]); // get first item in argument list
            // return null;
        }
    }

    private static class deleteRouteAsyncTask extends AsyncTask<Route, Void, Void> {
        private RouteDao routeDao;

        private deleteRouteAsyncTask(RouteDao routeDao) {
            this.routeDao = routeDao;
        }

        @Override
        protected Void doInBackground(Route... routes) {
            routeDao.delete(routes[0]);
            return null;
        }
    }

    private static class updateRouteAsyncTask extends AsyncTask<Route, Void, Void> {
        private RouteDao routeDao;

        private updateRouteAsyncTask(RouteDao routeDao) {
            this.routeDao = routeDao;
        }

        @Override
        protected Void doInBackground(Route... routes) {
            routeDao.update(routes[0]);
            return null;
        }
    }

}
