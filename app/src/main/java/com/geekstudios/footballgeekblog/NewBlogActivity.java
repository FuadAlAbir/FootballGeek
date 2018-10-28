package com.geekstudios.footballgeekblog;



import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.geekstudios.footballgeekblog.MainActivity;
import com.geekstudios.footballgeekblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewBlogActivity extends AppCompatActivity {

    private Toolbar blogToolbar;

    private ImageView blogImage;
    private EditText blogDesc;
    private EditText blogTitle;
    private Button blogBtn;
    private ProgressBar blogProgressBar;
    private String claps;

    private Uri blogUploadedImageUri = null;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentReference blogRef;

    private String user_id;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_new_blog );

        Random rand = new Random();
        claps = Integer.toString( rand.nextInt(22) + 21 );

        blogToolbar = findViewById( R.id.new_blog_toolbar );
        setSupportActionBar( blogToolbar );
        getSupportActionBar().setTitle( null );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        blogImage = findViewById( R.id.new_blog_image_insert_place_holder );
        blogTitle = findViewById( R.id.new_blog_title );
        blogDesc = findViewById( R.id.new_blog_add_description_edit_text );
        blogBtn = findViewById( R.id.new_blog_post_btn );
        blogProgressBar = findViewById( R.id.new_blog_progress_bar );
        blogProgressBar.setVisibility( View.INVISIBLE );

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        blogImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines( CropImageView.Guidelines.ON)
                        .setMinCropResultSize( 648, 400 )
                        .setAspectRatio( 162, 100 )
                        .start(NewBlogActivity.this);
            }
        } );

        blogBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = blogDesc.getText().toString().trim();
                final String title = blogTitle.getText().toString().trim();


                if(!TextUtils.isEmpty( title ) && !TextUtils.isEmpty(desc) && blogUploadedImageUri != null) {

                    blogProgressBar.setVisibility( View.VISIBLE );

                    final String randomStr = UUID.randomUUID().toString();

                    StorageReference filePath = storageReference.child("blog_images").child(randomStr + ".jpg");
                    filePath.putFile( blogUploadedImageUri ).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            final String downloadURI = task.getResult().getDownloadUrl().toString();

                            if(task.isSuccessful()) {

                                final File blogImageThumb = new File(blogUploadedImageUri.getPath());
                                try {

                                    compressedImageFile = new Compressor(NewBlogActivity.this)
                                            .setMaxWidth( 100 )
                                            .setMaxHeight( 100 )
                                            .setQuality( 1 )
                                            .compressToBitmap( blogImageThumb);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child( "blog_images/thumbs" )
                                        .child(randomStr + ".jpg")
                                        .putBytes( thumbData );

                                uploadTask.addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadThumbURI = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> blogMap = new HashMap<>(  );
                                        blogMap.put("image_url", downloadURI);
                                        blogMap.put("image_thumb", downloadThumbURI);
                                        blogMap.put("title", title );
                                        blogMap.put("desc", desc);
                                        blogMap.put("user_id", user_id);
                                        blogMap.put("clap_counter", claps);
                                        blogMap.put("time", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection( "Blogs" ).document(title).set(blogMap)
                                                .addOnCompleteListener( new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {

                                                            Toast.makeText(NewBlogActivity.this, "Blog Published!", Toast.LENGTH_LONG).show();
                                                            sendToMain();
                                                            finish();

                                                        } else {

                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(NewBlogActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                                        }

                                                        blogProgressBar.setVisibility( View.INVISIBLE );
                                                    }
                                                } );

                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        String error = e.getMessage();
                                        Toast.makeText(NewBlogActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();
                                        blogProgressBar.setVisibility( View.INVISIBLE );

                                    }
                                } );

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(NewBlogActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                blogProgressBar.setVisibility( View.INVISIBLE );
                            }
                        }
                    } );

                } else {

                    Toast.makeText(NewBlogActivity.this, "Add blog title, description and a suitable HD feature image.", Toast.LENGTH_LONG).show();

                }

            }
        } );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                blogUploadedImageUri = result.getUri();
                blogImage.setImageURI( blogUploadedImageUri );

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {

            sendToMain();
            finish();

        } else if (firebaseAuth.getCurrentUser() != null) {
            user_id = firebaseAuth.getCurrentUser().getUid();

            firebaseFirestore.collection( "Users" ).document( user_id ).get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        if (!task.getResult().exists()) {
                            sendToSetup();
                            finish();
                        }

                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText( NewBlogActivity.this, "Error: " + error, Toast.LENGTH_SHORT ).show();

                    }

                }
            } );
        } else {
            Toast.makeText( NewBlogActivity.this, "Firebase Error: Failed to get any user data.", Toast.LENGTH_SHORT ).show();
        }

    }

    private void sendToSetup() {

        Intent setupIntent = new Intent(NewBlogActivity.this, SetupActivity.class);
        startActivity(setupIntent);

    }


    private void sendToMain() {

        Intent mainIntent = new Intent(NewBlogActivity.this, MainActivity.class);
        startActivity(mainIntent);

    }

}
