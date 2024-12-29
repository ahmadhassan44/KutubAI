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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookDetailsFragment extends Fragment {

    private Book book;
    private String bookId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button wantToReadButton, readButton, currentlyReadingButton, addReviewButton, submitRatingButton, clearButton;
    private RatingBar ratingBar;
    private List<Review> reviewList = new ArrayList<>();
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;


    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public BookDetailsFragment(Book book) {
        this.book = book;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        // Find views by their IDs
        ImageView coverImageView = view.findViewById(R.id.image_view_cover);
        TextView titleTextView = view.findViewById(R.id.text_view_title);
        TextView authorsTextView = view.findViewById(R.id.text_view_authors);
        TextView descriptionTextView = view.findViewById(R.id.text_view_description);
        TextView isbnTextView = view.findViewById(R.id.text_view_isbn);
        wantToReadButton = view.findViewById(R.id.btn_set_as_want_to_read);
        readButton = view.findViewById(R.id.btn_set_as_read);
        currentlyReadingButton = view.findViewById(R.id.btn_set_as_currently_reading);
        addReviewButton = view.findViewById(R.id.btn_add_review);
        ratingBar = view.findViewById(R.id.rating_bar);
        submitRatingButton = view.findViewById(R.id.btn_submit_rating);
        clearButton = view.findViewById(R.id.btn_clear);

        ratingBar.setVisibility(View.GONE);
        submitRatingButton.setVisibility(View.GONE);
        addReviewButton.setVisibility(View.GONE);

        recyclerViewReviews = view.findViewById(R.id.recycler_view_reviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new ReviewAdapter(reviewList, getContext());
        recyclerViewReviews.setAdapter(reviewAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Set values to views
        if (book != null) {
            // Load cover image
            if (book.getCoverURL() != null) {
                Log.e("Error", book.getCoverURL());
                Glide.with(this)
                        .load(book.getCoverURL())
                        .override(150, 200)
                        .into(coverImageView);
            }

            titleTextView.setText(book.getTitle());
            authorsTextView.setText(book.getAuthors());
            descriptionTextView.setText(book.getDescription());
            isbnTextView.setText(book.getIsbn10());

            if (book.getBookID() == null){
                addBookToBooks(book);
            }

//            checkExistingBook(book, exists -> {
//                if (!exists) {
//
//                }
//            });
//            addBookToBooks(book);


            checkBookInList("Want to Read", book.getBookID(), existsInList -> {
                if (existsInList) {
                    Log.d("Want to Read", "TRUE for Want to Read "+book.getBookID());
                    wantToReadButton.setEnabled(false);
                    wantToReadButton.setText("Want to Read ✔");
                    wantToReadButton.setBackgroundColor(getResources().getColor(R.color.background));
                }
                else {
                    Log.d("Want to Read", "FALSE for Want to Read "+book.getBookID());
                }
            });

            checkBookInList("Currently Reading", book.getBookID(), existsInList -> {
                if (existsInList) {
                    Log.d("Currently Reading", "TRUE for Currently Reading "+book.getBookID());
                    currentlyReadingButton.setEnabled(false);
                    wantToReadButton.setEnabled(false);
                    currentlyReadingButton.setText("Currently Reading ✔");
                    wantToReadButton.setBackgroundColor(getResources().getColor(R.color.background));
                    currentlyReadingButton.setBackgroundColor(getResources().getColor(R.color.background));
                }
                else {
                    Log.d("Currently Reading", "FALSE for Currently Reading "+book.getBookID());
                }
            });

            checkBookInList("Read", book.getBookID(), existsInList -> {
                if (existsInList) {
                    Log.d("Read", "TRUE for Read "+book.getBookID());
                    readButton.setEnabled(false);
                    wantToReadButton.setEnabled(false);
                    currentlyReadingButton.setEnabled(false);
                    readButton.setText("Read ✔");
                    readButton.setBackgroundColor(getResources().getColor(R.color.background));
                    wantToReadButton.setBackgroundColor(getResources().getColor(R.color.background));
                    currentlyReadingButton.setBackgroundColor(getResources().getColor(R.color.background));

                    ratingBar.setVisibility(View.VISIBLE);
                    addReviewButton.setVisibility(View.VISIBLE);
                    submitRatingButton.setVisibility(View.VISIBLE);
//                    //SETTING THE RATING BAR IF ALREADY RATED BY USER
                    checkAndSetUserRating(book.getBookID()); // Call the new method here
                }
                else {
                    Log.d("Read", "FALSE for Read "+book.getBookID());
                }
            });

            wantToReadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBookToBooks(book);
                    addBookToList("Want to Read", book.getBookID());
                    removeFromRecommendations();
                    wantToReadButton.setEnabled(false);
                    wantToReadButton.setText("Want to Read ✔");
                    wantToReadButton.setBackgroundColor(getResources().getColor(R.color.background));
                }
            });

            readButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBookToBooks(book);
                    addBookToList("Read", book.getBookID());
//                    if (book.getBookID() != null){
//                        addBookToList("Read", book.getBookID());
//                    }
//                    else {
//                        addBookToList("Read", bookId);
//                    }
                    readButton.setEnabled(false);
                    wantToReadButton.setEnabled(false);
                    currentlyReadingButton.setEnabled(false);
                    readButton.setText("Read ✔");
                    readButton.setBackgroundColor(getResources().getColor(R.color.background));
                    wantToReadButton.setBackgroundColor(getResources().getColor(R.color.background));
                    currentlyReadingButton.setBackgroundColor(getResources().getColor(R.color.background));

                    ratingBar.setVisibility(View.VISIBLE);
                    addReviewButton.setVisibility(View.VISIBLE);
                    submitRatingButton.setVisibility(View.VISIBLE);

                    removeBookFromList("Want to Read", book.getBookID());
                    removeBookFromList("Currently Reading", book.getBookID());
                    removeFromRecommendations();
                }
            });

            submitRatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //SETTING THE RATING BAR IF ALREADY RATED BY USER
                    saveRating(book.getBookID(), mAuth.getUid(), ratingBar.getRating());
                    checkAndSetUserRating(bookId);

                    submitRatingButton.setEnabled(false);
                    refreshFragment();
                }
            });

            currentlyReadingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBookToBooks(book);
                    addBookToList("Currently Reading", book.getBookID());
                    currentlyReadingButton.setEnabled(false);
                    wantToReadButton.setEnabled(false);
                    currentlyReadingButton.setText("Currently Reading ✔");
                    wantToReadButton.setBackgroundColor(getResources().getColor(R.color.background));
                    currentlyReadingButton.setBackgroundColor(getResources().getColor(R.color.background));
                    removeBookFromList("Want to Read", book.getBookID());
                    removeFromRecommendations();
                }
            });

            addReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open a dialog or activity for adding a review
                    openReviewDialog();
                }
            });

            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeBookFromList("Read", book.getBookID());
                    removeBookFromList("Want to Read", book.getBookID());
                    removeBookFromList("Currently Reading", book.getBookID());

                    removeFromRecommendations();

                    ratingBar.setVisibility(View.GONE);
                    addReviewButton.setVisibility(View.GONE);
                    submitRatingButton.setVisibility(View.GONE);

                    wantToReadButton.setEnabled(true);
                    readButton.setEnabled(true);
                    currentlyReadingButton.setEnabled(true);

                    wantToReadButton.setBackgroundColor(getResources().getColor(R.color.primary));
                    currentlyReadingButton.setBackgroundColor(getResources().getColor(R.color.primary));
                    readButton.setBackgroundColor(getResources().getColor(R.color.primary));

                    wantToReadButton.setText("Want to Read");
                    readButton.setText("Read");
                    currentlyReadingButton.setText("Currently Reading");
                }
            });
            fetchReviews();
        }
        return view;
    }

    public interface OnCheckBookInListListener {
        void onCheckBookInList(boolean exists);
    }

    public interface OnCheckExistingBookListener {
        void onCheckExistingBook(boolean exists);
    }

    private void checkBookInList(String listName, String bookId, OnCheckBookInListListener listener) {
        db.collection("lists")
                .whereEqualTo("userID", mAuth.getUid())
                .whereEqualTo("name", listName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> books = (ArrayList<String>) document.get("books");
                                if (books != null && books.contains(bookId)) {
                                    listener.onCheckBookInList(true);
                                    Log.d("checkBookInList", "True for "+bookId+" in "+document.getId());
                                    return;
                                }
                            }
                        }
                        listener.onCheckBookInList(false);
                        Log.d("HE", "False for "+bookId);

                    } else {
                        listener.onCheckBookInList(false);
                        Log.d("HE", "False");
                    }
                });
    }

    private void checkExistingBook(Book book, OnCheckExistingBookListener listener) {
        db.collection("books")
                .whereEqualTo("title", book.getTitle())
                .whereEqualTo("author", book.getAuthors())
                .whereEqualTo("coverImageUrl", book.getCoverURL())
                .whereEqualTo("description", book.getDescription())
//                .whereEqualTo("genre", book.getCategories())
//                .whereEqualTo("isbn10", book.getIsbn10())
//                .whereEqualTo("isbn13", book.getIsbn13())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Check", "The book "+document.getId()+" already in books");
                                bookId = document.getId();
                                book.setBookID(bookId);
//                                book.setCoverURL();
                            }
                            listener.onCheckExistingBook(true);
                        } else {
                            listener.onCheckExistingBook(false);
                        }
                    } else {
                        listener.onCheckExistingBook(false);
                        Log.d("BookDetailsFragment", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void addBookToBooks(Book book) {
        checkExistingBook(book, exists -> {
            if (!exists) {
                Map<String, Object> bookMap = new HashMap<>();
                bookMap.put("title", book.getTitle());
                bookMap.put("author", book.getAuthors());
                bookMap.put("coverImageUrl", book.getCoverURL());
                bookMap.put("description", book.getDescription());
                bookMap.put("genre", book.getCategories());
                bookMap.put("isbn10", book.getIsbn10());
                bookMap.put("isbn13", book.getIsbn13());

                db.collection("books")
                        .add(bookMap)
                        .addOnSuccessListener(documentReference -> {
                            bookId = documentReference.getId();
                            book.setBookID(bookId);
                        })
                        .addOnFailureListener(e -> Log.w("BookDetailsFragment", "Error adding document", e));
            }
        });
    }

    private void addBookToList(String listName, String bookId) {
        db.collection("lists")
                .whereEqualTo("userID", mAuth.getUid())
                .whereEqualTo("name", listName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference listRef = document.getReference();
                                listRef.update("books", FieldValue.arrayUnion(bookId))
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Book added to " + listName + " list", Toast.LENGTH_SHORT).show();
                                            if (listName.equals("Want to Read")) {
                                                updateWantToReadButton(true, false);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Failed to add book to " + listName + " list", Toast.LENGTH_SHORT).show();
                                            Log.w("BookDetailsFragment", "Error updating document", e);
                                        });
                            }
                        } else {
                            Toast.makeText(getContext(), listName + " list not found for user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("BookDetailsFragment", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateWantToReadButton(boolean inWantToReadList, boolean inReadList) {
        if (inReadList) {
            wantToReadButton.setEnabled(false);
            wantToReadButton.setText("Already Read");
            wantToReadButton.setBackgroundColor(getResources().getColor(R.color.purple_200));
            removeBookFromList("Want to Read", bookId);
        } else if (inWantToReadList) {
            wantToReadButton.setEnabled(false);
            wantToReadButton.setText("Want to Read ✔");
            wantToReadButton.setBackgroundColor(getResources().getColor(R.color.background));
        } else {
            wantToReadButton.setEnabled(true);
            wantToReadButton.setText("Want to Read");
            wantToReadButton.setBackgroundColor(getResources().getColor(R.color.purple_500));
        }
    }

    private void removeBookFromList(String listName, String bookId) {
        db.collection("lists")
                .whereEqualTo("userID", mAuth.getUid())
                .whereEqualTo("name", listName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference listRef = document.getReference();
                                listRef.update("books", FieldValue.arrayRemove(bookId))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("BookDetailsFragment", "Book removed from " + listName + " list");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("BookDetailsFragment", "Error updating document", e);
                                        });
                            }
                        }
                    } else {
                        Log.d("BookDetailsFragment", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void saveRating(String bookId, String userId, float ratingValue) {
        // Check if a rating with the same userID and bookID already exists
        db.collection("ratings")
                .whereEqualTo("userID", userId)
                .whereEqualTo("bookID", bookId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // If a rating already exists, update its value
                            String ratingId = document.getId();
                            db.collection("ratings")
                                    .document(ratingId)
                                    .update("rating", ratingValue)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("BookDetailsFragment", "Rating updated successfully");
                                        Toast.makeText(getContext(), "Rating updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("BookDetailsFragment", "Error updating rating", e);
                                        Toast.makeText(getContext(), "Failed to update rating", Toast.LENGTH_SHORT).show();
                                    });
                            return; // Exit the loop after updating the first matching rating
                        }
                        // If no matching rating found, create a new one
                        createNewRating(bookId, userId, ratingValue);
                    } else {
                        Log.w("BookDetailsFragment", "Error getting ratings", task.getException());
                        Toast.makeText(getContext(), "Failed to get ratings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewRating(String bookId, String userId, float ratingValue) {
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("bookID", bookId);
        ratingMap.put("userID", userId);
        ratingMap.put("timestamp", FieldValue.serverTimestamp());
        ratingMap.put("rating", ratingValue);

        db.collection("ratings")
                .add(ratingMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("BookDetailsFragment", "New rating added successfully");
                    Toast.makeText(getContext(), "New rating added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("BookDetailsFragment", "Error adding new rating", e);
                    Toast.makeText(getContext(), "Failed to add new rating", Toast.LENGTH_SHORT).show();
                });
    }


    private void checkAndSetUserRating(String bookId) {
        db.collection("ratings")
                .whereEqualTo("userID", mAuth.getUid())
                .whereEqualTo("bookID", bookId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot ratingDoc : task.getResult()) {
                            float rating = ratingDoc.getDouble("rating").floatValue();
                            ratingBar.setRating(rating);
                            ratingBar.setVisibility(View.VISIBLE);
                            Log.d("Rating", "Rating set: " + rating);
//                            refreshFragment();

                        }
                    } else {
                        Log.w("Rating", "Error getting rating: ", task.getException());
                    }
                });
    }


    // Method to open a dialog for adding a review
    private void openReviewDialog() {
        ReviewDialogFragment dialogFragment = new ReviewDialogFragment();
        dialogFragment.setOnReviewSubmittedListener(new ReviewDialogFragment.OnReviewSubmittedListener() {
            @Override
            public void onReviewSubmitted(String reviewText) {
                // Save the review to the database
                saveReview(reviewText);
            }
        });
        dialogFragment.show(getFragmentManager(), "ReviewDialogFragment");
    }

    // Method to save the review to the database
    private void saveReview(String reviewText) {
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("bookID", book.getBookID());
        reviewMap.put("userID", mAuth.getUid());
        reviewMap.put("timestamp", FieldValue.serverTimestamp());
        reviewMap.put("review", reviewText);

        db.collection("reviews")
                .add(reviewMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("BookDetailsFragment", "Review added successfully");
                    Toast.makeText(getContext(), "Review added successfully", Toast.LENGTH_SHORT).show();
                    refreshFragment();

                })
                .addOnFailureListener(e -> {
                    Log.w("BookDetailsFragment", "Error adding review", e);
                    Toast.makeText(getContext(), "Failed to add review", Toast.LENGTH_SHORT).show();
                });
    }
    private void removeFromRecommendations(){
        db.collection("recommendations")
                .whereEqualTo("userID", mAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference listRef = document.getReference();
                                listRef.update("bookIDs", FieldValue.arrayRemove(book.getBookID()))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("RemoveFromRecommendations", "Book removed from recommendations");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("RemoveFromRecommendations", "Error updating document", e);
                                        });
                            }
                        }
                    } else {
                        Log.d("RemoveFromRecommendations", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void fetchReviews() {
        db.collection("reviews")
                .whereEqualTo("bookID", book.getBookID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Review review = new Review();
                            String userID = document.getString("userID");
                            review.setBookId(book.getBookID());
                            review.setUserId(userID);
                            review.setReviewText(document.getString("review"));
                            review.setTimestamp(document.getTimestamp("timestamp"));

                            // Fetch user information
                            db.collection("user").document(userID).get()
                                    .addOnCompleteListener(userTask -> {
                                        if (userTask.isSuccessful()) {
                                            DocumentSnapshot userDocument = userTask.getResult();
                                            if (userDocument.exists()) {
                                                review.setUserName(userDocument.getString("username"));
                                                review.setUserProfilePicture(userDocument.getString("profilePic"));
                                            }

                                            // Fetch rating information
                                            db.collection("ratings")
                                                    .whereEqualTo("bookID", book.getBookID())
                                                    .whereEqualTo("userID", userID)
                                                    .get()
                                                    .addOnCompleteListener(ratingTask -> {
                                                        if (ratingTask.isSuccessful()) {
                                                            for (QueryDocumentSnapshot ratingDoc : ratingTask.getResult()) {
                                                                review.setRating(ratingDoc.getDouble("rating").floatValue());
                                                            }
                                                            reviewList.add(review);
                                                            reviewAdapter.notifyDataSetChanged();
                                                            Log.d("ReviewDetails", "User: " + review.getUserName() + ", Rating: " + review.getRating());
                                                        } else {
                                                            Log.w("BookDetailsFragment", "Error getting ratings.", ratingTask.getException());
                                                        }
                                                    });
                                        } else {
                                            Log.w("BookDetailsFragment", "Error getting user details.", userTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.w("BookDetailsFragment", "Error getting reviews.", task.getException());
                    }
                });
    }

    private void refreshFragment() {
//        getFragmentManager().beginTransaction()
//                .detach(this)
//                .attach(this)
//                .commit();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new BookDetailsFragment(book));
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
