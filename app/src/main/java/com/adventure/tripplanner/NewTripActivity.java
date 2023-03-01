package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;

public class NewTripActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TextView btnSelectImage;
    private AppCompatButton btnCreate;
    private TextInputEditText destination,from,dateTo,dateFrom,charge,time,pickupLocation,vehicle,seats;
    private EditText inclusions,description;
    private final int IMAGE_REQ_CODE=1;
    private int total=0;


    private String destinationT;
    private String fromT;
    private String dateFromT;
    private String dateToT;
    private String chargeT;
    private String timeT;
    private String pickupLocationT;
    private String vehicleT;
    private String seatsT;
    private String inclusionsT;
    private String descriptionT;

    private Calendar calendar;
    private ProgressDialog progressDialog;

    private ArrayList<Uri> imagesUri= new ArrayList<>(); //array of images in form of uri.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        toolbar=findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Trip");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar=Calendar.getInstance();

        dateFrom = findViewById(R.id.dateFrom);
        dateTo = findViewById(R.id.dateTo);
        destination=findViewById(R.id.destinationID);
        from=findViewById(R.id.fromID);
        charge=findViewById(R.id.chargeID);
        time=findViewById(R.id.timeID);
        pickupLocation=findViewById(R.id.locationID);
        vehicle=findViewById(R.id.vehicleID);
        seats=findViewById(R.id.seatsID);
        inclusions=findViewById(R.id.inclusionID);
        description=findViewById(R.id.descriptionID);
        viewPager=findViewById(R.id.viewPager);
        btnSelectImage=findViewById(R.id.insertImageBtn);
        btnCreate=findViewById(R.id.btnCreate);


        progressDialog=new ProgressDialog(NewTripActivity.this);
        progressDialog.setTitle("Please wait !");
        progressDialog.setMessage("Your trip is creating. Once a trip is created later you can also modify it again.");
        progressDialog.setCancelable(false);


        //select dateFrom calender
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(0);
            }
        });

        //select dateTo from calender
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(1);
            }
        });


        //create
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationT=destination.getText().toString().trim();
                fromT=from.getText().toString().trim();
                dateFromT=dateFrom.getText().toString().trim();
                dateToT=dateTo.getText().toString().trim();
                pickupLocationT=pickupLocation.getText().toString().trim();
                timeT=time.getText().toString().trim();
                chargeT=charge.getText().toString().trim();
                vehicleT=vehicle.getText().toString().trim();
                seatsT=seats.getText().toString().trim();
                inclusionsT=inclusions.getText().toString().trim();
                descriptionT=description.getText().toString().trim();

                if(TextUtils.isEmpty(destinationT) || TextUtils.isEmpty(dateFromT) || TextUtils.isEmpty(fromT) || TextUtils.isEmpty(dateToT) || TextUtils.isEmpty(pickupLocationT) || TextUtils.isEmpty(timeT) || TextUtils.isEmpty(chargeT) || TextUtils.isEmpty(vehicleT) || TextUtils.isEmpty(seatsT) || TextUtils.isEmpty(inclusionsT))
                { Toast.makeText(NewTripActivity.this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();}
                else
                {
                    uploadData(destinationT,dateFromT);
                }
            }
        });

        //pick an images from gallery
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }

            private void pickImage() {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,IMAGE_REQ_CODE);

                if(imagesUri !=null)
                    imagesUri.clear();
            }
        });


    }


    //this method contains data(images) from gallery and added to array of images uri.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== IMAGE_REQ_CODE && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                int count=1;
                if(data.getClipData()!=null) //if images is more than one (multiple)
                {
                    count=data.getClipData().getItemCount();
                    for(int i=0;i<count;i++)
                    {
                        imagesUri.add(data.getClipData().getItemAt(i).getUri());
                    }
                }
                else
                    imagesUri.add(data.getData()); //single image

                Toast.makeText(this, "Total "+count+" images selected", Toast.LENGTH_SHORT).show();
                setAdapter();
            }
        }
        else
            Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
    }


    //adapter for inflating the images to view pager
    private void setAdapter()
    {
        ImageAdapter imageAdapter=new ImageAdapter(this,imagesUri);
        viewPager.setAdapter(imageAdapter);
    }


    //selecting date
    private void selectDate(int check) {
        int year,day,month;
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog=new DatePickerDialog(NewTripActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                if(check==0)
                    dateFrom.setText(dayOfMonth+"-"+Raw.getMonth(month)+"-"+year);
                else
                    dateTo.setText(dayOfMonth+"-"+Raw.getMonth(month)+"-"+year);
            }
        },year,month,day);
          dialog.show();
        }


        //upload the images and trip data to firebase.
        private void uploadData(String dest,String date) {

            Intent intent = getIntent();

            FirebaseConnectivity firebaseConnectivity = new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());

            if (imagesUri.isEmpty())
            {
                Toast.makeText(this, "Please upload image", Toast.LENGTH_SHORT).show();
            }
            else if(!Raw.connectedToInternet(getApplicationContext()))
            {
                View view=getLayoutInflater().inflate(R.layout.custom_toast_internet,(ViewGroup)findViewById(R.id.customToastInternet));
                new Raw().internetToast(view,NewTripActivity.this);
            }
            else {
                progressDialog.show();
                StorageReference reference = firebaseConnectivity.getStorageReference().child(intent.getStringExtra("TeamName")).child(dest +" "+ date).child("ImageFolder");

                //loop will execute till imagesUri contains array of images gets empty.
                for (int uploadCount = 0; uploadCount < imagesUri.size(); uploadCount++) {

                    final int x=uploadCount;


                    //get a single image from imageArray.
                    Uri singleImage = imagesUri.get(uploadCount);

                    StorageReference storageReference = reference.child("Image" + singleImage.getLastPathSegment());
                    storageReference.putFile(singleImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    firebaseConnectivity.getReference().child(intent.getStringExtra("TeamName")).child(dest +" "+ date).child("Images").child("image"+x).setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful())
                                                Toast.makeText(NewTripActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            if(x == imagesUri.size()-1)
                                               insertTripData();
                                        }

                                        private void insertTripData() {
                                            NewTripModelClass model = new NewTripModelClass(destinationT, fromT, dateFromT, dateToT, pickupLocationT, timeT, chargeT, vehicleT, seatsT, inclusionsT, descriptionT);
                                            firebaseConnectivity.getReference().child(intent.getStringExtra("TeamName")).child(dest +" "+ date).child("TripData").setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(NewTripActivity.this, "Your trip is successfully organized.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(NewTripActivity.this, OrganizerMainActivity.class);
                                                                startActivity(intent);
                                                                finishAffinity();
                                                    }
                                                    else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(NewTripActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewTripActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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