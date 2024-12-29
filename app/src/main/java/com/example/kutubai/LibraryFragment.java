//package com.example.goodreads2;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class LibraryFragment extends Fragment {
//
//    private RecyclerView recyclerRead;
//    private RecyclerView recyclerCurrentlyReading;
//    private RecyclerView recyclerWantToRead;
//
//    private BookAdapter readAdapter;
//    private BookAdapter currentlyReadingAdapter;
//    private BookAdapter wantToReadAdapter;
//
//    private List<Book> readList;
//    private List<Book> currentlyReadingList;
//    private List<Book> wantToReadList;
//    private FirebaseFirestore db;
//    private FirebaseAuth mAuth;
//
//    public LibraryFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_library, container, false);
//
//        recyclerRead = view.findViewById(R.id.recycler_read);
//        recyclerCurrentlyReading = view.findViewById(R.id.recycler_currently_reading);
//        recyclerWantToRead = view.findViewById(R.id.recycler_want_to_read);
//
//        setupRecyclerView(recyclerRead);
//        setupRecyclerView(recyclerCurrentlyReading);
//        setupRecyclerView(recyclerWantToRead);
//
//        readList = new ArrayList<>();
//        currentlyReadingList = new ArrayList<>();
//        wantToReadList = new ArrayList<>();
//
//        readAdapter = new BookAdapter(readList);
//        currentlyReadingAdapter = new BookAdapter(currentlyReadingList);
//        wantToReadAdapter = new BookAdapter(wantToReadList);
//
//        recyclerRead.setAdapter(readAdapter);
//        recyclerCurrentlyReading.setAdapter(currentlyReadingAdapter);
//        recyclerWantToRead.setAdapter(wantToReadAdapter);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        loadBooks();
//
//        return view;
//    }
//
//    private void setupRecyclerView(RecyclerView recyclerView) {
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//    }
//
//    private void loadBooks() {
//        String userId = mAuth.getUid();
//        if (userId == null) {
//            // Handle the case where the user is not logged in
//            return;
//        }
//
//        // Load books for the "Read" list
//        db.collection("lists")
//                .whereEqualTo("userID", userId)
//                .whereEqualTo("name", "Read")
//                .get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {;
//                            List<String> bookIds = (List<String>) document.get("books");
//                            if (bookIds != null) {
//                                for (String bookId : bookIds) {
//                                    db.collection("books").document(bookId)
//                                            .get().addOnSuccessListener(bookDoc -> {
//                                                if (bookDoc.exists()) {
//                                                    Book book = bookDoc.toObject(Book.class);
//                                                    readList.add(book);
//                                                    readAdapter.notifyDataSetChanged();
//                                                }
//                                            });
//                                }
//                            }
//                        }
//                    }
//                });
//
//        // Load books for the "Currently Reading" list
//        db.collection("lists")
//                .whereEqualTo("userID", userId)
//                .whereEqualTo("name", "Currently Reading")
//                .get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            List<String> bookIds = (List<String>) document.get("books");
//                            if (bookIds != null) {
//                                for (String bookId : bookIds) {
//                                    db.collection("books").document(bookId)
//                                            .get().addOnSuccessListener(bookDoc -> {
//                                                if (bookDoc.exists()) {
//                                                    Book book = bookDoc.toObject(Book.class);
//                                                    currentlyReadingList.add(book);
//                                                    currentlyReadingAdapter.notifyDataSetChanged();
//                                                }
//                                            });
//                                }
//                            }
//                        }
//                    }
//                });
//
//        // Load books for the "Want to Read" list
//        db.collection("lists")
//                .whereEqualTo("userID", userId)
//                .whereEqualTo("name", "Want to Read")
//                .get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            List<String> bookIds = (List<String>) document.get("books");
//                            if (bookIds != null) {
//                                for (String bookId : bookIds) {
//                                    db.collection("books").document(bookId)
//                                            .get().addOnSuccessListener(bookDoc -> {
//                                                if (bookDoc.exists()) {
//                                                    String title = bookDoc.getString("title");
//                                                    String author = bookDoc.getString("author");
//                                                    String description = bookDoc.getString("description");
//                                                    String coverURL = bookDoc.getString("coverImageURL");
//                                                    String isbn10 = bookDoc.getString("isbn10");
//                                                    String isbn13 = bookDoc.getString("isbn13");
//                                                    ArrayList<String> categories = (ArrayList<String>) document.get("categories");
//
//
////                                                    Book book = bookDoc.toObject(Book.class);
//                                                    Book book = new Book(coverURL, title, author, description, isbn10, isbn13, categories);
//                                                    wantToReadList.add(book);
//                                                    wantToReadAdapter.notifyDataSetChanged();
//                                                }
//                                            });
//                                }
//                            }
//                        }
//                    }
//                });
//    }
//}

