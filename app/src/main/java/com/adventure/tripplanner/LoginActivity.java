package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import android.app.Dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputEmail,inputPassword;
    private AppCompatButton btnSignIn;
    private TextView resetPass,signUp;
    private FirebaseAuth auth;
    private String email,pass;
    private Dialog dialog;
    private ProgressBar progressBar;
    private static SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //finding id of views
        inputEmail=findViewById(R.id.loginEmailID);
        inputPassword=findViewById(R.id.loginPassID);
        btnSignIn=findViewById(R.id.btnSignin);
        resetPass=findViewById(R.id.resetPassID);
        signUp=findViewById(R.id.signUpID);
        progressBar=findViewById(R.id.progressBar);

        //auth initializing
        auth=FirebaseAuth.getInstance();


        //check user logged before
        sharedPreferences=getSharedPreferences(Raw.getSharedFileName(),MODE_PRIVATE);
        isLoggedIn();



        // click on sign in
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 email= inputEmail.getText().toString().trim();
                 pass = inputPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {    inputEmail.setError("Enter email");
                     inputEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(pass) )
                {    inputPassword.setError("Enter password");
                     inputPassword.requestFocus(); }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);

                    //firebase authentication using email/password.

                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                          {
                            if(task.isSuccessful())
                            {

                                //modified email which work as a key or root of its child
                                //the database data manipulation will done by this key.

                                final String keyEmail =email.replace(".","^");
                                String path=FirebaseConnectivity.getUserDatabase()+"/"+ keyEmail;

                                //getting the user data to check status either user is traveler or organizer

                                FirebaseConnectivity firebaseConnectivity=new FirebaseConnectivity(path);
                                firebaseConnectivity.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists())
                                        {
                                            //get the status from DB(not_defined,traveler,organizer)
                                            ChoiceModel choiceModel = snapshot.getValue(ChoiceModel.class);

                                            //if the status of user(either traveler or organizer) is not defined then the dialog box is opened.
                                            //this dialog will shown only once at the time of creating new user.
                                            if (choiceModel.getValue().equals(FirebaseConnectivity.getDataNotDefined()))
                                            {      progressBar.setVisibility(View.GONE);
                                                   dialogCheck();
                                            }

                                            //if status is set to organizer and user logged earlier.
                                            else if (choiceModel.getValue().equals(FirebaseConnectivity.STATUS_ORGANIZER))
                                            {
                                                Raw.share(0);
                                                Intent intent = new Intent(LoginActivity.this, OrganizerMainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                            //status is set to traveler and user logged earlier
                                            else {
                                                Raw.share(1);
                                                Intent intent = new Intent(LoginActivity.this,TravelerMainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        //if data not found this block will execute
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            //it will execute when any error occur while authentication
                            else
                            {
                                 progressBar.setVisibility(View.GONE);
                                try {
                                      throw task.getException();
                                    }
                                catch(FirebaseNetworkException f)
                                {  View view=getLayoutInflater().inflate(R.layout.custom_toast_internet,(ViewGroup)findViewById(R.id.customToastInternet));
                                    new Raw().internetToast(view,LoginActivity.this); } //No Internet connection
                                catch (Exception e)
                                {  LinearLayout linearLayout=findViewById(R.id.linearID); //invalid email/pass or any another exception
                                    linearLayout.setVisibility(View.VISIBLE);
                                    resetPass.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        });



        // sign up activity
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });



        // reset password
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                email=inputEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {  inputEmail.setError("Please enter email"); inputEmail.requestFocus(); }
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    //firebase auth for reset password via email.
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                                Toast.makeText(LoginActivity.this, "Reset password link sent to your email", Toast.LENGTH_SHORT).show();
                            else
                            {
                                try { throw task.getException(); }
                                catch(FirebaseNetworkException ae)
                                {
                                    //for internet toast
                                    View view=getLayoutInflater().inflate(R.layout.custom_toast_internet,(ViewGroup)findViewById(R.id.customToastInternet));
                                    new Raw().internetToast(view,LoginActivity.this);
                                } //no internet
                                catch(FirebaseAuthInvalidCredentialsException ae)
                                { inputEmail.setError("Enter valid email"); inputEmail.requestFocus();} //invalid email format
                                catch (Exception e)
                                { Toast.makeText(LoginActivity.this, "E-mail doesn't exist.", Toast.LENGTH_SHORT).show(); }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                     });
                }
            }
         });
     }


     // user logged or not
    private void isLoggedIn()
    {
        final String DEFAULT_VALUE="not_defined";
        String value=sharedPreferences.getString(Raw.getSharedRefKey(),DEFAULT_VALUE);

        //status of user is organizer then go to organizer activity
        if(value.equals(FirebaseConnectivity.STATUS_ORGANIZER))
        {
            Intent intent=new Intent(LoginActivity.this,OrganizerMainActivity.class);
            startActivity(intent);
            finish();
        }

        //status is traveler
        if(value.equals(FirebaseConnectivity.STATUS_TRAVELER))
        {
            Intent intent=new Intent(LoginActivity.this,TravelerMainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    // dialog for user choice
    private void dialogCheck()
    {

        //custom dialog
        dialog=new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.choice_dialog);
        dialog.setCancelable(false);
        dialog.show();

        //Widgets(Views) on dialog
        MaterialButton btnOrganizer,btnTraveler;
        btnOrganizer=dialog.findViewById(R.id.btnOrganizer);
        btnTraveler=dialog.findViewById(R.id.btnTraveler);

        Intent get=getIntent();  //register to login takes name


        //keys for passing the values in intent
        final String KEY="emailKey"; //email
        final String NAME="Name";  //name

        //clicked on organizer
        btnOrganizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent intent=new Intent(LoginActivity.this, OrganizerProfileActivity.class);
                intent.putExtra(KEY,email.replace(".","^"));
                intent.putExtra("callFrom","dialog");
                if(get!=null)
                  intent.putExtra(NAME,get.getStringExtra(NAME));
                startActivity(intent);
                finish();
            }
        });


        //clicked on traveler
        btnTraveler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
              Intent intent=new Intent(LoginActivity.this,TravelerProfileActivity.class);
              intent.putExtra(KEY,email);
              if(get!=null)
                  intent.putExtra(NAME,get.getStringExtra(NAME));
              startActivity(intent);
              finish();
            }
        });
    }

    //return reference of local storage - shared preferences
    public static SharedPreferences getShared()
    {
        return sharedPreferences;
    }


}
