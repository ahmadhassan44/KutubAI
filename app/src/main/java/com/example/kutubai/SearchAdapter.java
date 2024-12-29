// SearchAdapter.java
package com.example.kutubai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Book> bookList;
    private OnItemClickListener listener; // Interface to handle item click events

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public SearchAdapter(List<Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(book);
                }
            }
        });

        holder.titleTextView.setText(book.getTitle());
        holder.authorsTextView.setText(book.getAuthors());
//        holder.descriptionTextView.setText(book.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(book.getCoverURL())
                .into(holder.coverImageView);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView authorsTextView;
//        TextView descriptionTextView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.image_view_cover);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            authorsTextView = itemView.findViewById(R.id.text_view_authors);
//            descriptionTextView = itemView.findViewById(R.id.text_view_description);
        }
    }
}
