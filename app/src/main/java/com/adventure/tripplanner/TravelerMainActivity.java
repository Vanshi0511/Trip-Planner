package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TravelerMainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ArrayList<CardModel> arrayList=new ArrayList<>();
    private RecyclerView recyclerView;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private String emailKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_main);


        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trips");

        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setVisibility(View.VISIBLE);


        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        emailKey=user.getEmail();


        //internet is not available
        if (!Raw.connectedToInternet(getApplicationContext())) {
            View view = getLayoutInflater().inflate(R.layout.custom_toast_internet, (ViewGroup) findViewById(R.id.customToastInternet));
            new Raw().internetToast(view, TravelerMainActivity.this);
        }


        if(!arrayList.isEmpty())
            arrayList.clear();

        //getting trips data from DB and showing in recycler views.
        FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());
        connectivity.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        String teamName=snapshot1.getKey();
                        for(DataSnapshot snapshot2 : snapshot1.getChildren())
                        {
                            try{
                            String tripName=snapshot2.getKey();
                            String image=snapshot2.child("Images").child("image0").getValue().toString();
                            CardModel cardModel = new CardModel(image, tripName,teamName);
                            arrayList.add(cardModel);}
                            catch (NullPointerException npe){}
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    //setAdapter
                    CardAdapter cardAdapter=new CardAdapter(TravelerMainActivity.this,arrayList,"traveler");
                    recyclerView.setAdapter(cardAdapter);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TravelerMainActivity.this, "No trip found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TravelerMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //inflate menu on toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.traveler_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //click on menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_profile) {
            Intent intent = new Intent(TravelerMainActivity.this, TravelerProfileActivity.class);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        } else if (itemId == R.id.menu_about) {
            Intent intent=new Intent(TravelerMainActivity.this,AboutActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.menu_help) {
            Intent intent = new Intent(TravelerMainActivity.this, HelpActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.menu_logOut) {
            Raw.showLogOutDialog(TravelerMainActivity.this);
        }else if(itemId== R.id.menu_myBookings){
            Intent intent=new Intent(TravelerMainActivity.this,MyBookings.class);
            startActivity(intent);
        }
        else {
            Raw.showDeactivateDialog(TravelerMainActivity.this, "traveler", null);
        }
        return super.onOptionsItemSelected(item);
    }
}