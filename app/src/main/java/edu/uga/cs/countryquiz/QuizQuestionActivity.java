package edu.uga.cs.countryquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private Button nextButton;

    private CountryData countryData;
    private List<Country> allCountries;
    private List<Country> quizCountries;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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
        nextButton = findViewById(R.id.next_button);

        countryData = new CountryData(this);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNextButton();
            }
        });

        // Load quiz data asynchronously
        new QuizDataLoader().execute();
    }

    private void handleNextButton() {
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

        // Pick 2 random wrong answers
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

        if (currentQuestionIndex == 5) {
            nextButton.setText("Finish");
        }
    }

    private void finishQuiz() {
        // Save result
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        new QuizResultSaver().execute(currentDate, String.valueOf(score));

        Toast.makeText(this, "Quiz Finished! Score: " + score + "/6", Toast.LENGTH_LONG).show();
        finish();
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

    /**
     * AsyncTask to load countries and set up the quiz.
     */
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

            // Select 6 random countries for the quiz
            quizCountries = new ArrayList<>(allCountries);
            Collections.shuffle(quizCountries);
            quizCountries = quizCountries.subList(0, 6);

            displayQuestion();
        }
    }

    /**
     * AsyncTask to save the quiz result.
     */
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
            Log.d(DEBUG_TAG, "Quiz result saved");
        }
    }
}
