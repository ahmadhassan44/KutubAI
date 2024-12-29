package com.example.kutubai;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements BookAdapter.OnBookClickListener{

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private RequestQueue requestQueue;
    private Button recommendButton;
    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private List<Book> recommendedBooks;

    private TextView quoteOfTheDayTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recommendButton = view.findViewById(R.id.recommendButton);
        quoteOfTheDayTextView = view.findViewById(R.id.quoteOfTheDayTextView);

        // Initialize the requestQueue
        requestQueue = Volley.newRequestQueue(requireContext());

        fetchQuoteOfTheDay();

        recommendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecommendation();
//                recyclerViewBooks.setVisibility(View.GONE);
                getNewRecommendations();
                fetchRecommendedBooks();
//                fetchRecommendedBooks();
//                fetchBooksFromFirestore();
//                recyclerViewBooks.setVisibility(View.VISIBLE);


            }
        });

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(requireContext());

        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendedBooks = new ArrayList<>();
//        bookAdapter = new BookAdapter(getContext(), recommendedBooks, new BookAdapter.OnBookClickListener() {
//            @Override
//            public void onBookClick(int position, List<Book> bookList) {
//                // Handle book item click
//                Book clickedBook = bookList.get(position);
//                BookDetailsFragment bookDetailsFragment = new BookDetailsFragment(clickedBook);
//
//            }
//        });
        bookAdapter = new BookAdapter(getContext(), recommendedBooks, this);
        recyclerViewBooks.setAdapter(bookAdapter);
        fetchRecommendedBooks();

        return view;
    }


    private void fetchRecommendedBooks() {
        firestore.collection("recommendations")
                .whereEqualTo("userID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot doc: task.getResult()) {
                                List<String> bookIds= (List<String>) doc.get("bookIDs");
                                for (String id: bookIds){
                                    firestore.collection("books")
                                            .document(id)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        DocumentSnapshot document = task.getResult();
                                                        Book book = document.toObject(Book.class);
//                                Book book = new Book();
                                                        String coverURL = document.getString("coverImageUrl");
                                                        String title = document.getString("title");
                                                        String description = document.getString("description");
                                                        String isbn10 = document.getString("isbn10");
                                                        String isbn13 = document.getString("isbn13");
                                                        book.setCoverURL(coverURL);
                                                        book.setDescription(description);
                                                        book.setTitle(title);
                                                        book.setIsbn10(isbn10);
                                                        book.setIsbn13(isbn13);

                                                        book.setBookID(id);
                                                        Log.d("RECOMMENDATION", id+" URL: "+coverURL);
//                                book.setCoverURL(document.getString("coverURL"));


                                                        if (book != null) {
                                                            recommendedBooks.add(book);
                                                            bookAdapter.notifyDataSetChanged();
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Error fetching book details", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    private void getNewRecommendations() {
        List<String> titles = new ArrayList<>();
        String userId = mAuth.getUid();

        firestore.collection("lists")
                .whereEqualTo("name", "Read")
                .whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                List<String> bookIds = (List<String>) doc.get("books");
                                if (bookIds != null && !bookIds.isEmpty()) {
                                    for (String id : bookIds) {
                                        firestore.collection("books").document(id)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful() && task.getResult() != null) {
                                                            String bookTitle = task.getResult().getString("author"); // Fetch the title, not the author
                                                            if (bookTitle != null) {
                                                                titles.add(bookTitle);
                                                            }
                                                            // Log the titles being added
                                                            Log.d("getNewRecommendations", "Added title: " + bookTitle);
                                                            // Check if all titles have been fetched
                                                            if (titles.size() == bookIds.size()) {
                                                                fetchRecommendations(titles);
                                                            }
                                                        } else {
                                                            Toast.makeText(getContext(), "Error fetching book details", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // Handle empty or null bookIds
                                    Log.d("getNewRecommendations", "No book IDs found in the Read list.");
                                    Toast.makeText(getContext(), "No books found in the Read list.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // Handle unsuccessful task or null result
                            Log.d("getNewRecommendations", "Failed to fetch Read list or no Read list found.");
                            Toast.makeText(getContext(), "Failed to fetch Read list or no Read list found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchRecommendations(List<String> titles) {
        if (titles.isEmpty()) {
            // Handle empty titles case, perhaps notify the user or log an error
            Toast.makeText(getContext(), "No titles found to fetch recommendations", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + android.text.TextUtils.join(",", titles);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("items");
                            List<Map<String, Object>> newBooks = new ArrayList<>();

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                                String title = volumeInfo.optString("title");
                                String authors = volumeInfo.optJSONArray("authors") != null ? volumeInfo.optJSONArray("authors").join(", ") : "Unknown Author";
                                String description = volumeInfo.optString("description", "No description available");

                                // Extract the image URL
                                JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
                                String coverURL = null;
                                if (imageLinks != null) {
                                    coverURL = imageLinks.optString("thumbnail");
                                }

                                // Check if coverURL is null
                                if (coverURL == null) {
                                    coverURL = "https://covers.openlibrary.org/b/id/12547191-L.jpg";
                                }

                                // Extract genres
                                JSONArray categories = volumeInfo.optJSONArray("categories");
                                ArrayList<String> genres = new ArrayList<>();
                                if (categories != null) {
                                    for (int x = 0; x < categories.length(); x++) {
                                        genres.add(categories.get(x).toString());
                                    }
                                }

                                // Extract ISBNs
                                JSONArray identifiers = volumeInfo.optJSONArray("industryIdentifiers");
                                String isbn10 = null;
                                String isbn13 = null;

                                if (identifiers != null) {
                                    for (int j = 0; j < identifiers.length(); j++) {
                                        JSONObject identifier = identifiers.getJSONObject(j);
                                        String type = identifier.optString("type");
                                        String value = identifier.optString("identifier");

                                        if ("ISBN_10".equals(type)) {
                                            isbn10 = value;
                                        } else if ("ISBN_13".equals(type)) {
                                            isbn13 = value;
                                        }
                                    }
                                }

                                Map<String, Object> bookData = new HashMap<>();
                                bookData.put("title", title);
                                bookData.put("author", authors);
                                bookData.put("description", description);
                                bookData.put("coverImageUrl", coverURL);
                                bookData.put("genre", genres);
                                bookData.put("isbn10", isbn10);
                                bookData.put("isbn13", isbn13);

                                newBooks.add(bookData);
                            }
                            checkAndAddBooksToFirestore(newBooks);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }


//    private void fetchRecommendedBooks() {
//        firestore.collection("recommendations")
//                .whereEqualTo("userID", mAuth.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (DocumentSnapshot doc : task.getResult()) {
//                                List<String> bookIds = (List<String>) doc.get("bookIDs");
//                                if (bookIds != null) {
//                                    fetchBooksByIds(bookIds);
//                                }
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "Error fetching recommendations", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//    private void fetchBooksByIds(List<String> bookIds) {
//        for (String bookId : bookIds) {
//            firestore.collection("books")
//                    .document(bookId)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                                DocumentSnapshot document = task.getResult();
//                                Book book = document.toObject(Book.class);
////                                Book book = new Book();
//                                String coverURL = document.getString("coverImageUrl");
//                                String title = document.getString("title");
//                                String description = document.getString("description");
//                                String isbn10 = document.getString("isbn10");
//                                String isbn13 = document.getString("isbn13");
//                                book.setCoverURL(coverURL);
//                                book.setDescription(description);
//                                book.setTitle(title);
//                                book.setIsbn10(isbn10);
//                                book.setIsbn13(isbn13);
//
//                                book.setBookID(bookId);
//                                Log.d("RECOMMENDATION", bookId+" URL: "+coverURL);
////                                book.setCoverURL(document.getString("coverURL"));
//
//
//                                if (book != null) {
//                                    recommendedBooks.add(book);
//                                    bookAdapter.notifyDataSetChanged();
//                                }
//                            } else {
//                                Toast.makeText(getContext(), "Error fetching book details", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//    }
//    private void fetchBooksFromFirestore() {
//        firestore.collection("lists")
//                .whereEqualTo("name", "Read")
//                .whereEqualTo("userID", mAuth.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            List<String> bookIds = new ArrayList<>();
//                            for (DocumentSnapshot document : task.getResult()) {
//                                List<String> books = (List<String>) document.get("books");
//                                if (books != null) {
//                                    bookIds.addAll(books);
//                                }
//                            }
//                            fetchBookTitles(bookIds);
//                        } else {
//                            Toast.makeText(getContext(), "Error fetching lists", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    private void fetchBookTitles(List<String> bookIds) {
//        List<String> bookTitles = new ArrayList<>();
//        for (String bookId : bookIds) {
//            firestore.collection("books").document(bookId)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                                String bookTitle = task.getResult().getString("author");
//                                if (bookTitle != null) {
//                                    bookTitles.add(bookTitle);
//                                }
//                                // Check if we have processed all books
//                                if (bookTitles.size() == bookIds.size()) {
//                                    fetchRecommendations(bookTitles);
//                                }
//                            } else {
//                                Toast.makeText(getContext(), "Error fetching book details", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//    }
//
//    private void fetchRecommendations(List<String> bookTitles) {
//        String url = "https://www.googleapis.com/books/v1/volumes?q=" + android.text.TextUtils.join(",", bookTitles);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray items = response.getJSONArray("items");
//                            List<Map<String, Object>> newBooks = new ArrayList<>();
//
//                            for (int i = 0; i < items.length(); i++) {
//                                JSONObject item = items.getJSONObject(i);
//                                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
//
//                                String title = volumeInfo.optString("title");
//                                String authors = volumeInfo.optJSONArray("authors") != null ? volumeInfo.optJSONArray("authors").join(", ") : "Unknown Author";
//                                String description = volumeInfo.optString("description", "No description available");
//
//                                // Extract the image URL
//                                JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
//                                String coverURL = null;
//                                if (imageLinks != null) {
//                                    coverURL = imageLinks.optString("thumbnail");
//                                }
//
//                                // Check if coverURL is null
//                                if (coverURL == null) {
//                                    coverURL = "https://covers.openlibrary.org/b/id/12547191-L.jpg";
//                                }
//
//                                // Extract genres
//                                JSONArray categories = volumeInfo.optJSONArray("categories");
//                                ArrayList<String> genres = new ArrayList<>();
//                                if (categories != null) {
//                                    for (int x = 0; x < categories.length(); x++) {
//                                        genres.add(categories.get(x).toString());
//                                    }
//                                }
//
//                                // Extract ISBNs
//                                JSONArray identifiers = volumeInfo.optJSONArray("industryIdentifiers");
//                                String isbn10 = null;
//                                String isbn13 = null;
//
//                                if (identifiers != null) {
//                                    for (int j = 0; j < identifiers.length(); j++) {
//                                        JSONObject identifier = identifiers.getJSONObject(j);
//                                        String type = identifier.optString("type");
//                                        String value = identifier.optString("identifier");
//
//                                        if ("ISBN_10".equals(type)) {
//                                            isbn10 = value;
//                                        } else if ("ISBN_13".equals(type)) {
//                                            isbn13 = value;
//                                        }
//                                    }
//                                }
//
//                                Map<String, Object> bookData = new HashMap<>();
//                                bookData.put("title", title);
//                                bookData.put("author", authors);
//                                bookData.put("description", description);
//                                bookData.put("coverImageUrl", coverURL);
//                                bookData.put("genre", genres);
//                                bookData.put("isbn10", isbn10);
//                                bookData.put("isbn13", isbn13);
//
//                                newBooks.add(bookData);
//                            }
//                            checkAndAddBooksToFirestore(newBooks);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//
//        requestQueue.add(request);
//    }

    private void deleteRecommendation() {
        String userId = mAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<String>[] bookIDs = new List[]{new ArrayList<>()};

        // REMOVE ID FROM RECOMMENDATIONS
        db.collection("recommendations")
                .whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if (!documents.isEmpty()) {
                                DocumentReference docRef = documents.get(0).getReference();
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            DocumentSnapshot doc = task.getResult();
                                            List<String> bookIDs = (List<String>) doc.get("bookIDs");
                                            if (bookIDs != null) {
                                                for (String bookID : bookIDs) {
                                                    db.collection("books")
                                                            .document(bookID)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Log.e("RECOMMENDATIONS", bookID + " removed from books");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e("RECOMMENDATIONS", bookID + " failed to remove from books");
                                                                }
                                                            });
                                                }
                                            }
                                            docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> deleteTask) {
                                                    if (deleteTask.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Recommendation deleted successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Error deleting recommendation", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Error fetching recommendation details", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "No recommendation found for this user", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error fetching recommendations", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        //REMOVE BOOK FROM BOOKS
//        for (String bookID: bookIDs[0]){
//            db.collection("books")
//                    .document(bookID)
//                    .delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Log.e("RECOMMENDATIONS", bookID + " removed from books");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e("RECOMMENDATIONS", bookID + " failed to remove from books");
//                        }
//                    });
//
//
//        }
    }
    private void checkAndAddBooksToFirestore(List<Map<String, Object>> newBooks) {
        firestore.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> existingTitles = new ArrayList<>();
                    //CHECK FOR EXISTING TITLES
                    for (DocumentSnapshot document : task.getResult()) {
                        String title = document.getString("title");
                        if (title != null) {
                            existingTitles.add(title);
                        }
                    }
                    //ADD NEW TITLES
                    List<Map<String, Object>> booksToAdd = new ArrayList<>();
                    for (Map<String, Object> bookData : newBooks) {
                        String title = (String) bookData.get("title");
                        if (!existingTitles.contains(title)) {
                            booksToAdd.add(bookData);
                        }
                    }

                    addBooksToFirestore(booksToAdd);
                } else {
                    Toast.makeText(getContext(), "Error fetching existing books", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addBooksToFirestore(List<Map<String, Object>> booksToAdd) {
        List<String> newBookIds = new ArrayList<>();
        for (Map<String, Object> bookData : booksToAdd) {
            firestore.collection("books")
                    .add(bookData)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                newBookIds.add(task.getResult().getId());
                                if (newBookIds.size() == booksToAdd.size()) {
                                    addRecommendation(newBookIds);
//                                    createNewRecommendation(newBookIds);
                                }
                            } else {
                                Toast.makeText(getContext(), "Error adding book to Firestore", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void addRecommendation(List<String> bookIds) {
        // Check if a recommendation already exists for the current user
        firestore.collection("recommendations")
                .whereEqualTo("userID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<DocumentSnapshot> existingRecommendations = task.getResult().getDocuments();
                            if (!existingRecommendations.isEmpty()) {
                                // Recommendation already exists, update it
                                DocumentSnapshot existingRecommendation = existingRecommendations.get(0);
                                existingRecommendation.getReference().update("bookIDs", FieldValue.arrayUnion(bookIds.toArray()), "timestamp", FieldValue.serverTimestamp())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> updateTask) {
                                                if (updateTask.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Recommendation updated", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Error updating recommendation", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // Recommendation does not exist, create a new one
                                createNewRecommendation(bookIds);
                            }
                        } else {
                            Toast.makeText(getContext(), "Error fetching existing recommendations", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNewRecommendation(List<String> bookIds) {
        Map<String, Object> recommendationData = new HashMap<>();
        recommendationData.put("userID", mAuth.getUid());
        recommendationData.put("timestamp", FieldValue.serverTimestamp());
        recommendationData.put("bookIDs", bookIds);

        firestore.collection("recommendations")
                .add(recommendationData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Recommendation saved", Toast.LENGTH_SHORT).show();
                            refreshFragment();
                        } else {
                            Toast.makeText(getContext(), "Error saving recommendation", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onBookClick(int position, List<Book> bookList) {
        // Get the clicked book from the appropriate list
        Book clickedBook = bookList.get(position);

        // Navigate to BookDetailsFragment with the selected book
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new BookDetailsFragment(clickedBook));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchQuoteOfTheDay() {
        String url = "https://favqs.com/api/qotd";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject quoteObject = response.getJSONObject("quote");
                            String quoteText = quoteObject.getString("body");
                            String author = quoteObject.getString("author");

                            // Display the quote in the TextView
                            String quoteOfDay = "\"" + quoteText + "\" - " + author;
                            quoteOfTheDayTextView.setText(quoteOfDay);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void refreshFragment() {
//        getFragmentManager().beginTransaction()
//                .detach(this)
//                .attach(this)
//                .commit();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


}