package com.example.carpooling;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Trips extends AppCompatActivity implements RCInterface {

    RecyclerView recyclerView;
    ArrayList<Route> routeArrayList;

    RCAdapterCart rcAdapter;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        routeArrayList = new ArrayList<>();
        rcAdapter = new RCAdapterCart(routeArrayList, this);
        recyclerView.setAdapter(rcAdapter);

        // Get the current signed-in user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query all routes for the current user
        Query pending = FirebaseDatabase.getInstance().getReference("Routes")
                .orderByChild("user_ids/" + userId).equalTo("pending");

        Query approved = FirebaseDatabase.getInstance().getReference("Routes")
                .orderByChild("user_ids/" + userId).equalTo("approved");
        Query cancelled = FirebaseDatabase.getInstance().getReference("Routes")
                .orderByChild("user_ids/" + userId).equalTo("rejected");

        pending.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //routeArrayList.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    if (!routeArrayList.contains(route)) {
                        routeArrayList.add(route);
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors, if any
            }
        });

        approved.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //routeArrayList.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    if (!routeArrayList.contains(route)) {
                        routeArrayList.add(route);
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors, if any
            }
        });

        cancelled.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //routeArrayList.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    if (!routeArrayList.contains(route)) {
                        routeArrayList.add(route);
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors, if any
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // Handle item click if needed
//        Intent intent = new Intent(Trips.this, TripDetails.class);
//        startActivity(intent);
    }
}
