package com.geekstudios.footballgeekblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
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

public class MainActivity extends AppCompatActivity {

    private AdapterBlogRecycler blogRecyclerAdapter;
    private List<classBlogItem> blog_list;
    private RecyclerView blog_list_view;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        MobileAds.initialize( this, "ca-app-pub-8962788107836417~5631679446" );

        Toolbar toolbar = findViewById( R.id.custom_toolbar_5036 );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( null );

        blog_list = new ArrayList<>();
        blog_list_view = findViewById( R.id.blog_list_view );

        blogRecyclerAdapter = new AdapterBlogRecycler(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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

        Query firstQuery = firebaseFirestore.collection("Blogs")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(3);
        firstQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

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

                } else {
                    Toast.makeText( MainActivity.this, "No Blogs yet!", Toast.LENGTH_SHORT ).show();
                }

            }

        });

    }

    public void loadMorePost(){

            Query nextQuery = firebaseFirestore.collection("Blogs")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        // Toast.makeText( MainActivity.this, "Loading more blogs...", Toast.LENGTH_SHORT ).show();
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                classBlogItem blogPost = doc.getDocument().toObject(classBlogItem.class);
                                blog_list.add(blogPost);

                                blogRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    } else {

                        Toast.makeText( MainActivity.this, "You're at the bottom of the blog list.", Toast.LENGTH_SHORT ).show();

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
                    Toast.makeText(MainActivity.this, "This function is for only our contributing writers.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.main_menu_write_new_blog:
                if(firebaseAuth.getCurrentUser() != null) {
                    sendToNewBlog();
                } else {
                    Toast.makeText(MainActivity.this, "Login first...", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "You are not logged in!", Toast.LENGTH_SHORT).show();
                }

                return true;

            default:
                return false;
        }
    }

    private void sendToNewBlog() {

        Intent newBlogIntent = new Intent( MainActivity.this, NewBlogActivity.class );
        startActivity(newBlogIntent);

    }

    private void sendToSetup() {

        Intent setupIntent = new Intent( MainActivity.this, SetupActivity.class );
        startActivity(setupIntent);

    }

    private void sentToWriteForUs() {

        Intent writeForUsIntent = new Intent( MainActivity.this, WriteForUsActivity.class );
        startActivity(writeForUsIntent);

    }


    private void sentToAboutUs() {

        Intent aboutUsIntent = new Intent( MainActivity.this, AboutUsActivity.class );
        startActivity(aboutUsIntent);

    }

    private void logout() {
        firebaseAuth.signOut();
    }

}



