package com.example.kutubai;

import java.util.ArrayList;

public class Book {
    private String bookID;
    private String coverURL;
    private String title;
    private String authors;
    private String description;
    private String isbn10;
    private String isbn13;
    private ArrayList<String> categories;

    // No-argument constructor required for Firestore
    public Book() {
    }

    // Full-argument constructor
    public Book(String bookID, String coverURL, String title, String authors, String description, String isbn10, String isbn13, ArrayList<String> categories) {
        this.coverURL = coverURL;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.categories = categories;
        this.bookID = bookID;
    }
    public Book(String coverURL, String title, String authors, String description, String isbn10, String isbn13, ArrayList<String> categories) {
        this.coverURL = coverURL;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.categories = categories;
    }

    // Getters and Setters
    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }
}
