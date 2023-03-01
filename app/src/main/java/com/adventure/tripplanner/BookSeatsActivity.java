package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BookSeatsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView availableSeat, amount;
    private EditText seats,name;
    private AppCompatButton proceed;
    private ProgressBar progressBar;

    private int availSeats;
    private int charge;
    private String teamName;
    private String tripName;
    private String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_seats);

        toolbar=findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Seat Book");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        availableSeat=findViewById(R.id.availableSeats);
        amount=findViewById(R.id.amount);
        seats=findViewById(R.id.seats);
        proceed=findViewById(R.id.btnProceed);
        name=findViewById(R.id.name);
        progressBar=findViewById(R.id.progressBar);


        //getting available or total seats from trip info
        Intent get=getIntent();
        if(get!=null)
        {
            //getting available seats with its charge for calculating payable amount.
            availSeats=get.getIntExtra("seats",0);
            charge=get.getIntExtra("charge",0);

            //display no.of available seats.
            availableSeat.setText("Available seats : "+availSeats);

            //getting trip and team name for inserting the travelers booking into DB.
            tripName=get.getStringExtra("tripName");
            teamName=get.getStringExtra("teamName");
        }


        //input the no of seats to booking get simultaneously display total amount.
        seats.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(!seats.getText().toString().equals(""))
                {
                    int getSeats = Integer.parseInt(seats.getText().toString());
                    totalAmount=String.valueOf(charge*getSeats);
                    amount.setText("Total amount : "+totalAmount+"/-");
                }
                else
                    amount.setText("Total amount : 0/-");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //proceed to book a seat.
        proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String getSeat=seats.getText().toString();
                    String holderName=name.getText().toString();
                    if(TextUtils.isEmpty(getSeat))
                    {
                        seats.setError("Enter seats");
                        seats.requestFocus();
                    }
                    else if(TextUtils.isEmpty(holderName))
                    {
                        name.setError("Enter name");
                        name.requestFocus();
                    }
                    else if(Integer.parseInt(getSeat) > availSeats) //if user i/p seats greater than available seat.
                    {
                        Toast.makeText(BookSeatsActivity.this, Integer.parseInt(getSeat)+" Seats are not available", Toast.LENGTH_SHORT).show();
                        seats.setError("Unavailable seats");
                        seats.requestFocus();
                    }
                    else
                    {
                        progressBar.setVisibility(View.VISIBLE);

                        //getting current user emailKey for booking.
                        FirebaseAuth auth=FirebaseAuth.getInstance();
                        FirebaseUser user=auth.getCurrentUser();
                        final String emailKey=user.getEmail().replace(".","^");


                        //working with DB.
                        FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());

                        //getting the available seats to be updated after traveler book a seat.
                        DatabaseReference reference=connectivity.getReference().child(teamName).child(tripName).child("TripData");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    String seat="";
                                    try{
                                     seat=snapshot.child("seats").getValue().toString();}
                                    catch (NullPointerException npe){}

                                    //updating seats
                                    reference.child("seats").setValue(String.valueOf(Integer.parseInt(seat)-Integer.parseInt(getSeat))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                //inserting the booking details to DB.
                                                BookingModel model=new BookingModel(holderName,totalAmount,getSeat,new SimpleDateFormat("dd/MM/yy").format(new Date()));
                                                connectivity.getReference().child(teamName).child(tripName).child("Bookings").child(emailKey).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            progressBar.setVisibility(View.GONE);

                                                            //go back to main activity after booking success.
                                                            Toast.makeText(BookSeatsActivity.this, "Your seats are confirmed. Thankyou!", Toast.LENGTH_SHORT).show();
                                                            Intent intent=new Intent(BookSeatsActivity.this,TravelerMainActivity.class);
                                                            startActivity(intent);
                                                            finishAffinity();
                                                        }
                                                        else{ progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(BookSeatsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();}
                                                    }

                                                });
                                            }
                                            else
                                            {
                                                try {
                                                     throw task.getException(); }
                                                catch (Exception e){
                                                    Toast.makeText(BookSeatsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();}
                                                }

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(BookSeatsActivity.this, "Seats are unavailable", Toast.LENGTH_SHORT).show();
                                   progressBar.setVisibility(View.GONE);}
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(BookSeatsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid=item.getItemId();
        if(itemid==android.R.id.home)
            super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}