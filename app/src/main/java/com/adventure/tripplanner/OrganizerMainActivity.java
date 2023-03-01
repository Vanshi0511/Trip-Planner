package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrganizerMainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fac;
    private TextView name,mob,email,team;
    private View headerView;
    private ShapeableImageView imageView;
    private OrganizerProfileModel profile;
    private RecyclerView recyclerView;
    private ProgressBar progressBar,headerProgressBar;

    private ArrayList<CardModel> arrayCardModel =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);

        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Trips");


        drawerLayout = findViewById(R.id.drawerID);
        navigationView = findViewById(R.id.navigation);
        fac = findViewById(R.id.facBtn);
        progressBar = findViewById(R.id.progressBar);


        //for accessing items of navigation header we need to inflate a header to another view.
        headerView = navigationView.getHeaderView(0);

        name = headerView.findViewById(R.id.header_nameID);
        mob = headerView.findViewById(R.id.header_mobID);
        email = headerView.findViewById(R.id.header_emailID);
        team = headerView.findViewById(R.id.header_teamID);
        imageView = headerView.findViewById(R.id.imageProfile);
        headerProgressBar=headerView.findViewById(R.id.progressBar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fac.setColorFilter(Color.WHITE);
        progressBar.setVisibility(View.VISIBLE);
        headerProgressBar.setVisibility(View.VISIBLE);

        //if internet is not available
        if (!Raw.connectedToInternet(getApplicationContext())) {
            View view = getLayoutInflater().inflate(R.layout.custom_toast_internet, (ViewGroup) findViewById(R.id.customToastInternet));
            new Raw().internetToast(view, OrganizerMainActivity.this);
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            final String emailKey = user.getEmail().replace(".", "^");

            //getting organizer profile data to navigation header.
            FirebaseConnectivity firebaseConnectivity = new FirebaseConnectivity(FirebaseConnectivity.getOrganizerDatabase());
            firebaseConnectivity.getReference().child(emailKey).child("Other_Data").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        progressBar.setVisibility(View.GONE);
                        fac.setVisibility(View.VISIBLE);
                        profile = snapshot.getValue(OrganizerProfileModel.class);

                        name.setText(profile.getName());
                        mob.setText("+91 " + profile.getMobileNo());
                        email.setText(profile.getEmail());
                        team.setText(profile.getTeamName());

                        firebaseConnectivity.getReference().child(emailKey).child("Image").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    String image = snapshot.getValue(String.class);
                                    Picasso.get().load(image).into(imageView);
                                }
                                headerProgressBar.setVisibility(View.GONE);
                                getData();

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(OrganizerMainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(OrganizerMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            //new trip
            fac.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrganizerMainActivity.this, NewTripActivity.class);
                    intent.putExtra("TeamName", profile.getTeamName());
                    startActivity(intent);
                }

            });

            //click on navigation menu
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    int itemId = item.getItemId();

                    if (itemId == R.id.menu_profile)
                    {
                        Intent intent = new Intent(OrganizerMainActivity.this, OrganizerProfileActivity.class);
                        intent.putExtra("callFrom", "main");
                        intent.putExtra("emailKey", emailKey);
                        startActivity(intent);

                    } else if (itemId == R.id.menu_about)
                    {
                        Intent intent=new Intent(OrganizerMainActivity.this,AboutActivity.class);
                        startActivity(intent);

                    } else if (itemId == R.id.menu_help)
                    {
                        Intent intent = new Intent(OrganizerMainActivity.this, HelpActivity.class);
                        startActivity(intent);

                    } else if (itemId == R.id.menu_logOut)
                    {
                        Raw.showLogOutDialog(OrganizerMainActivity.this);
                    } else
                    {
                        Raw.showDeactivateDialog(OrganizerMainActivity.this, "Organizer", profile.getTeamName());
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
          super.onBackPressed();
    }

    //getting data from DB and using recycler view displaying current organizer trips.
    private void getData()
    {
        recyclerView=findViewById(R.id.recyclerCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());
        connectivity.getReference().child(profile.getTeamName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String image="",destination="",tripDate="";
                    for (DataSnapshot teamSnapshot : snapshot.getChildren()) {
                        try{
                            image = teamSnapshot.child("Images").child("image0").getValue().toString();
                            destination = teamSnapshot.child("TripData").child("destination").getValue().toString();
                            tripDate=teamSnapshot.child("TripData").child("dateFrom").getValue().toString();

                            CardModel cardModel = new CardModel(image, destination+" "+tripDate, profile.getTeamName());
                            arrayCardModel.add(cardModel);
                        }
                        catch (NullPointerException npe){}
                    }
                    //set adapter
                    progressBar.setVisibility(View.GONE);
                    CardAdapter cardAdapter=new CardAdapter(OrganizerMainActivity.this,arrayCardModel,"organizer");
                    recyclerView.setAdapter(cardAdapter);
                }
                else { progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrganizerMainActivity.this, "No trip found", Toast.LENGTH_SHORT).show(); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrganizerMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}


