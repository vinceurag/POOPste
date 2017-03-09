package com.callofnature.poopste.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.callofnature.poopste.NewsFeedFragment;
import com.callofnature.poopste.R;
import com.callofnature.poopste.model.Feed;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vinceurag on 09/03/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private List<Feed> posts;

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView name, post_date, status;
        public ImageView profile_pic, post_photo_url;

        public FeedViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.post_name);
            status = (TextView) view.findViewById(R.id.txtStatusMsg);
            post_date = (TextView) view.findViewById(R.id.post_date);
            profile_pic = (ImageView) view.findViewById(R.id.postProfilePic);
            post_photo_url = (ImageView) view.findViewById(R.id.post_image);
        }
    }

    public FeedAdapter(List<Feed> posts) {
        this.posts = posts;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewTtype) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);

        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        Feed post = posts.get(position);
        holder.name.setText(post.getName());
        holder.status.setText(post.getStatus());
        holder.post_date.setText(post.getPost_date());
        Picasso.with(holder.profile_pic.getContext()).load(post.getProfile_pic()).fit().centerCrop().into(holder.profile_pic);
        if(!post.getPhoto_url().isEmpty() || !post.getPhoto_url().equals("")) {
            Picasso.with(holder.post_photo_url.getContext()).load(post.getPhoto_url()).placeholder(R.drawable.progress_spin).fit().centerCrop().into(holder.post_photo_url);
        } else {
            holder.post_photo_url.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
