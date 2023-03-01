package com.adventure.tripplanner;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class TravelerProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText name, mob, address;
    private AppCompatButton btnSave;
    private ProgressBar progressBar;
    private ShapeableImageView imageProfile;
    private TextView btnSelectImage;
    private ActivityResultLauncher<String> launcher;
    private Uri userImage;
    private FirebaseConnectivity firebaseConnectivity;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_profile);

        //set up custom toolbar
        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");


        //find id of views
        name = findViewById(R.id.nameID);
        mob = findViewById(R.id.mobID);
        address = findViewById(R.id.addressID);
        btnSave = findViewById(R.id.btnsave);
        progressBar = findViewById(R.id.progressBar);
        imageProfile=findViewById(R.id.imageProfile);
        btnSelectImage=findViewById(R.id.btnImage);


        //getting ref of RTDB.
        firebaseConnectivity=new FirebaseConnectivity(FirebaseConnectivity.getTravelerDatabase());

        Intent get=getIntent(); //login to this activity taking email
        final String KEY="emailKey";

        //email-key
        final String emailKey=get.getStringExtra(KEY).replace(".","^");
        getData(emailKey);

        //launcher for getting the image from gallery.
        launcher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                //set image to imageView from uri and also contain its copy for later use.
                userImage=result;
                imageProfile.setImageURI(result); //ImageView
            }
        });


        //listener to launch a launcher to get the image from gallery
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String inputName = name.getText().toString().trim();
                String inputMob = mob.getText().toString().trim();
                String inputAddress = address.getText().toString().trim();

                if (TextUtils.isEmpty(inputAddress) || TextUtils.isEmpty(inputMob) || TextUtils.isEmpty(inputName))
                    Toast.makeText(TravelerProfileActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();
                else {

                    progressBar.setVisibility(View.VISIBLE);




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
                                            Toast.makeText(TravelerProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {

                                    try {
                                        throw task.getException();
                                    } catch (FirebaseNetworkException firebaseNetworkException) {
                                        View view = getLayoutInflater().inflate(R.layout.custom_toast_internet, (ViewGroup) findViewById(R.id.customToastInternet));
                                        new Raw().internetToast(view, TravelerProfileActivity.this);
                                    }  //no internet
                                    catch (Exception e) {
                                        Toast.makeText(TravelerProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    //progressBar.setVisibility(View.GONE);
                                }

                            }
                        });

                    }

                    //getting reference of traveler database

                    //set the value in traveler model class
                    TravelerProfileModel travelerModel = new TravelerProfileModel(inputName, inputMob, inputAddress);

                    //insert data in traveler database
                    firebaseConnectivity.getReference().child(emailKey).child("Other_Data").setValue(travelerModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                //if the data is inserted in DB then update the status of user to traveler.
                                FirebaseConnectivity.updateUserStatus(FirebaseConnectivity.STATUS_TRAVELER,emailKey);

                                //update the value of shared reference also to not to show again login activity and go to traveler activity.
                                Raw.share(1);


                                Toast.makeText(TravelerProfileActivity.this, "Record Updated", Toast.LENGTH_SHORT).show();

                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(TravelerProfileActivity.this, TravelerMainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                try {  throw task.getException(); }
                                catch (FirebaseNetworkException firebaseNetworkException)
                                {  View view=getLayoutInflater().inflate(R.layout.custom_toast_internet,(ViewGroup)findViewById(R.id.customToastInternet));
                                    new Raw().internetToast(view,TravelerProfileActivity.this); } //no internet
                                 catch (Exception e) {
                                    Toast.makeText(TravelerProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    //getting data from DB and set to views.
    private void getData(String emailKey) {

        progressBar.setVisibility(View.VISIBLE);
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
                    TravelerProfileModel model = snapshot.child("Other_Data").getValue(TravelerProfileModel.class);

                    name.setText(model.getName());
                    mob.setText(model.getMobile());
                    address.setText(model.getAddress());

                    if (getImage != null) {
                        Picasso.get().load(getImage).into(imageProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TravelerProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressBar.setVisibility(View.GONE);

    }

}
