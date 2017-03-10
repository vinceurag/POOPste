package com.callofnature.poopste.adapters;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.callofnature.poopste.NewsFeedFragment;
import com.callofnature.poopste.R;
import com.callofnature.poopste.helpers.OnLoadMoreListener;
import com.callofnature.poopste.model.Feed;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by vinceurag on 09/03/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter {
    private List<Feed> posts;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;


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

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View view) {
            super(view);

            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }



    public FeedAdapter(List<Feed> posts, RecyclerView recyclerView) {
        this.posts = posts;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                            if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if(onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }

                                loading = true;
                            }
                        }

                        
                    });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewTtype) {

        RecyclerView.ViewHolder vh;

        if(viewTtype == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);

            vh = new FeedViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_progressbar, parent, false);

            vh = new ProgressViewHolder(v);
        }


        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FeedViewHolder) {
            final Feed post = posts.get(position);
            ((FeedViewHolder) holder).post_photo_url.setVisibility(View.VISIBLE);
            ((FeedViewHolder) holder).name.setText(post.getName());
            ((FeedViewHolder) holder).status.setText(post.getStatus());
            ((FeedViewHolder) holder).post_date.setText((CharSequence) post.getDatePosted());
            Picasso.with( ((FeedViewHolder) holder).profile_pic.getContext()).load(post.getProfile_pic()).fit().centerCrop().into( ((FeedViewHolder) holder).profile_pic);
            if(!post.getPhoto_url().equals("")) {
                Picasso.with( ((FeedViewHolder) holder)
                        .post_photo_url.getContext())
                        .load(post.getPhoto_url())
                        .placeholder(R.drawable.progress_spin)
                        .fit()
                        .centerCrop()
                        .tag("feed_images")
                        .into( ((FeedViewHolder) holder).post_photo_url);
                Log.e("pic", "Loaded Picture for: " + post.getStatus());
            } else {
                ((FeedViewHolder) holder).post_photo_url.setVisibility(View.GONE);
                Log.e("pic", "No Picture for: " + post.getStatus());
            }
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }




    }

    public void setLoaded() {
        loading = false;
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return posts.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

}
