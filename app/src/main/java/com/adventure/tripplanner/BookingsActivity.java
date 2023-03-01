package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String tripName;
    private String teamName;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<BookingModel> arrayList=new ArrayList<>();
    private ArrayList<String> arrayKey=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        progressBar=findViewById(R.id.progressBar);
        toolbar=findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set recycler view layout
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get team and trip name for fetching current trip bookings.
        Intent get=getIntent();
        if(get!=null)
        {
            tripName=get.getStringExtra("tripName");
            teamName=get.getStringExtra("teamName");
        }

        progressBar.setVisibility(View.VISIBLE);
        //get the data of bookings of current trip.
        FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());
        connectivity.getReference().child(teamName).child(tripName).child("Bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        arrayKey.add(snapshot1.getKey()); //getting emailKey corresponding to its booking.
                        BookingModel model=snapshot1.getValue(BookingModel.class);
                        arrayList.add(model);
                    }
                    progressBar.setVisibility(View.GONE);
                    //set adapter to recycler view which display list of bookings of current trip.
                    BookingsAdapter adapter=new BookingsAdapter(arrayList,BookingsActivity.this,arrayKey);
                    recyclerView.setAdapter(adapter);
                }
                //if bookings not found this else will execute.
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(BookingsActivity.this, "No bookings found regarding your trip.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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