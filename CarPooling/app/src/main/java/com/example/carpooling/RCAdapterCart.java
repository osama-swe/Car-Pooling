package com.example.carpooling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RCAdapterCart extends RecyclerView.Adapter<RCAdapterCart.ViewHolder> {

    private ArrayList<Route> routeList;
    private DatabaseReference databaseReference;
    private String userId;
    private RCInterface itemClickListener;

    public RCAdapterCart(ArrayList<Route> routeList, RCInterface itemClickListener) {
        this.routeList = routeList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Routes");
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routeList.get(position);

        // Populate your views with route data
        holder.tvFrom.setText(route.getFrom());
        holder.tvTo.setText(route.getTo());
        holder.tvTime.setText(route.getTime());
        holder.tvDate.setText(route.getDate());
        holder.tvPrice.setText(route.getPrice());

        // Retrieve and display the status from Firebase
        retrieveStatusFromFirebase(route.getRouteId(), holder.tvStatus);
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFrom, tvTo, tvTime, tvDate, tvPrice, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    private void retrieveStatusFromFirebase(String routeId, TextView tvStatus) {
        // Retrieve the status from Firebase for the specified route and user
        databaseReference.child(routeId).child("user_ids").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.getValue(String.class);
                    tvStatus.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if any
            }
        });
    }
}
