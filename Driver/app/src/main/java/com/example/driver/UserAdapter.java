package com.example.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<String> userList;
    private final UserAdapterListener listener;
    private String routeId; // Assuming routeId is accessible in this adapter

    public UserAdapter(Context context, List<String> userList, UserAdapterListener listener, String routeId) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
        this.routeId = routeId;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userId = userList.get(position);

        // Populate user details
        holder.tvUserName.setText(userId);

        // Check the status in the database and update visibility accordingly
        checkUserStatus(userId, holder);

        // Set click listeners for approval and rejection buttons
        holder.btnApprove.setOnClickListener(view -> {
            listener.onApproveClick(position, holder);
        });
        holder.btnReject.setOnClickListener(view -> {
            listener.onRejectClick(position, holder);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        Button btnApprove;
        Button btnReject;
        TextView tvStatus;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    // Interface to handle button clicks
    public interface UserAdapterListener {
        void onApproveClick(int position, UserViewHolder holder);
        void onRejectClick(int position, UserViewHolder holder);
    }

    private void checkUserStatus(String userId, UserViewHolder holder) {
        DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("Routes")
                .child(routeId) // Assuming routeId is available; replace it with the actual route ID
                .child("user_ids")
                .child(userId);

        userStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userStatus = dataSnapshot.getValue(String.class);

                    switch (userStatus) {
                        case "pending":
                            // User is pending, show buttons, hide status
                            holder.btnApprove.setVisibility(View.VISIBLE);
                            holder.btnReject.setVisibility(View.VISIBLE);
                            holder.tvStatus.setVisibility(View.GONE);
                            break;
                        case "approved":
                        case "rejected":
                            // User is either approved or rejected, hide buttons, show status
                            holder.btnApprove.setVisibility(View.GONE);
                            holder.btnReject.setVisibility(View.GONE);
                            holder.tvStatus.setVisibility(View.VISIBLE);
                            holder.tvStatus.setText(userStatus.substring(0, 1).toUpperCase() + userStatus.substring(1));
                            break;
                        default:
                            // Default case, handle as needed
                            break;
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
