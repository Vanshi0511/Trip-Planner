package com.adventure.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//adapter for getting trips for both traveler and organizer via recycler view
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CardModel> arrayList;
    private String status;
    public CardAdapter(Context context, ArrayList<CardModel> arrayList,String status)
    {
        this.context=context;
        this.arrayList=arrayList;
        this.status=status;
    }
    @NonNull
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.trip_cardview,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.ViewHolder holder, int position) {

        //getting data of trip which is used for getting trip info from DB in ViewTripActivity.
        CardModel model=arrayList.get(position);

        holder.destination.setText(arrayList.get(position).getDestination());
        holder.organization.setText(arrayList.get(position).getTeam());
        Picasso.get().load(arrayList.get(position).getImage()).into(holder.imageView);


        //click on image shows tripInfo.
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewTripActivity.class);
                intent.putExtra("teamName",model.getTeam());
                intent.putExtra("tripName",model.getDestination());

                //who clicked on RecyclerTrip Card.
                if(status.equals("organizer"))
                  intent.putExtra("status","organizer");
                else
                    intent.putExtra("status","traveler");
                context.startActivity(intent);
            }
        });

        //click on layout which shows profile of organizer.
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewUsersProfile.class);
                intent.putExtra("teamName",model.getTeam()); //OrganizerKeyMap for getting emailKey via team name.
                intent.putExtra("status","organizer");
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

        private TextView destination,organization;
        private ImageView imageView;
        private RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            destination=itemView.findViewById(R.id.cardTripName);
            organization=itemView.findViewById(R.id.cardOrganizerName);
            imageView=itemView.findViewById(R.id.cardTripImage);
            relativeLayout=itemView.findViewById(R.id.relativeProfile);
        }
    }
}
