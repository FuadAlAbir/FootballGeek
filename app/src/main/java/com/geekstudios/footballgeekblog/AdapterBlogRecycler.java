package com.geekstudios.footballgeekblog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterBlogRecycler extends RecyclerView.Adapter<AdapterBlogRecycler.ViewHolder> {

    private List<classBlogItem> blogList;
    private Context context;

    private FirebaseFirestore firebaseFirestore;


    public AdapterBlogRecycler(List<classBlogItem> blogList) {
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_blog, parent, false );
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( context, OpenBlogActivity.class );
                i.putExtra( "blogTitle", blogList.get( position ).getTitle() );
                i.putExtra( "blogImage", blogList.get( position ).getImage_url() );
                i.putExtra( "blogDesc", blogList.get( position ).getDesc() );
                i.putExtra( "userID", blogList.get( position ).getUser_id() );
                i.putExtra( "claps_counter", blogList.get( position ).getClap_counter() );

                context.startActivity( i );
            }
        } );

        String title_data = blogList.get( position ).getTitle();
        holder.setTitleText( title_data );

        String image_url = blogList.get( position ).getImage_url();
        holder.setBlogImage( image_url );

    }


    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView titleView;
        private ImageView blogImage;


        public ViewHolder(View itemView) {
            super( itemView );
            mView = itemView;
        }


        public void setTitleText (String titleText) {

            titleView = mView.findViewById( R.id.blog_heading_text );
            titleView.setText( titleText );

        }

        public void setBlogImage (String downloadURI) {

            blogImage = mView.findViewById( R.id.blog_feature_image_view );
            RequestOptions blogImageRequest = new RequestOptions();
            blogImageRequest.placeholder( R.drawable.image_placeholder );
            Glide.with( context ).applyDefaultRequestOptions( blogImageRequest ).load( downloadURI ).into( blogImage );

        }

    }
}