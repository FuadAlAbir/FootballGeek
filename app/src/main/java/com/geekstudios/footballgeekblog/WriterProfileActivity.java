package com.geekstudios.footballgeekblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class WriterProfileActivity extends AppCompatActivity {

    //private CircleImageView writerImage;
    //private TextView writerName;

    private AdapterBlogRecycler blogRecyclerAdapter;
    private List<classBlogItem> blog_list;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private String user_id;

    private RecyclerView blog_list_view;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = findViewById( R.id.custom_toolbar_5036 );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( null );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        //writerImage = findViewById( R.id.writer_profile_user_image );
        //writerName = findViewById( R.id.writer_profile_username_text );

        blog_list = new ArrayList<>();
        blog_list_view = findViewById( R.id.blog_list_view );

        blogRecyclerAdapter = new AdapterBlogRecycler(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(WriterProfileActivity.this ));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
/*
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
                Toast.makeText( WriterProfileActivity.this, message, Toast.LENGTH_SHORT ).show();
            }
        } );
*/


        blog_list_view.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom){
                    loadMorePost();
                }
            }
        });

        Intent i = getIntent();
        user_id = i.getStringExtra( "user_id" );
        Query firstQuery = firebaseFirestore.collection("Blogs")
                .whereEqualTo( "user_id", user_id )
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(3);
        firstQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if ( documentSnapshots != null && !documentSnapshots.isEmpty() ) {

                    if (isFirstPageFirstLoad) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        blog_list.clear();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            classBlogItem blogPost = doc.getDocument().toObject(classBlogItem.class);

                            if (isFirstPageFirstLoad) {

                                blog_list.add(blogPost);

                            } else {

                                blog_list.add(0, blogPost);

                            }

                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }

                    isFirstPageFirstLoad = false;

                }

            }

        });

    }

    public void loadMorePost(){


            Query nextQuery = firebaseFirestore.collection("Blogs")
                    .whereEqualTo( "user_id", user_id )
                    .orderBy("time", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if ( documentSnapshots != null && !documentSnapshots.isEmpty()) {
                        //Toast.makeText( WriterProfileActivity.this, "Loading more blogs...", Toast.LENGTH_SHORT ).show();
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                classBlogItem blogPost = doc.getDocument().toObject(classBlogItem.class);
                                blog_list.add(blogPost);

                                blogRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    } else {

                        Toast.makeText( WriterProfileActivity.this, "You're at the bottom of the blog list.", Toast.LENGTH_SHORT ).show();

                    }
                }
            });
    }

    // inflate main activity with menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    // menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main_menu_account_settings:
                if(firebaseAuth.getCurrentUser() != null) {
                    sendToSetup();
                } else {
                    Toast.makeText(WriterProfileActivity.this, "This function is for only our contributing writers.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.main_menu_write_new_blog:
                if(firebaseAuth.getCurrentUser() != null) {
                    sendToNewBlog();
                } else {
                    Toast.makeText(WriterProfileActivity.this, "Login first...", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.main_menu_write_for_us:
                sentToWriteForUs();
                return true;
            case R.id.main_menu_About_us:
                sentToAboutUs();
                return true;

            case R.id.main_menu_logout:
                if(firebaseAuth.getCurrentUser() != null) {
                    logout();
                    Toast.makeText(WriterProfileActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WriterProfileActivity.this, "You are not logged in!", Toast.LENGTH_SHORT).show();
                }

                return true;

            default:
                return false;
        }
    }

    private void sendToNewBlog() {

        Intent newBlogIntent = new Intent( WriterProfileActivity.this, NewBlogActivity.class );
        startActivity(newBlogIntent);

    }

    private void sendToSetup() {

        Intent setupIntent = new Intent( WriterProfileActivity.this, SetupActivity.class );
        startActivity(setupIntent);

    }

    private void sentToWriteForUs() {

        Intent writeForUsIntent = new Intent( WriterProfileActivity.this, WriteForUsActivity.class );
        startActivity(writeForUsIntent);

    }

    private void sentToAboutUs() {

        Intent aboutUsIntent = new Intent( WriterProfileActivity.this, AboutUsActivity.class );
        startActivity(aboutUsIntent);

    }

    private void logout() {
        firebaseAuth.signOut();
    }

    /*

    public void UserData(String userName, String image) {

        writerName.setText( userName );
        RequestOptions placeHolder = new RequestOptions();
        placeHolder.placeholder( R.drawable.profile_placeholder );

        Glide.with( WriterProfileActivity.this ).applyDefaultRequestOptions( placeHolder ).load(image).into( writerImage );

    }
    */

}



