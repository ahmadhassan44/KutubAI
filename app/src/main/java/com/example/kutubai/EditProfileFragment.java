package com.example.kutubai;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.kutubai.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText editUsername;
    private EditText editName;
    private EditText editEmail;
    private Button btnSave;
    private Button btnSelectImage;

    private static final int PICK_IMAGE_REQUEST = 1;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FirebaseAuth and Firestore instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        editUsername = view.findViewById(R.id.editUsername);
        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        btnSave = view.findViewById(R.id.btnSave);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);

        // Set up the button to select an image from device
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Fetch and display current user information
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("user").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String currentUsername = documentSnapshot.getString("username");
                    String currentName = documentSnapshot.getString("name");
                    String currentEmail = documentSnapshot.getString("email");

                    editUsername.setText(currentUsername);
                    editName.setText(currentName);
                    editEmail.setText(currentEmail);
                }
            });
        }

        // Save button click listener
        btnSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newName = editName.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(newName) || TextUtils.isEmpty(newEmail)) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserProfile(newUsername, newName, newEmail);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI from the intent
            Uri imageUri = data.getData();

            uploadImageToFirebaseStorage(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            StorageReference profilePicRef = FirebaseStorage.getInstance()
                    .getReference("profile_pictures")
                    .child(currentUser.getUid() + ".jpg");

            // Upload file to Firebase Storage
            profilePicRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update user's profile with new profile picture URL
                            String profilePicUrl = uri.toString();
                            updateUserProfileWithNewPic(profilePicUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUserProfileWithNewPic(String profilePicUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("user").document(userId);

            userRef.update("profilePic", profilePicUrl)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                        // Update the profile picture ImageView with the new image
                        // If you have a profile picture ImageView in the layout, update it here
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update profile picture", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateUserProfile(String newUsername, String newName, String newEmail) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("user").document(userId);

            userRef.update("username", newUsername, "name", newName, "email", newEmail)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show());
        }
    }
}
