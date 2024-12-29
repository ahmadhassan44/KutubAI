package com.example.kutubai;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ReviewDialogFragment extends DialogFragment {

    private OnReviewSubmittedListener mListener;

    public interface OnReviewSubmittedListener {
        void onReviewSubmitted(String reviewText);
    }

    public void setOnReviewSubmittedListener(OnReviewSubmittedListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_review_dialog, null);
        final EditText reviewEditText = view.findViewById(R.id.edit_text_review);

        builder.setView(view)
                .setTitle("Add Review")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String reviewText = reviewEditText.getText().toString();
                    if (mListener != null) {
                        mListener.onReviewSubmitted(reviewText);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        return builder.create();
    }
}
