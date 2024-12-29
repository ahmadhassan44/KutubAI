package com.example.kutubai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kutubai.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<Book> bookList;
    private RequestQueue requestQueue;
    private String query;

    public SearchFragment(String query) {
        this.query = query;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookList = new ArrayList<>();
        searchAdapter = new SearchAdapter(bookList, new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                // Navigate to BookDetailsFragment and pass selected book details
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layout, new BookDetailsFragment(book));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        recyclerView.setAdapter(searchAdapter);

        // Initialize RequestQueue here
        requestQueue = Volley.newRequestQueue(requireContext());
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("items");
                            bookList.clear();
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

                                bookList.add(new Book(coverURL, title, authors, description, isbn10, isbn13, genres));
                            }
                            searchAdapter.notifyDataSetChanged();
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
        return view;
    }
}
