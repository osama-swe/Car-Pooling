package com.example.driver;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AvailableRoutes extends AppCompatActivity implements RCInterface{

    RecyclerView recyclerView;
    ArrayList<Route> routeArrayList;
    RCAdapter rcAdapter;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_routes);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        
        routeArrayList = new ArrayList<>();
        rcAdapter = new RCAdapter(this, routeArrayList, this);
        recyclerView.setAdapter(rcAdapter);

        db = FirebaseDatabase.getInstance().getReference("Routes");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeArrayList.clear();
                for (DataSnapshot routeSnapshot: snapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);

                    if (route != null) {
                        routeArrayList.add(route);
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(AvailableRoutes.this, Address.class);
        startActivity(intent);
    }
}