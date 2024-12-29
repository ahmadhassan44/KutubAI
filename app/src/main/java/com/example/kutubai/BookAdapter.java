package com.example.kutubai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kutubai.R;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    private OnBookClickListener listener;

    public BookAdapter(Context context, List<Book> bookList, OnBookClickListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_horizontal, parent, false);
        return new BookViewHolder(view, listener, bookList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        if (!bookList.isEmpty()){
            Book book = (Book)bookList.get(position);
            holder.bookTitle.setText(book.getTitle());
            Glide.with(context)
                    .load(book.getCoverURL())
                    .into(holder.bookCover);
        }
        else {
            holder.bookTitle.setText("No books added yet! ");
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView bookCover;
        TextView bookTitle;
        OnBookClickListener listener;
        List<Book> bookList;

        public BookViewHolder(@NonNull View itemView, OnBookClickListener listener, List<Book> bookList) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.book_cover);
            bookTitle = itemView.findViewById(R.id.book_title);
            this.listener = listener;
            this.bookList = bookList;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onBookClick(getAdapterPosition(), bookList);
            }
        }
    }

    public interface OnBookClickListener {
        void onBookClick(int position, List<Book> bookList);
    }
}
