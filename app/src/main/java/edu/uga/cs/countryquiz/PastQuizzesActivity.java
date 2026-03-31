package edu.uga.cs.countryquiz;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PastQuizzesActivity extends AppCompatActivity {

    private TextView noPastQuizzesView;
    private CountryData countryData;
    private List<Quiz> allQuizzes;
    RecyclerView recyclerView;
    QuizAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_past_quizzes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        noPastQuizzesView = findViewById(R.id.noPastQuizzes);

        countryData = new CountryData(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // load past quiz data asynchronously
        new QuizLoader().execute();
    }

    private void displayQuizzes() {
        noPastQuizzesView.setVisibility(View.GONE);
        // sort from new to old
        Collections.sort(allQuizzes, (a, b) -> b.getDate().compareTo(a.getDate()));
        adapter = new QuizAdapter(allQuizzes);
        recyclerView.setAdapter(adapter);
    }

    /**
     * AsyncTask to load past quizzes.
     */
    private class QuizLoader extends AsyncTask<Void, List<Quiz>> {
        @Override
        protected List<Quiz> doInBackground(Void... params) {
            countryData.open();
            allQuizzes = countryData.retrieveAllQuizzes();
            return allQuizzes;
        }
        @Override
        protected void onPostExecute(List<Quiz> quizzes) {
            if (quizzes == null) {
                noPastQuizzesView.setVisibility(View.VISIBLE);
                return;
            }
            displayQuizzes();
        }
    }
}