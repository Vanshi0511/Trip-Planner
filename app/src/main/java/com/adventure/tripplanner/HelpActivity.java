package com.adventure.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class HelpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatButton btnSendQuery;
    private EditText queryMessage;
    private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar=findViewById(R.id.toolbarID);
        btnSendQuery=findViewById(R.id.btnSendQuery);
        queryMessage=findViewById(R.id.queryMessage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //send email for your query.
        btnSendQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=queryMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    queryMessage.setError("Message can't be empty");
                    queryMessage.requestFocus();
                }
                else
                {
                    Intent iMail=new Intent(Intent.ACTION_SEND);
                    iMail.setType("message/rfc822");
                    iMail.putExtra(Intent.EXTRA_EMAIL,new String[]{"sanskarpatidar14@gmail.com"});
                    iMail.putExtra(Intent.EXTRA_SUBJECT,"Issue about app");
                    iMail.putExtra(Intent.EXTRA_TEXT,message);
                    startActivity(Intent.createChooser(iMail,"Email via"));

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