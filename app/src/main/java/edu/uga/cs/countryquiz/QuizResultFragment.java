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

/**
 * Fragment that displays the final quiz results.
 * Shows the user's score along with a performance message,
 * and provides an option to close the quiz.
 */
public class QuizResultFragment extends Fragment {

    private static final String ARG_SCORE = "score";

    private int score;

    /**
     * Creates a new instance of QuizResultFragment with the given score.
     *
     * @param score The user's final quiz score
     * @return A new instance of QuizResultFragment with arguments set
     */
    public static QuizResultFragment newInstance(int score) {
        QuizResultFragment fragment = new QuizResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created.
     * Retrieves the score passed in through fragment arguments.
     *
     * @param savedInstanceState Bundle containing previously saved state (if any)
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = getArguments().getInt(ARG_SCORE);
        }
    }

    /**
     * Inflates the fragment layout and initializes UI components.
     * Displays the user's score and a corresponding feedback message,
     * and sets up the close button to exit the quiz.
     *
     * @param inflater LayoutInflater used to inflate the view
     * @param container Parent view that the fragment UI will attach to
     * @param savedInstanceState Bundle containing previously saved state (if any)
     * @return The root view of the fragment layout
     */
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
