package com.geekstudios.footballgeekblog;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpenBlogActivity extends AppCompatActivity {

    private TextView title;
    private TextView username;
    private TextView date;
    private TextView blogDesc;
    private ImageView featureImage;
    private CircleImageView userImage;
    private TextView claps;
    private FloatingActionButton clapBtn;

    private Toolbar mToolbar;
    private AdView adView;

    private DocumentReference clapRef;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_open_blog );

        title = findViewById( R.id.oblog_heading_text );
        username = findViewById( R.id.oblog_username_text );
        blogDesc = findViewById( R.id.oblog_description );
        featureImage = findViewById( R.id.oblog_feature_image_view );
        userImage = findViewById( R.id.oblog_user_image );
        claps = findViewById( R.id.clap_counter );
        clapBtn = findViewById( R.id.clap_floating_btn );

        firebaseFirestore = FirebaseFirestore.getInstance();

        mToolbar = findViewById(R.id.open_toolbar_5036);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( null );


        Typeface custom_font = Typeface.createFromAsset( getAssets(), "fonts/Kalpurush.ttf" );
        title.setTypeface( custom_font );
        blogDesc.setTypeface( custom_font );
        username.setTypeface( custom_font );

        // AD
        // String id = Settings.Secure.getString( getContentResolver(), Settings.Secure.ANDROID_ID );
        adView = findViewById( R.id.adView );
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd( adRequest );


        Intent i = getIntent();
        final String blogTitle = i.getStringExtra( "blogTitle" );
        title.setText( blogTitle );

        String clapCounter = i.getStringExtra( "claps_counter" );
        claps.setText( clapCounter );

        String blogDesccription = i.getStringExtra( "blogDesc" );
        blogDesc.setText( blogDesccription );

        String blogImage = i.getStringExtra( "blogImage" );
        RequestOptions blogImageRequest = new RequestOptions();
        blogImageRequest.placeholder( R.drawable.image_placeholder );
        Glide.with( OpenBlogActivity.this ).applyDefaultRequestOptions( blogImageRequest ).load( blogImage ).into( featureImage );

        final String user_id = i.getStringExtra( "userID" );
        firebaseFirestore.collection( "Users" ).document(user_id).get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

                    String userName = task.getResult().getString( "name" );
                    String userImage = task.getResult().getString( "image" );

                    UserData( userName, userImage );

                }

            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast.makeText( OpenBlogActivity.this, message, Toast.LENGTH_SHORT ).show();
            }
        } );;

        clapBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clapRef = firebaseFirestore.collection( "Blogs" ).document(blogTitle);
                clapRef.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot clapSS = task.getResult();
                            String clapStr = (String) clapSS.get( "clap_counter" );
                            int clapInt = Integer.parseInt( clapStr );
                            clapInt++;
                            clapStr = Integer.toString( clapInt );
                            claps.setText( clapStr );

                            clapRef.update( "clap_counter", clapStr );

                        }
                    }
                } );

                // Clap sound
                MediaPlayer claps = MediaPlayer.create( OpenBlogActivity.this, R.raw.clap_audio );
                claps.start();

            }
        } );

        username.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writerProfileIntent = new Intent( OpenBlogActivity.this, WriterProfileActivity.class );
                writerProfileIntent.putExtra( "user_id", user_id );
                startActivity( writerProfileIntent );
            }
        } );

        userImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writerProfileIntent = new Intent( OpenBlogActivity.this, WriterProfileActivity.class );
                writerProfileIntent.putExtra( "user_id", user_id );
                startActivity( writerProfileIntent );
            }
        } );
    }

    public void UserData(String userName, String image) {

        username.setText( userName );
        RequestOptions placeHolder = new RequestOptions();
        placeHolder.placeholder( R.drawable.profile_placeholder );

        Glide.with( OpenBlogActivity.this ).applyDefaultRequestOptions( placeHolder ).load(image).into( userImage );

    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed
        Toast.makeText(OpenBlogActivity.this, "Please use the back button in the toolbar.", Toast.LENGTH_SHORT).show();
    }
}