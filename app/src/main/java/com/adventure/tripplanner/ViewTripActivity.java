package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ViewTripActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView destination,from,date,vehicle,charge,timing,seats,inclusion,description,pickupLocation;
    private ViewPager viewPager;
    private ArrayList<String> imageList=new ArrayList<>();
    private AppCompatButton btnBook,btnViewBookings;
    private ProgressBar progressBar;

    private String teamName="";
    private String tripName="";
    private int availableSeats;
    private int chargeAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);

        toolbar=findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trip Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        destination=findViewById(R.id.destination);
        description=findViewById(R.id.description);
        from=findViewById(R.id.from);
        date=findViewById(R.id.date);
        vehicle=findViewById(R.id.vehicle);
        charge=findViewById(R.id.charge);
        timing=findViewById(R.id.timing);
        seats=findViewById(R.id.seats);
        inclusion=findViewById(R.id.inclusion);
        viewPager=findViewById(R.id.viewPager);
        btnBook=findViewById(R.id.btnBookSeats);
        pickupLocation=findViewById(R.id.pickLocation);
        btnViewBookings=findViewById(R.id.btnBookRecords);
        progressBar=findViewById(R.id.progressBar);

        Intent get=getIntent();

        //set buttons visibility according to profiler.
        if(get!=null)
        {
            if(get.getStringExtra("status").equals("traveler"))
            {
                btnViewBookings.setVisibility(View.GONE);
                teamName=get.getStringExtra("teamName");
                tripName=get.getStringExtra("tripName");
            }
        else{
            teamName=get.getStringExtra("teamName");
            tripName=get.getStringExtra("tripName");
            btnBook.setVisibility(View.GONE);
            }
        }
        progressBar.setVisibility(View.VISIBLE);

        Log.d("xx",teamName+" "+tripName);

        //getting trip details from firebase.
        FirebaseConnectivity firebaseConnectivity=new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());
        firebaseConnectivity.getReference().child(teamName).child(tripName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot snapshot1 : snapshot.child("Images").getChildren())
                    {
                        String image=snapshot1.getValue().toString();
                        imageList.add(image);
                    }
                        NewTripModelClass modelClass= snapshot.child("TripData").getValue(NewTripModelClass.class);
                        destination.setText(modelClass.getDestination());
                        try{
                        description.setText(modelClass.getDescription()); }
                        catch (Exception e){ description.setText("Not available");}
                        from.setText(modelClass.getFrom());
                        inclusion.setText(modelClass.getInclusions());
                        date.setText(modelClass.getDateFrom()+" to "+modelClass.getDateTo());
                        timing.setText(modelClass.getTime());
                        vehicle.setText(modelClass.getVehicle());
                        seats.setText(modelClass.getSeats());
                        charge.setText(modelClass.getCharge());
                        pickupLocation.setText(modelClass.getPickupLocation());
                        availableSeats=Integer.parseInt(modelClass.getSeats());
                        chargeAmount=Integer.parseInt(modelClass.getCharge());


                    if(availableSeats == 0)
                    {
                        btnBook.setEnabled(false);
                        btnBook.setKeyListener(null);
                    }
                    setAdapter();
                    }
                else
                    Toast.makeText(ViewTripActivity.this, "data is not available", Toast.LENGTH_SHORT).show();
                }
                //get image to view pager from firebase
            private void setAdapter() {
                progressBar.setVisibility(View.GONE);
                ImageGetAdapter adapter=new ImageGetAdapter(ViewTripActivity.this,imageList);
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewTripActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //btn book seat by traveler.
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewTripActivity.this,BookSeatsActivity.class);
                intent.putExtra("tripName",tripName);
                intent.putExtra("teamName",teamName);
                intent.putExtra("seats",availableSeats);
                intent.putExtra("charge",chargeAmount);
                startActivity(intent);
            }
        });

        //btn view seat bookings by organizer
        btnViewBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewTripActivity.this,BookingsActivity.class);
                intent.putExtra("tripName",tripName);
                intent.putExtra("teamName",teamName);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
            super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}