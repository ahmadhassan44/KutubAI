package com.example.kutubai;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    private ImageView profilePic;
    private TextView username;
    private TextView name;
    private TextView email;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize FirebaseAuth and Firestore instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();  // Ensure db is initialized here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = view.findViewById(R.id.profilePic);
        username = view.findViewById(R.id.username);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        Button logoutButton = view.findViewById(R.id.btnLogout);
        Button editProfileButton = view.findViewById(R.id.btnEditProfile);
        editProfileButton.setOnClickListener(v -> {
            // Open EditProfileFragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, EditProfileFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Fetch and display user information
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("user").document(userId);

            registration = userRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Toast.makeText(getActivity(), "Error while loading!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String profilePicUrl = snapshot.getString("profilePic");
                    String usernameText = snapshot.getString("username");
                    String nameText = snapshot.getString("name");
                    String emailText = snapshot.getString("email");

                    // Load profile picture using Glide
                    if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                        Glide.with(this)
                                .load(profilePicUrl)
                                .placeholder(R.drawable.home)
                                .into(profilePic);
                    }

                    username.setText(usernameText);
                    name.setText(nameText);
                    email.setText(emailText);
                }
            });
        }

        // Set up the logout button
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop listening to Firestore updates when the view is destroyed
        if (registration != null) {
            registration.remove();
        }
    }
}
