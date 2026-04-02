package edu.uga.cs.countryquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QuizResultFragment extends Fragment {

    private static final String ARG_SCORE = "score";

    private int score;

    public static QuizResultFragment newInstance(int score) {
        QuizResultFragment fragment = new QuizResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = getArguments().getInt(ARG_SCORE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_result, container, false);

        TextView scoreTextView = view.findViewById(R.id.result_score);
        TextView messageTextView = view.findViewById(R.id.result_message);
        Button closeButton = view.findViewById(R.id.close_button);

        scoreTextView.setText("Your Score: " + score + "/6");

        if (score == 6) {
            messageTextView.setText("Perfect! You're a geography expert!");
        } else if (score >= 4) {
            messageTextView.setText("Great job!");
        } else if (score >= 2) {
            messageTextView.setText("Good effort!");
        } else {
            messageTextView.setText("Keep practicing!");
        }

        closeButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}
