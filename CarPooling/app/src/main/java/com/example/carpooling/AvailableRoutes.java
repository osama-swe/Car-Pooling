package com.example.carpooling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class AvailableRoutes extends AppCompatActivity implements RCInterface {
    RecyclerView recyclerView;
    ArrayList<Route> routeArrayList;
    RCAdapter rcAdapter;
    DatabaseReference db;
    Switch switchShowAllRoutes;

    private TimeTickReceiver timeTickReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_routes);
        switchShowAllRoutes = findViewById(R.id.switchShowAllRoutes);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        routeArrayList = new ArrayList<>();
        rcAdapter = new RCAdapter(this, routeArrayList, this);
        recyclerView.setAdapter(rcAdapter);

        // Check the state of the switch when the activity is created
        if (switchShowAllRoutes.isChecked()) {
            // Show all routes
            showAllRoutes();
        } else {
            // Show only validated routes
            showValidatedRoutes();
        }

        // Set a listener for switch changes
        switchShowAllRoutes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all routes when the switch is checked
                showAllRoutes();
            } else {
                // Show only validated routes when the switch is not checked
                showValidatedRoutes();
            }
        });


        // Initialize and register the TimeTickReceiver
        timeTickReceiver = new TimeTickReceiver(routeArrayList, rcAdapter, switchShowAllRoutes);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeTickReceiver, filter);
    }

    private void showAllRoutes() {
        db = FirebaseDatabase.getInstance().getReference("Routes");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeArrayList.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);

                    if (route != null) {
                        routeArrayList.add(route);
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void showValidatedRoutes() {
        db = FirebaseDatabase.getInstance().getReference("Routes");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeArrayList.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);

                    if (route != null && RouteUtils.isRouteVisible(route)) {
                        routeArrayList.add(route);
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(AvailableRoutes.this, Review.class);
        Route route = routeArrayList.get(position);
        intent.putExtra("route_id", route.routeId);
        intent.putExtra("to", route.to);
        intent.putExtra("from", route.from);
        intent.putExtra("date", route.date);
        intent.putExtra("time", route.time);
        intent.putExtra("price", route.price);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(timeTickReceiver);
    }
}

class TimeTickReceiver extends BroadcastReceiver {
    ArrayList<Route> routeArrayList;
    RCAdapter rcAdapter;
    Switch switchShowAllRoutes;

    public TimeTickReceiver(ArrayList<Route> routeArrayList, RCAdapter rcAdapter, Switch switchShowAllRoutes) {
        this.routeArrayList = routeArrayList;
        this.rcAdapter = rcAdapter;
        this.switchShowAllRoutes = switchShowAllRoutes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimeTickReceiver", "onReceive: A minute has passed");

        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            // Check the state of the switch
            if (!switchShowAllRoutes.isChecked()) {
                // If switch is not checked (showing validated routes), perform route visibility check
                Iterator<Route> iterator = routeArrayList.iterator();

                while (iterator.hasNext()) {
                    Route route = iterator.next();
                    if (!RouteUtils.isRouteVisible(route)) {
                        iterator.remove();
                    }
                }
                // Notify the adapter that the data set has changed
                rcAdapter.notifyDataSetChanged();
            }
            // If switch is checked (showing all routes), do nothing
        }
    }
}

class RouteUtils {
    public static boolean isRouteVisible(Route route) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.US);

        try {
            Date routeDateTime = sdf.parse(route.getDate() + " " + route.getTime());
            Calendar limit = Calendar.getInstance();
            limit.setTime(routeDateTime);
            // Set the cutoff time based on the route time
            if (route.getTime().equals("7:30 AM")) {
                // Adjust the calendar to be one day before with the hour set to 10 PM
                limit.add(Calendar.DAY_OF_MONTH, -1);
                limit.set(Calendar.HOUR_OF_DAY, 22);
                limit.set(Calendar.MINUTE, 00);
            } else if (route.getTime().equals("5:30 PM")) {
                // Calculate the cutoff time as 1:00 PM on the same day
                limit.set(Calendar.HOUR_OF_DAY, 13);
                limit.set(Calendar.MINUTE, 0);
            }

            // Check if the current time is before both the cutoff time
            return Calendar.getInstance().before(limit);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
