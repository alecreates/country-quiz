package edu.uga.cs.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Button takeNewQuizButton;
    Button viewPastQuizzesButton;
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

        takeNewQuizButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream in_s = getAssets().open( "countries_data.csv" );

                    CSVReader reader = new CSVReader( new InputStreamReader( in_s ) );

                    // reading first row for now, testing CSV reading works

                    String[] firstRow = reader.readNext(); // read first line

                    String firstCountry = firstRow[0]; // assuming country is in column 0

                    Intent intent = new Intent(MainActivity.this, QuizQuestionActivity.class);

                    intent.putExtra("country", firstCountry); // pass it

                    startActivity(intent);
                    // put countries in DB here

                } catch(Exception e) {

                }
            }
        });
    }
}