package com.example.driver;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RCAdapter extends RecyclerView.Adapter<RCAdapter.RCViewHolder> {

    Context context;

    ArrayList<Route> routeArrayList;

    private final RCInterface rcInterface;

    public RCAdapter(Context context, ArrayList<Route> routeArrayList, RCInterface rcInterface) {
        this.context = context;
        this.routeArrayList = routeArrayList;
        this.rcInterface = rcInterface;
    }



    @NonNull
    @Override
    public RCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rc_item_driver, parent, false);

        return new RCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position) {
        Route route = routeArrayList.get(position);
        holder.from.setText(route.from);
        holder.to.setText(route.to);
        holder.time.setText(route.time);
        holder.date.setText(route.date);
        holder.price.setText(route.price);
        // Fetch the status from the database
        fetchStatusFromFirebase(route, holder);

        holder.btnFinishTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the status in Firebase Realtime Database
                updateStatusInFirebase(route);

            }
        });

    }
    private void fetchStatusFromFirebase(Route route, RCViewHolder holder) {
        DatabaseReference routesRef = FirebaseDatabase.getInstance().getReference("Routes");

        routesRef.child(route.routeId).child("status").get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String status = dataSnapshot.getValue(String.class);
                        // Check if the status needs to be updated
                        if (status != null && !status.equals(route.status)) {
                            route.status = status;
                            // Ensure the UI update happens on the main thread
                            holder.itemView.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.status.setText(route.status);

                                    // Update the visibility of the button
                                    if ("completed".equals(status)) {
                                        holder.btnFinishTrip.setVisibility(View.GONE);
                                    } else {
                                        holder.btnFinishTrip.setVisibility(View.VISIBLE);
                                    }

                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure, if needed
                    }
                });
    }



    private void updateStatusInFirebase(Route route) {
        DatabaseReference routesRef = FirebaseDatabase.getInstance().getReference("Routes");
        routesRef.child(route.routeId).child("status").setValue("completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle the success, if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure, if needed
                    }
                });
    }

    @Override
    public int getItemCount() {
        return routeArrayList.size();
    }

    public class RCViewHolder extends RecyclerView.ViewHolder{
        Button btnFinishTrip;

        TextView from, to, time,date, price, status;
        public RCViewHolder(@NonNull View itemView){
            super(itemView);
            from = itemView.findViewById(R.id.tvFrom);
            to = itemView.findViewById(R.id.tvTo);
            time = itemView.findViewById(R.id.tvTime);
            price = itemView.findViewById(R.id.tvPrice);
            date = itemView.findViewById(R.id.tvDate);
            status = itemView.findViewById(R.id.tvStatus);
            btnFinishTrip = itemView.findViewById(R.id.btnFinishTrip);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rcInterface != null){
                        int position = getAdapterPosition();
                        // if position is valid
                        if(position != RecyclerView.NO_POSITION){
                            rcInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
