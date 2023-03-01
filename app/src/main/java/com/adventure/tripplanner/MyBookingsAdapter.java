package com.adventure.tripplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//adapter for recycler view to get users bookings.
public class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MyBookingModel> arrayList;
    private ArrayList<String> tripName;
    private ArrayList<String> teamName;
    public MyBookingsAdapter(Context context,ArrayList<MyBookingModel> arrayList,ArrayList<String> teamName,ArrayList<String> tripName)
    {
        this.context=context;
        this.arrayList=arrayList;
        this.tripName=tripName;
        this.teamName=teamName;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_mybookings,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.organization.setText(arrayList.get(position).getTeamName());
        holder.trip.setText(arrayList.get(position).getTripName());
        holder.amount.setText("Amount: "+arrayList.get(position).getAmount()+"/-");
        holder.seats.setText("Seats: "+arrayList.get(position).getSeatBooked());
        holder.date.setText("Date: "+arrayList.get(position).getBookingDate());

        //click on users booking and view its info.
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewTripActivity.class);
                intent.putExtra("tripName",tripName.get(position));
                intent.putExtra("teamName",teamName.get(position));
                intent.putExtra("status","traveler");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView organization,trip,amount,date,seats;
        private RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            organization=itemView.findViewById(R.id.organization);
            trip=itemView.findViewById(R.id.teamName);
            amount=itemView.findViewById(R.id.amount);
            date=itemView.findViewById(R.id.date);
            seats=itemView.findViewById(R.id.seats);
            relativeLayout=itemView.findViewById(R.id.bookingLayout);
        }
    }
}
