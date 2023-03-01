package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBookings extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<MyBookingModel> arrayList = new ArrayList<>();
    private String teamName ;
    private String tripName ;
    private ArrayList<String> teamArray=new ArrayList<>();
    private ArrayList<String> tripArray=new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Bookings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //for getting bookings of traveler we need a emailKey.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String emailKey = user.getEmail().replace(".", "^");

        progressBar.setVisibility(View.VISIBLE);
        //getting bookings of user.
        FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());
        connectivity.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        teamName= snapshot1.getKey();
                        for(DataSnapshot snapshot2 : snapshot1.getChildren())
                        {
                                    tripName=snapshot2.getKey();
                                    String bookingDate="";
                                    String amount="";
                                    String seats="";
                                    try {
                                        bookingDate = snapshot2.child("Bookings").child(emailKey).child("bookingDate").getValue().toString();
                                        amount = snapshot2.child("Bookings").child(emailKey).child("amount").getValue().toString();
                                        seats = snapshot2.child("Bookings").child(emailKey).child("seatsBooked").getValue().toString();

                                        //need a array of different teams and trips for different bookings.
                                        teamArray.add(teamName);
                                        tripArray.add(tripName);
                                        MyBookingModel model=new MyBookingModel(teamName,tripName,seats,bookingDate,amount);
                                        arrayList.add(model);
                                      }
                                    catch (NullPointerException npe){
                                        Toast.makeText(MyBookings.this, "No booking found npe", Toast.LENGTH_SHORT).show();}

                            }

                        }
                }
                    progressBar.setVisibility(View.GONE);
                   //setting adapter for trip info
                    MyBookingsAdapter adapter=new MyBookingsAdapter(MyBookings.this,arrayList,teamArray,tripArray);
                    recyclerView.setAdapter(adapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyBookings.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

