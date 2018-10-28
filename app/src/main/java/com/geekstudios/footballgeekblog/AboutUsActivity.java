package com.geekstudios.footballgeekblog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {

    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_about_us );

        link = findViewById( R.id.fb_page_link );
        link.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.facebook.com/GEEKStudios21/";
                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);

                }
            }
        } );

    }
}