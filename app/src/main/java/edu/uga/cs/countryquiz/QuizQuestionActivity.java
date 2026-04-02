package edu.uga.cs.countryquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Activity that manages the quiz gameplay.
 * Displays questions, handles user input via swipe gestures,
 * tracks score, and saves results upon completion.
 */
public class QuizQuestionActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "QuizQuestionActivity";
    private TextView questionNumber;
    private TextView countryName;
    private RadioGroup answersGroup;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private View quizLayout;
    private CountryData countryData;
    private List<Country> allCountries;
    private List<Country> quizCountries;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int savedSelectedId = -1;
    private List<List<String>> questionOptions;
    private GestureDetector gestureDetector;

    /**
     * Called when the activity is first created.
     * Initializes UI components, gesture detection, and starts loading quiz data.
     *
     * @param savedInstanceState Bundle containing previously saved state (if any)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.quiz_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        questionNumber = findViewById(R.id.question_number);
        countryName = findViewById(R.id.country_name);
        answersGroup = findViewById(R.id.answers_group);
        option1 = findViewById(R.id.option_1);
        option2 = findViewById(R.id.option_2);
        option3 = findViewById(R.id.option_3);
        quizLayout = findViewById(R.id.quiz_layout);

        countryData = new CountryData(this);
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        // restore data if needed
        if (savedInstanceState != null) {
            // Restore state
            savedSelectedId = savedInstanceState.getInt("selectedAnswerId", -1);
            currentQuestionIndex = savedInstanceState.getInt("currentQuestionIndex");
            score = savedInstanceState.getInt("score");

            quizCountries = (List<Country>) savedInstanceState.getSerializable("quizCountries");

            questionOptions = (List<List<String>>) savedInstanceState.getSerializable("questionOptions");

            // Load all countries again to reshuffle options
            new QuizDataLoader().execute();

        } else {
            // Normal first launch
            new QuizDataLoader().execute();
        }
    }

    /**
     * Saves the current state of the quiz before the activity is destroyed.
     * This allows the quiz to be restored if the app is temporarily interrupted
     * (e.g., screen rotation, app switch, or incoming phone call).
     *
     * The following data is saved:
     * - Current question index
     * - Current score
     * - Selected answer for the current question
     * - List of quiz countries
     * - Pre-generated answer options for each question
     *
     * @param outState Bundle in which to place saved state data
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("currentQuestionIndex", currentQuestionIndex);
        outState.putInt("score", score);

        // Save selected answer
        outState.putInt("selectedAnswerId", answersGroup.getCheckedRadioButtonId());

        // Save quiz countries (must be Serializable or Parcelable)
        outState.putSerializable("quizCountries", new ArrayList<>(quizCountries));

        // save quiz options
        outState.putSerializable("questionOptions", new ArrayList<>(questionOptions));
    }

    /**
     * Handles touch events for the activity and passes them to the gesture detector.
     *
     * @param event The motion event detected
     * @return true if the gesture detector handled the event, otherwise default handling
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    /**
     * Ensures that all touch events are passed to the gesture detector
     * even if child views consume them.
     *
     * @param ev The motion event being dispatched
     * @return true if handled, otherwise passes to superclass
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // this ensures swipes are detected even if children mess up the touches
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Detects swipe gestures. Specifically listens for left swipe
     * to trigger advancing to the next quiz question.
     */
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        /**
         * Detects a fling (swipe) gesture and determines if it qualifies
         * as a left swipe based on distance and velocity thresholds.
         *
         * @param e1 Initial motion event
         * @param e2 Final motion event
         * @param velocityX Velocity along the X axis
         * @param velocityY Velocity along the Y axis
         * @return true if a left swipe is detected, false otherwise
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            float diffX = e2.getX() - e1.getX();
            // left swipe
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    handleSwipeLeft();
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Handles logic when the user swipes left to proceed to the next question.
     * Checks the selected answer, updates score, and advances the quiz.
     */
    private void handleSwipeLeft() {
        int selectedId = answersGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedButton = findViewById(selectedId);
        String selectedAnswer = selectedButton.getText().toString();
        String correctAnswer = quizCountries.get(currentQuestionIndex).getCapital();

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
        }

        currentQuestionIndex++;

        if (currentQuestionIndex < 6) {
            displayQuestion();
        } else {
            finishQuiz();
        }
    }

    /**
     * Displays the current quiz question and its answer choices.
     * Updates the UI with the country name and corresponding options.
     */
    private void displayQuestion() {
        Country currentCountry = quizCountries.get(currentQuestionIndex);
        questionNumber.setText(String.valueOf(currentQuestionIndex + 1));
        countryName.setText(currentCountry.getName());

        List<String> options = questionOptions.get(currentQuestionIndex);

        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));

        answersGroup.clearCheck();

        // Restore selected answer ONLY for the current question
        if (savedSelectedId != -1) {
            answersGroup.check(savedSelectedId);

            // Reset so it doesn't apply to future questions
            savedSelectedId = -1;
        }
    }

    /**
     * Ends the quiz, saves the result to the database, and displays
     * the result fragment with the user's final score.
     */
    private void finishQuiz() {
        // Save result to DB
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        new QuizResultSaver().execute(currentDate, String.valueOf(score));

        // Hide quiz UI
        quizLayout.setVisibility(View.GONE);

        // Show fragment with result
        QuizResultFragment fragment = QuizResultFragment.newInstance(score);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Called when the activity resumes.
     * Reopens the database connection if it was previously closed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (countryData != null && !countryData.isDBOpen()) {
            countryData.open();
        }
    }

    /**
     * Called when the activity is paused.
     * Closes the database connection to release resources.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (countryData != null) {
            countryData.close();
        }
    }

    /**
     * AsyncTask that loads all country data from the database
     * in the background and prepares quiz questions.
     */
    private class QuizDataLoader extends AsyncTask<Void, List<Country>> {
        /**
         * Retrieves all countries from the database in a background thread.
         *
         * @param params No parameters required
         * @return List of all countries
         */
        @Override
        protected List<Country> doInBackground(Void... params) {
            countryData.open();
            allCountries = countryData.retrieveAllCountries();
            return allCountries;
        }

        /**
         * Called after background execution completes.
         * Initializes quiz questions and displays the first question.
         *
         * @param countries List of countries retrieved from the database
         */
        @Override
        protected void onPostExecute(List<Country> countries) {
            if (countries == null || countries.size() < 6) {
                Toast.makeText(QuizQuestionActivity.this, "Not enough data for a quiz!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (quizCountries == null) {
                // First launch only
                quizCountries = new ArrayList<>(allCountries);
                Collections.shuffle(quizCountries);
                quizCountries = quizCountries.subList(0, 6);

                questionOptions = new ArrayList<>();

                // also initialize quiz options
                for (int i = 0; i < quizCountries.size(); i++) {
                    Country currentCountry = quizCountries.get(i);

                    List<String> options = new ArrayList<>();
                    options.add(currentCountry.getCapital());

                    Random random = new Random();
                    while (options.size() < 3) {
                        Country randomCountry = allCountries.get(random.nextInt(allCountries.size()));
                        String randomCapital = randomCountry.getCapital();
                        if (!options.contains(randomCapital)) {
                            options.add(randomCapital);
                        }
                    }

                    Collections.shuffle(options);
                    questionOptions.add(options);
                }
            }

            displayQuestion();
        }
    }

    /**
     * AsyncTask that saves the quiz result (date and score)
     * to the database in the background.
     */
    private class QuizResultSaver extends AsyncTask<String, Void> {
        /**
         * Saves the quiz result to the database.
         *
         * @param params Contains the date and score as strings
         * @return null
         */
        @Override
        protected Void doInBackground(String... params) {
            String date = params[0];
            int result = Integer.parseInt(params[1]);
            countryData.open();
            countryData.storeQuizResult(date, result);
            return null;
        }

        /**
         * Called after the quiz result has been saved.
         * No additional action is required.
         *
         * @param result Void result
         */
        @Override
        protected void onPostExecute(Void result) {
            // Nothing to do after saving result
        }
    }
}