package com.example.kutubai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment implements BookAdapter.OnBookClickListener{

    private RecyclerView recyclerRead;
    private RecyclerView recyclerCurrentlyReading;
    private RecyclerView recyclerWantToRead;

    private BookAdapter readAdapter;
    private BookAdapter currentlyReadingAdapter;
    private BookAdapter wantToReadAdapter;

    private List<Book> readList;
    private List<Book> currentlyReadingList;
    private List<Book> wantToReadList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerRead = view.findViewById(R.id.recycler_read);
        recyclerCurrentlyReading = view.findViewById(R.id.recycler_currently_reading);
        recyclerWantToRead = view.findViewById(R.id.recycler_want_to_read);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupRecyclerView(recyclerRead);
        setupRecyclerView(recyclerCurrentlyReading);
        setupRecyclerView(recyclerWantToRead);

        readList = new ArrayList<>();
        currentlyReadingList = new ArrayList<>();
        wantToReadList = new ArrayList<>();

        loadBooks();

        readAdapter = new BookAdapter(getContext(), readList, this);
        currentlyReadingAdapter = new BookAdapter(getContext(), currentlyReadingList, this);
        wantToReadAdapter = new BookAdapter(getContext(), wantToReadList, this);

        recyclerRead.setAdapter(readAdapter);
        recyclerCurrentlyReading.setAdapter(currentlyReadingAdapter);
        recyclerWantToRead.setAdapter(wantToReadAdapter);

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadBooks() {
        String userId = mAuth.getUid();
        if (userId == null) {
            // Handle the case where the user is not logged in
            return;
        }

        // Load books for the "Read" list
        db.collection("lists")
                .whereEqualTo("userID", userId)
                .whereEqualTo("name", "Read")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> bookIds = (List<String>) document.get("books");
                            if (bookIds != null) {
                                for (String bookId : bookIds) {
                                    if (bookId != null) {  // Ensure bookId is not null
                                        db.collection("books").document(bookId)
                                                .get().addOnSuccessListener(bookDoc -> {
                                                    if (bookDoc.exists()) {
                                                        String title = bookDoc.getString("title");
                                                        String author = bookDoc.getString("author");
                                                        String description = bookDoc.getString("description");
                                                        String coverURL = bookDoc.getString("coverImageUrl");
                                                        String isbn10 = bookDoc.getString("isbn10");
                                                        String isbn13 = bookDoc.getString("isbn13");
                                                        ArrayList<String> categories = (ArrayList<String>) bookDoc.get("categories");

                                                        Book book = new Book(bookId, coverURL, title, author, description, isbn10, isbn13, categories);
                                                        readList.add(book);
                                                        readAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                    } else {
                                        // Log or handle the null bookId case
                                        Log.w("LibraryFragment", "Null bookId encountered in 'Read' list.");
                                    }
                                }
                            }
                        }
                    }
                });

        // Load books for the "Currently Reading" list
        db.collection("lists")
                .whereEqualTo("userID", userId)
                .whereEqualTo("name", "Currently Reading")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> bookIds = (List<String>) document.get("books");
                            if (bookIds != null) {
                                for (String bookId : bookIds) {
                                    if (bookId != null) {  // Ensure bookId is not null
                                        db.collection("books").document(bookId)
                                                .get().addOnSuccessListener(bookDoc -> {
                                                    if (bookDoc.exists()) {
                                                        String title = bookDoc.getString("title");
                                                        String author = bookDoc.getString("author");
                                                        String description = bookDoc.getString("description");
                                                        String coverURL = bookDoc.getString("coverImageUrl");
                                                        String isbn10 = bookDoc.getString("isbn10");
                                                        String isbn13 = bookDoc.getString("isbn13");
                                                        ArrayList<String> categories = (ArrayList<String>) bookDoc.get("categories");

                                                        Book book = new Book(bookId, coverURL, title, author, description, isbn10, isbn13, categories);
                                                        currentlyReadingList.add(book);
                                                        currentlyReadingAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                    } else {
                                        // Log or handle the null bookId case
                                        Log.w("LibraryFragment", "Null bookId encountered in 'Currently Reading' list.");
                                    }
                                }
                            }
                        }
                    }
                });

        // Load books for the "Want to Read" list
        db.collection("lists")
                .whereEqualTo("userID", userId)
                .whereEqualTo("name", "Want to Read")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> bookIds = (List<String>) document.get("books");
                            if (bookIds != null) {
                                for (String bookId : bookIds) {
                                    if (bookId != null) {  // Ensure bookId is not null
                                        db.collection("books").document(bookId)
                                                .get().addOnSuccessListener(bookDoc -> {
                                                    if (bookDoc.exists()) {
                                                        String title = bookDoc.getString("title");
                                                        String author = bookDoc.getString("author");
                                                        String description = bookDoc.getString("description");
                                                        String coverURL = bookDoc.getString("coverImageUrl");
                                                        String isbn10 = bookDoc.getString("isbn10");
                                                        String isbn13 = bookDoc.getString("isbn13");
                                                        ArrayList<String> categories = (ArrayList<String>) bookDoc.get("categories");

                                                        Book book = new Book(bookId, coverURL, title, author, description, isbn10, isbn13, categories);
                                                        wantToReadList.add(book);
                                                        wantToReadAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                    } else {
                                        // Log or handle the null bookId case
                                        Log.w("LibraryFragment", "Null bookId encountered in 'Want to Read' list.");
                                    }
                                }
                            }
                        }
                    }
                });
    }
    @Override
    public void onBookClick(int position, List<Book> bookList) {
        // Get the clicked book from the appropriate list
        Book clickedBook = new Book();
        if (bookList == readList){
            clickedBook = readList.get(position);
        } else if (bookList == currentlyReadingList){
            clickedBook = currentlyReadingList.get(position);
        } else if (bookList == wantToReadList) {
            clickedBook = wantToReadList.get(position);
        }
        // For example, if the position is within the readList:

//        try {
//            clickedBook = readList.get(position);
//        } catch (Exception e) {
//            try {
//                clickedBook = currentlyReadingList.get(position);
//            } catch (Exception e2) {
//                clickedBook = wantToReadList.get(position);
//            }
//        }

        // Navigate to BookDetailsFragment with the selected book
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new BookDetailsFragment(clickedBook));
        transaction.addToBackStack(null);
        transaction.commit();
    }
//    @Override
//    public void onBookClick(Book book) {
//        // Get the clicked book from the appropriate list
//
//        // For example, if the position is within the readList:
//        Book clickedBook = readList.get(position);
////        try {
////            clickedBook = readList.get(position);
////        } catch (Exception e) {
////            try {
////                clickedBook = currentlyReadingList.get(position);
////            } catch (Exception e2) {
////                clickedBook = wantToReadList.get(position);
////            }
////        }
//
//        // Navigate to BookDetailsFragment with the selected book
//        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout, new BookDetailsFragment(clickedBook));
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

}
