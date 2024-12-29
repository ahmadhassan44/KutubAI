package com.example.kutubai;

import com.google.firebase.Timestamp;

public class Review {
    private String userId;
    private String bookId;
    private String userName;
    private String userProfilePicture;
    private String reviewText;
    private float rating;
    private Timestamp timestamp;

    public Review() {}
    public Review(String userId, String bookId,String userName, String userProfilePicture, String reviewText, Timestamp timestamp, float rating){
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.userName = userName;
        this.userProfilePicture = userProfilePicture;
        this.timestamp = timestamp;
    }

    public float getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getBookId() {
        return bookId;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
