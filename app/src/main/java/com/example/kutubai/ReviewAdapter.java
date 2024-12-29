package com.example.kutubai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    private Context context;

    public ReviewAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.userNameTextView.setText(review.getUserName());
        holder.reviewTextView.setText(review.getReviewText());
        holder.ratingBar.setRating(review.getRating());
        holder.ratingBar.setEnabled(false);
        Glide.with(context).load(review.getUserProfilePicture()).into(holder.userProfileImageView);
        holder.timestampTextView.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(review.getTimestamp().toDate()));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImageView;
        TextView userNameTextView;
        TextView reviewTextView;
        RatingBar ratingBar;
        TextView timestampTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            userProfileImageView = itemView.findViewById(R.id.image_view_user_profile);
            userNameTextView = itemView.findViewById(R.id.text_view_user_name);
            reviewTextView = itemView.findViewById(R.id.text_view_review);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            timestampTextView = itemView.findViewById(R.id.text_view_timestamp);
        }
    }
}
