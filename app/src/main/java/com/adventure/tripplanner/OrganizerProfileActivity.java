package com.adventure.tripplanner;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class OrganizerProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText team,name,address,mob,email;
    private EditText description;
    private AppCompatButton save;
    private TextView uploadImage;
    private ImageView image;
    private Uri userImage;
    private StorageReference storageReference;
    private ActivityResultLauncher<String> launcher;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

            //set toolbar
            toolbar=findViewById(R.id.toolbarID);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("My Profile");


            //find id of views
            team=findViewById(R.id.teamID);
            name=findViewById(R.id.nameID);
            address=findViewById(R.id.addressID);
            mob=findViewById(R.id.mobileID);
            description=findViewById(R.id.descriptionID);
            save=findViewById(R.id.btnsave);
            uploadImage=findViewById(R.id.btnImage);
            image=findViewById(R.id.imageProfile);
            email=findViewById(R.id.emailID);
            progressBar=findViewById(R.id.progressBar);


            //login to this activity contains email and name.
            Intent get=getIntent();

            final String NAME="Name";
            final String KEY="emailKey";

            String emailKey="";

            if(get!=null) {
                //name.setText(get.getStringExtra(NAME));
                email.setText(get.getStringExtra(KEY).replace("^","."));
                emailKey=get.getStringExtra(KEY);

                //calling profile from organizerMainActivity gets main for disable change team name.
                if(get.getStringExtra("callFrom").equals("main"))
                {
                    team.setEnabled(false);
                    team.setKeyListener(null);
                }
                getData(emailKey);
                email.setEnabled(false);
                email.setKeyListener(null);
            }

            //launcher for getting the image from gallery.
            launcher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {

                    //set image to imageView from uri and also contain its copy for later use.
                    userImage=result;
                    image.setImageURI(result); //ImageView
                }
            });


            //listener to launch a launcher to get the image from gallery
            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launcher.launch("image/*");
                 }
             });


            //click on save button
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String inputName=name.getText().toString().trim();
                    String inputTeam=team.getText().toString().trim();
                    String inputAddress=address.getText().toString().trim();
                    String inputMob=mob.getText().toString().trim();
                    String inputDesc=description.getText().toString().trim();
                    String inputEmail=email.getText().toString().trim();


                    if(TextUtils.isEmpty(inputTeam) || TextUtils.isEmpty(inputName) || TextUtils.isEmpty(inputAddress) || TextUtils.isEmpty(inputMob) || TextUtils.isEmpty(inputDesc) || TextUtils.isEmpty(inputEmail))
                        Toast.makeText(OrganizerProfileActivity.this, "Please enter all the fields.", Toast.LENGTH_SHORT).show();
                    else
                    {
                        progressBar.setVisibility(View.VISIBLE);

                        //getting reference of storage database for storing profile images of organizers.
                        // DB name Organizers.
                        FirebaseConnectivity firebaseConnectivity=new FirebaseConnectivity(FirebaseConnectivity.getOrganizerDatabase());



                        //email-key
                        final String emailKey=inputEmail.replace(".","^");


                        if(userImage != null) {

                            //insert image in storage DB
                            final String IMAGE_ROOT = "Profile_Images";


                            storageReference = firebaseConnectivity.getStorageReference().child(IMAGE_ROOT).child(emailKey);

                            storageReference.putFile(userImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        //now we want url of that image which will be inserted in RTDB(real time DB).
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String PROFILE_IMAGE = "Image";


                                                //inserting url of image in RTDB. converting uri in string
                                                firebaseConnectivity.getReference().child(emailKey).child(PROFILE_IMAGE).setValue(uri.toString());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(OrganizerProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {

                                        try {
                                            throw task.getException();
                                        } catch (FirebaseNetworkException firebaseNetworkException) {
                                            View view = getLayoutInflater().inflate(R.layout.custom_toast_internet, (ViewGroup) findViewById(R.id.customToastInternet));
                                            new Raw().internetToast(view, OrganizerProfileActivity.this);
                                        }  //no internet
                                        catch (Exception e) {
                                            Toast.makeText(OrganizerProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                       // progressBar.setVisibility(View.GONE);
                                    }

                                }
                            });

                        }

                        //inserting organizers data in DB.

                        //set the values in model class
                        OrganizerProfileModel model=new OrganizerProfileModel(inputName,inputEmail,inputMob,inputTeam,inputDesc,inputAddress);

                        //insert data
                        final String OTHER_DATA="Other_Data";
                        firebaseConnectivity.getReference().child(emailKey).child(OTHER_DATA).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    //after inserting successfully data we need to update the status of user to organizer.
                                    FirebaseConnectivity.updateUserStatus(FirebaseConnectivity.STATUS_ORGANIZER,emailKey);

                                    //also update the value to organizer to not to show again login and directly jump to organizer.
                                    Raw.share(0);

                                    FirebaseConnectivity connectivity=new FirebaseConnectivity("OrganizerKeyMap");
                                    connectivity.getReference().child(inputTeam).setValue(emailKey);
                                    Toast.makeText(OrganizerProfileActivity.this, "Updated Record", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(OrganizerProfileActivity.this,OrganizerMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    try { throw task.getException() ; }
                                    catch (FirebaseNetworkException firebaseNetworkException) //no internet
                                    { View view=getLayoutInflater().inflate(R.layout.custom_toast_internet,(ViewGroup)findViewById(R.id.customToastInternet));
                                        new Raw().internetToast(view,OrganizerProfileActivity.this);}
                                    catch (Exception e)
                                    {
                                        Toast.makeText(OrganizerProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            }


                        });

                    }
                }
            });
    }

    private void getData(String emailKey) {

        progressBar.setVisibility(View.VISIBLE);
        FirebaseConnectivity firebaseConnectivity=new FirebaseConnectivity(FirebaseConnectivity.getOrganizerDatabase());
        firebaseConnectivity.getReference().child(emailKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String getImage;
                    try {
                        getImage = snapshot.child("Image").getValue().toString();
                    } catch (Exception e) {
                        getImage = null;
                    }
                    OrganizerProfileModel model = snapshot.child("Other_Data").getValue(OrganizerProfileModel.class);

                    name.setText(model.getName());
                    team.setText(model.getTeamName());
                    mob.setText(model.getMobileNo());
                    address.setText(model.getAddress());
                    description.setText(model.getDescription());

                    if (getImage != null) {
                        Picasso.get().load(getImage).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrganizerProfileActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        progressBar.setVisibility(View.GONE);

    }


}