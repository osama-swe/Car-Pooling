package com.example.driver;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RouteUsersActivity extends AppCompatActivity implements UserAdapter.UserAdapterListener {

    private String routeId;
    private DatabaseReference usersRef;

    UserAdapter userAdapter;
    private List<String> userIdsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_users);

        // Get route ID from Intent or other sources
        routeId = getIntent().getStringExtra("routeId");

        // Set up RecyclerView for users
        RecyclerView recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        userIdsList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userIdsList, this, routeId);
        recyclerViewUsers.setAdapter(userAdapter);

        // Initialize Firebase references
        DatabaseReference routeRef = FirebaseDatabase.getInstance().getReference("Routes").child(routeId);
        usersRef = routeRef.child("user_ids");

        // Retrieve the list of user IDs
        retrieveUserIds();
    }

    private void retrieveUserIds() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userIdsList.clear(); // Clear the list before adding new user IDs
                    // dataSnapshot.getChildren() returns an Iterable<DataSnapshot>
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        userIdsList.add(userId);
                    }

                    // Notify the adapter about changes in the data
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors, if any
            }
        });
    }

    @Override
    public void onApproveClick(int position, UserAdapter.UserViewHolder holder) {
        String userId = userIdsList.get(position);
        usersRef.child(userId).setValue("approved");

        // Update UI if needed
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.tvStatus.setVisibility(View.VISIBLE);
        holder.tvStatus.setText("Approved");
    }

    @Override
    public void onRejectClick(int position, UserAdapter.UserViewHolder holder) {
        String userId = userIdsList.get(position);
        usersRef.child(userId).setValue("rejected");

        // Update UI if needed
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.tvStatus.setVisibility(View.VISIBLE);
        holder.tvStatus.setText("Rejected");
    }
}
