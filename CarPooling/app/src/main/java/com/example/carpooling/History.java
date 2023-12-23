package com.example.carpooling;

import android.content.Intent;
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

public class History extends AppCompatActivity implements RCInterface {

    RecyclerView recyclerView;
    ArrayList<Route> routeArrayList;

    RCAdapterCart rcAdapter;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        routeArrayList = new ArrayList<>();
        rcAdapter = new RCAdapterCart(routeArrayList, this);
        recyclerView.setAdapter(rcAdapter);

        // Get the current signed-in user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query all routes with status "completed" for the current user
        Query completed = FirebaseDatabase.getInstance().getReference("Routes")
                .orderByChild("user_ids/" + userId).equalTo("approved");

        completed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeArrayList.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    String routeId = routeSnapshot.getKey(); // get the routeId
                    DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Routes")
                            .child(routeId).child("status");

                    // Get the status directly from the status node
                    statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String status = dataSnapshot.getValue(String.class);
                            if ("completed".equals(status)) {
                                Route route = routeSnapshot.getValue(Route.class);
                                if (route != null) {
                                    routeArrayList.add(route);
                                    rcAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors, if any
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors, if any
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(History.this, Payment.class);
        intent.putExtra("routeId", routeArrayList.get(position).getRouteId());
        intent.putExtra("price", routeArrayList.get(position).getPrice());
        startActivity(intent);
    }
}
