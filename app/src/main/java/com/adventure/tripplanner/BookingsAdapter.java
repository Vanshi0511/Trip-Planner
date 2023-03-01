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
//adapter for setting recycler of travellers booking on current trip.
public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private ArrayList<BookingModel> arrayList;
    private Context context;
    private ArrayList<String> arrayKey;

    public BookingsAdapter(ArrayList<BookingModel> arrayList, Context context,ArrayList<String> arrayKey) {
        this.arrayList = arrayList;
        this.context = context;
        this.arrayKey=arrayKey;
    }

    @NonNull
    @Override
    public BookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_bookings,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.date.setText("Booking Date : "+arrayList.get(position).getBookingDate());
        holder.amount.setText("Amount : "+arrayList.get(position).getAmount()+"/-");
        holder.seats.setText("Seats Reserved : "+arrayList.get(position).getSeatsBooked());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewUsersProfile.class);
                intent.putExtra("status","traveler");
                intent.putExtra("emailKey",arrayKey.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name,date,seats,amount;
        private RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            date=itemView.findViewById(R.id.date);
            seats=itemView.findViewById(R.id.seats);
            amount=itemView.findViewById(R.id.amount);
            relativeLayout=itemView.findViewById(R.id.bookingsLayout);
        }
    }
}
