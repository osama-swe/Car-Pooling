package com.example.carpooling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View view = layoutInflater.inflate(R.layout.rc_item, parent, false);

        return new RCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position) {
        Route route = routeArrayList.get(position);
        holder.from.setText(route.from);
        holder.to.setText(route.to);
        holder.time.setText(route.time);
        holder.price.setText(route.price);
        holder.date.setText(route.date);

    }

    @Override
    public int getItemCount() {
        return routeArrayList.size();
    }

    public class RCViewHolder extends RecyclerView.ViewHolder{
        TextView from, to, time, price, date;
        public RCViewHolder(@NonNull View itemView){
            super(itemView);
            from = itemView.findViewById(R.id.tvFrom);
            to = itemView.findViewById(R.id.tvTo);
            time = itemView.findViewById(R.id.tvTime);
            price = itemView.findViewById(R.id.tvPrice);
            date = itemView.findViewById(R.id.tvDate);

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
