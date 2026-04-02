package edu.uga.cs.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Main entry point of the Country Quiz application.
 * Provides options to start a new quiz or view past quiz results.
 * Also initializes the country database from a CSV file if needed.
 */
public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "MainActivity";
    private Button takeNewQuizButton;
    private Button viewPastQuizzesButton;
    private CountryData countryData;

    /**
     * Called when the activity is first created.
     * Initializes UI components, sets up button listeners for navigation,
     * and triggers database initialization if necessary.
     *
     * @param savedInstanceState Bundle containing previously saved state (if any)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        takeNewQuizButton = findViewById(R.id.take_new_quiz);
        viewPastQuizzesButton = findViewById(R.id.view_past_quizzes);

        countryData = new CountryData(this);

        takeNewQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuizQuestionActivity.class);
                startActivity(intent);
            }
        });

        viewPastQuizzesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PastQuizzesActivity.class);
                startActivity(intent);
            }
        });

        // Initialize database from CSV if needed
        new CountryDBWriter().execute();
    }

    /**
     * Called when the activity becomes visible again.
     * Reopens the database connection if it is not already open.
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
     * AsyncTask that populates the database with country data from a CSV file
     * if the database is currently empty.
     */
    private class CountryDBWriter extends AsyncTask<Void, Void> {

        /**
         * Runs in the background to check if the database is empty and,
         * if so, reads country data from a CSV file and stores it in the database.
         *
         * @param params No parameters required
         * @return null
         */
        @Override
        protected Void doInBackground(Void... params) {
            countryData.open();
            if (countryData.isTableEmpty()) {
                Log.d(DEBUG_TAG, "Database is empty, populating from CSV...");
                try {
                    InputStream in_s = getAssets().open("countries_data.csv");
                    CSVReader reader = new CSVReader(new InputStreamReader(in_s));
                    String[] nextRow;
                    
                    // Setup for app's data.
                    while ((nextRow = reader.readNext()) != null) {
                        if (nextRow.length >= 4) {
                            String name = nextRow[0];
                            String capital = nextRow[1];
                            String continent = nextRow[2];
                            String code = nextRow[3];
                            Country country = new Country(name, capital, continent, code);
                            countryData.storeCountry(country);
                        }
                    }
                    Log.d(DEBUG_TAG, "Database population complete.");
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "Error reading CSV: " + e.getMessage());
                }
            } else {
                Log.d(DEBUG_TAG, "Database already populated.");
            }
            return null;
        }

        /**
         * Called after the database initialization completes.
         * Displays a confirmation message to the user.
         *
         * @param result Void result
         */
        @Override
        protected void onPostExecute(Void result) {
            // Database is ready
            Toast.makeText(MainActivity.this, "Database ready", Toast.LENGTH_SHORT).show();
        }
    }
}
