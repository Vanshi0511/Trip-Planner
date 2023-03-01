package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;


public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText email,pass,confirmPass,name;
    private AppCompatButton signUp;
    private FirebaseAuth auth;
    private String inputName,inputEmail,inputPass,inputConfirmPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ProgressBar progressBar;


        //find id of views
        email=findViewById(R.id.emailID);
        pass=findViewById(R.id.passID);
        name=findViewById(R.id.nameID);
        confirmPass=findViewById(R.id.confirmPassID);
        progressBar=findViewById(R.id.progressBar);
        signUp=findViewById(R.id.btnsignUp);

        //authentication
        auth=FirebaseAuth.getInstance();

        //clicked on signup
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputName=name.getText().toString().trim();
                inputEmail=email.getText().toString().trim();
                inputPass=pass.getText().toString().trim();
                inputConfirmPass=confirmPass.getText().toString().trim();

                if(TextUtils.isEmpty(inputName))
                { name.setError("Enter name"); name.requestFocus();}
                else if(TextUtils.isEmpty(inputEmail))
                { email.setError("Enter email"); email.requestFocus(); }
                else if(TextUtils.isEmpty(inputPass))
                { pass.setError("Enter password"); pass.requestFocus();}
                else if(TextUtils.isEmpty(inputConfirmPass))
                { confirmPass.setError("Enter confirmation password"); confirmPass.requestFocus();}
                else if(!inputPass.equals(inputConfirmPass))
                { confirmPass.setError("Password not matched"); confirmPass.requestFocus();}
                else
                {
                    progressBar.setVisibility(View.VISIBLE);

                    //firebase authentication
                    auth.createUserWithEmailAndPassword(inputEmail,inputPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                //initially set the status of user not_defined.
                                FirebaseConnectivity connectivity=new FirebaseConnectivity(FirebaseConnectivity.getUserDatabase());

                                //set the value not_defined.
                                ChoiceModel choiceModel=new ChoiceModel(FirebaseConnectivity.getDataNotDefined());


                                //email-key
                                final String emailKey=inputEmail.replace(".","^");

                                //set the value(status) of user not_defined.
                                connectivity.getReference().child(emailKey).setValue(choiceModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful())
                                        {  try { throw task.getException(); }
                                        catch(Exception e)
                                           { Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); }
                                        }
                                    }
                                });

                                Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);

                                //back to login
                                final String KEY="Name";
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra(KEY,inputName); //register to login taking name.
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                //if any error occur while authentication.

                                  try { throw task.getException(); }
                                  catch (FirebaseAuthWeakPasswordException fwp)
                                    { pass.setError("Minimum 6 characters") ; email.requestFocus();} //password exception
                                  catch (FirebaseAuthInvalidCredentialsException f)
                                  {  email.setError("Invalid email format"); email.requestFocus();} //invalid email format
                                  catch(FirebaseNetworkException e)
                                  { View view=getLayoutInflater().inflate(R.layout.custom_toast_internet,(ViewGroup)findViewById(R.id.customToastInternet));
                                      new Raw().internetToast(view,RegisterActivity.this);} // no internet connection
                                  catch (Exception e)
                                      {Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show(); }

                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

}