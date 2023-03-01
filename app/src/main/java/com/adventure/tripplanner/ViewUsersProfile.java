package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewUsersProfile extends AppCompatActivity {

    private ProgressBar progressBar;
    private ShapeableImageView imageProfile;
    private TextView name,address,email,mob,description,organization;
    private AppCompatButton btnCall;
    private Toolbar toolbar;
    private String mobNo;
    private String emailKey="";
    private String teamName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String status="";

        //this activity set both traveler and organizer content view.
        Intent get=getIntent();
        if(get!=null)
        {   status=get.getStringExtra("status");
            if(status.equals("organizer"))
                teamName=get.getStringExtra("teamName");
            else
                emailKey=get.getStringExtra("emailKey");
        }


        if(status.equals("traveler"))
        {
            setContentView(R.layout.activity_view_traveler);

            progressBar = findViewById(R.id.progressBar);
            imageProfile = findViewById(R.id.imageProfile);
            name = findViewById(R.id.name);
            address = findViewById(R.id.address);
            email = findViewById(R.id.email);
            mob = findViewById(R.id.mob);
            btnCall = findViewById(R.id.btnCall);
            toolbar = findViewById(R.id.toolbarID);


            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            progressBar.setVisibility(View.VISIBLE);

            FirebaseConnectivity firebaseConnectivity = new FirebaseConnectivity(FirebaseConnectivity.getTravelerDatabase());
            firebaseConnectivity.getReference().child(emailKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String getImage;
                        try {
                            getImage = snapshot.child("Image").getValue().toString();
                        } catch (Exception e) {
                            getImage = null;
                        }
                        TravelerProfileModel model = snapshot.child("Other_Data").getValue(TravelerProfileModel.class);

                        name.setText(model.getName());
                        mob.setText("+91 " + model.getMobile());
                        address.setText(model.getAddress());
                        email.setText(emailKey.replace("^", "."));
                        mobNo = model.getMobile();
                        btnCall.setVisibility(View.VISIBLE);

                        if (getImage != null) {
                            Picasso.get().load(getImage).into(imageProfile);
                        }
                    } else
                        Toast.makeText(ViewUsersProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewUsersProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            progressBar.setVisibility(View.GONE);

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   call();
                }
            });
        }
        else
        {
            setContentView(R.layout.activity_view_organizer);
            progressBar = findViewById(R.id.progressBar);
            imageProfile = findViewById(R.id.imageProfile);
            name = findViewById(R.id.name);
            address = findViewById(R.id.address);
            email = findViewById(R.id.email);
            mob = findViewById(R.id.mob);
            btnCall = findViewById(R.id.btnCall);
            toolbar = findViewById(R.id.toolbarID);
            description=findViewById(R.id.description);
            organization=findViewById(R.id.organization);

            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            progressBar.setVisibility(View.VISIBLE);

            FirebaseConnectivity firebaseConnectivity=new FirebaseConnectivity("OrganizerKeyMap");
            firebaseConnectivity.getReference().child(teamName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        final String emailKeyMap=snapshot.getValue().toString();
                        FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getOrganizerDatabase());
                        connectivity.getReference().child(emailKeyMap).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    try{
                                    String image=snapshot.child("Image").getValue().toString();
                                    Picasso.get().load(image).into(imageProfile);}
                                    catch(NullPointerException npe){}

                                    OrganizerProfileModel model=snapshot.child("Other_Data").getValue(OrganizerProfileModel.class);
                                    name.setText(model.getName());
                                    organization.setText(teamName);
                                    email.setText(model.getEmail());
                                    mob.setText("+91 "+model.getMobileNo());
                                    description.setText(teamName+" : "+model.getDescription());
                                    mobNo=model.getMobileNo();
                                    btnCall.setVisibility(View.VISIBLE);
                                }
                                else
                                    Toast.makeText(ViewUsersProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ViewUsersProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewUsersProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            progressBar.setVisibility(View.GONE);
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call();
                }
            });
        }
    }

    //call to profiler
    private void call()
    {
        Intent call = new Intent(Intent.ACTION_DIAL);
        if (mobNo != null) {
            call.setData(Uri.parse("tel: +91 " + mobNo));
            startActivity(call);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid=item.getItemId();
        if(itemid==android.R.id.home)
            super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}