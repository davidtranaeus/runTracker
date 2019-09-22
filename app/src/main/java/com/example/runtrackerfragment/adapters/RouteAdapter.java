package com.example.runtrackerfragment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runtrackerfragment.R;
import com.example.runtrackerfragment.models.Route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteHolder> {

    private List<Route> routes = new ArrayList<>();
    private onItemClickListener listener;

    @NonNull
    @Override
    public RouteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Skapa View och returnera en ViewHolder
        View routeView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_item, parent, false);

        return new RouteHolder(routeView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteHolder holder, int position) {
        // Sätt utseende för ViewHolder på plats position
        // Det representerar även det objektet på plats position i this.routes
        Route route = routes.get(position);
        holder.distanceTextView.setText(route.getDistance() + " m");
        holder.durationTextView.setText(route.getDuration() + " s");
        holder.dateTextView.setText(getDate(route.getStartTime(), "dd-MM-yyyy\nhh:mm"));
    }

    private String getDate(long milliSeconds, String dateFormat) {

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public int getItemCount() {
        // Returnera hur många ViewHolders det ska finnas
        return routes.size();
    }

    public void setRoutes(List<Route> routes) {
        // Sätt data som adaptern ska arbeta med
        this.routes = routes;
        notifyDataSetChanged(); // this can be changed later, see part 6
    }

    class RouteHolder extends RecyclerView.ViewHolder {
        // Koppla widgets till ViewHoldern med den View som skickats in
        private TextView distanceTextView;
        private TextView durationTextView;
        private TextView dateTextView;

        public RouteHolder(@NonNull View itemView) {
            super(itemView);
            distanceTextView = itemView.findViewById(R.id.distance_text_view);
            durationTextView = itemView.findViewById(R.id.duration_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClicked(routes.get(position));
                    }

                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClicked(Route route);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
