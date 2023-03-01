package com.adventure.tripplanner;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Raw
{

    private static final String SHARED_REF_KEY="isLogged";
    private static final String SHARED_FILE_NAME="CheckLogged";

    static ProgressDialog progressDialog;
    static FirebaseUser user;


    //internet toast
    public void internetToast(View view,Activity context)
    {
        Toast toast=new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM,0,250);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static String getSharedRefKey()
    {
        return SHARED_REF_KEY;
    }


    //when log out it changes the status of user
    public static void share(int check)
    {
        SharedPreferences.Editor editor=LoginActivity.getShared().edit();
        if(check==0) //0 for organizer
          editor.putString(Raw.getSharedRefKey(),FirebaseConnectivity.STATUS_ORGANIZER).apply();
        else //1 for traveler
            editor.putString(Raw.getSharedRefKey(),FirebaseConnectivity.STATUS_TRAVELER).apply();
    }

    //by calender
    public static String getMonth(int mon)
    {
        if(mon==1) return "Jan";
        if(mon==2) return "Feb";
        if(mon==3) return "Mar";
        if(mon==4) return "Apr";
        if(mon==5) return "May";
        if(mon==6) return "Jun";
        if(mon==7) return "Jul";
        if(mon==8) return "Aug";
        if(mon==9) return "Sep";
        if(mon==10) return "Oct";
        if(mon==11) return "Nov";
        if(mon==12) return "Dec";
        else return null;
    }

    //check for internet
    public static boolean connectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED  ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()== NetworkInfo.State.CONNECTED)
        { return true; }
        else
          return false;
    }


    public static String getSharedFileName()
    {
        return SHARED_FILE_NAME;
    }


    //log out user
    public static void showLogOutDialog(Activity activity)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.ic_logout);
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure to log out your account ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences=activity.getSharedPreferences(SHARED_FILE_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Raw.getSharedRefKey(),"not_defined").apply();

                Intent intent=new Intent(activity,LoginActivity.class);
                activity.startActivity(intent);
                activity.finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    //deactivate
    public static void showDeactivateDialog(Activity activity,String userStatus,String teamName)
    {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        progressDialog=new ProgressDialog(activity);
        progressDialog.setTitle("Alert!");
        progressDialog.setMessage("Deactivating please wait");
        progressDialog.setCancelable(false);


        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.ic_deactivate);
        builder.setTitle("Alert!");
        builder.setMessage("Are you sure to deactivate your account ?");
        EditText text=new EditText(activity);
        text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        text.setHint("Password");
        builder.setView(text);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(text.getText())) {
                    text.setError("Enter password");
                    text.requestFocus();
                } else {
                    AuthCredential authCredential;
                    progressDialog.show();
                    try {
                        authCredential = EmailAuthProvider.getCredential(user.getEmail(), text.getText().toString());
                        user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (userStatus.equals(FirebaseConnectivity.STATUS_TRAVELER))
                                        deactivateTravelerAccount(activity, user.getEmail());
                                    else
                                        deactivateOrganizerAccount(activity, user.getEmail(), teamName);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(activity, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    catch (Exception e){
                        progressDialog.dismiss();
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    //deactivate traveler
    private static void deactivateTravelerAccount(Activity activity,String email)
    {
        Log.d("aa","ss");
        final String emailKey=email.replace(".","^");
        Log.d("key",emailKey);
        FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getTravelerDatabase());
        FirebaseStorage storage=FirebaseStorage.getInstance();
        connectivity.getReference().child(emailKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Log.d("kk","s");
                    String userImage="";
                    try{
                    userImage=snapshot.child("Image").getValue().toString();}
                    catch (NullPointerException npe) {}
                    if(userImage != null)
                    { storage.getReferenceFromUrl(userImage).delete(); Log.d("pp","p"); }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        connectivity.getReference().child(emailKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    new FirebaseConnectivity(FirebaseConnectivity.getUserDatabase()).getReference().child(emailKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                user.delete();
                                SharedPreferences sharedPreferences=activity.getSharedPreferences(SHARED_FILE_NAME,Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Raw.getSharedRefKey(),"not_defined").apply();
                                progressDialog.dismiss();
                                Toast.makeText(activity, "Account Deactivated", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(activity,LoginActivity.class);
                                activity.startActivity(intent);
                                activity.finishAffinity();
                            }
                            else
                                Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //deactivate organizer
    private static void deactivateOrganizerAccount(Activity activity,String email,String teamName)
    {

        ArrayList<String> imagesUrl =new ArrayList<>();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        final String emailKey=email.replace(".","^");
        new FirebaseConnectivity(FirebaseConnectivity.getUserDatabase()).getReference().child(emailKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FirebaseConnectivity tripConnectivity=new FirebaseConnectivity(FirebaseConnectivity.getDatabaseTrip());
                    tripConnectivity.getReference().child(teamName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                for(DataSnapshot teamChild : snapshot.getChildren())
                                {

                                    for(DataSnapshot imagesSnap : teamChild.child("Images").getChildren())
                                    {
                                        imagesUrl.add(imagesSnap.getValue().toString());
                                    }
                                }

                                Log.d("uri",imagesUrl.get(0));
                                for(int i=0;i<imagesUrl.size();i++)
                                {
                                StorageReference reference=storage.getReferenceFromUrl(imagesUrl.get(i));
                                reference.delete();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    FirebaseConnectivity con=new FirebaseConnectivity("OrganizerKeyMap");
                    con.getReference().child(teamName).removeValue();
                    tripConnectivity.getReference().child(teamName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getOrganizerDatabase());
                                connectivity.getReference().child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String imageUri=null;
                                        try {
                                            imageUri = snapshot.child("Image").getValue().toString();
                                        }
                                        catch (NullPointerException npe){}
                                        if(imageUri != null)
                                           storage.getReferenceFromUrl(imageUri).delete();
                                           connectivity.getReference().child(emailKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    user.delete();
                                                    SharedPreferences sharedPreferences=activity.getSharedPreferences(SHARED_FILE_NAME,Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                                    editor.putString(Raw.getSharedRefKey(),"not_defined").apply();
                                                    progressDialog.dismiss();
                                                    Toast.makeText(activity, "Account Deactivated", Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent(activity,LoginActivity.class);
                                                    activity.startActivity(intent);
                                                    activity.finishAffinity();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });
    }

}

