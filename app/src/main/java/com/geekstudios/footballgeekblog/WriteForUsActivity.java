package com.geekstudios.footballgeekblog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WriteForUsActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button requestBtn;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_write_for_us );

        loginBtn = findViewById( R.id.wnb_login );
        requestBtn = findViewById( R.id.wnb_Apply );
        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent( WriteForUsActivity.this, LoginActivity.class );
                    startActivity( loginIntent );

                } else {

                    Toast.makeText( WriteForUsActivity.this, "You are already logged in.", Toast.LENGTH_SHORT ).show();

                }

            }
        } );

        requestBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData( Uri.parse("mailto:geekstudios21@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Request to create an Author account");
                    intent.putExtra(Intent.EXTRA_TEXT, "Fill up the mail body with your name, institution, what makes our team empowered having you with us. You can also tell us about your journey with football and don't forget to share your writings. Greetings, Team FootballGeek. Hala Football.");
                if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
        } );

    }
}
