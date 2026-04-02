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

    private GestureDetector gestureDetector;

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

        // Load quiz data asynchronously
        new QuizDataLoader().execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // this ensures swipes are detected even if children mess up the touches
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

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

    private void displayQuestion() {
        Country currentCountry = quizCountries.get(currentQuestionIndex);
        questionNumber.setText(String.valueOf(currentQuestionIndex + 1));
        countryName.setText(currentCountry.getName());

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

        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));

        answersGroup.clearCheck();
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        if (countryData != null && !countryData.isDBOpen()) {
            countryData.open();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countryData != null) {
            countryData.close();
        }
    }

    private class QuizDataLoader extends AsyncTask<Void, List<Country>> {
        @Override
        protected List<Country> doInBackground(Void... params) {
            countryData.open();
            allCountries = countryData.retrieveAllCountries();
            return allCountries;
        }

        @Override
        protected void onPostExecute(List<Country> countries) {
            if (countries == null || countries.size() < 6) {
                Toast.makeText(QuizQuestionActivity.this, "Not enough data for a quiz!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            quizCountries = new ArrayList<>(allCountries);
            Collections.shuffle(quizCountries);
            quizCountries = quizCountries.subList(0, 6);
            displayQuestion();
        }
    }

    private class QuizResultSaver extends AsyncTask<String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String date = params[0];
            int result = Integer.parseInt(params[1]);
            countryData.open();
            countryData.storeQuizResult(date, result);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Nothing to do after saving result
        }
    }
}
