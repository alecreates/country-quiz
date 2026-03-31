package edu.uga.cs.countryquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CountryData {
    public static final String DEBUG_TAG = "CountryData";
    private SQLiteDatabase db;
    private SQLiteOpenHelper countryQuizDbHelper;

    private static final String[] allColumns = {
            CountryQuizDBHelper.COUNTRIES_COLUMN_ID,
            CountryQuizDBHelper.COUNTRIES_COLUMN_NAME,
            CountryQuizDBHelper.COUNTRIES_COLUMN_CAPITAL,
            CountryQuizDBHelper.COUNTRIES_COLUMN_CONTINENT,
            CountryQuizDBHelper.COUNTRIES_COLUMN_CODE
    };

    private static final String[] allQuizColumns = {
            CountryQuizDBHelper.QUIZZES_COLUMN_ID,
            CountryQuizDBHelper.QUIZZES_COLUMN_DATE,
            CountryQuizDBHelper.QUIZZES_COLUMN_RESULT
    };

    public CountryData (Context context ) {
        this.countryQuizDbHelper = CountryQuizDBHelper.getInstance( context );
    }

    // open the database
    public void open() {
        db = countryQuizDbHelper.getWritableDatabase();
        Log.d(DEBUG_TAG, "Database opened");
    }

    // close the database
    public void close() {
        if (countryQuizDbHelper != null) {
            countryQuizDbHelper.close();
            Log.d(DEBUG_TAG, "Database closed");
        }
    }

    public boolean isDBOpen() {
        return db != null && db.isOpen();
    }

    /**
     * Store a new country in the database.
     */
    public Country storeCountry(Country country) {
        ContentValues values = new ContentValues();
        values.put(CountryQuizDBHelper.COUNTRIES_COLUMN_NAME, country.getName());
        values.put(CountryQuizDBHelper.COUNTRIES_COLUMN_CAPITAL, country.getCapital());
        values.put(CountryQuizDBHelper.COUNTRIES_COLUMN_CONTINENT, country.getContinent());
        values.put(CountryQuizDBHelper.COUNTRIES_COLUMN_CODE, country.getCode());

        long id = db.insert(CountryQuizDBHelper.TABLE_COUNTRIES, null, values);
        country.setId(id);
        return country;
    }

    /**
     * Retrieve all countries from the database.
     */
    public List<Country> retrieveAllCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(CountryQuizDBHelper.TABLE_COUNTRIES, allColumns,
                    null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(CountryQuizDBHelper.COUNTRIES_COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndex(CountryQuizDBHelper.COUNTRIES_COLUMN_NAME));
                    String capital = cursor.getString(cursor.getColumnIndex(CountryQuizDBHelper.COUNTRIES_COLUMN_CAPITAL));
                    String continent = cursor.getString(cursor.getColumnIndex(CountryQuizDBHelper.COUNTRIES_COLUMN_CONTINENT));
                    String code = cursor.getString(cursor.getColumnIndex(CountryQuizDBHelper.COUNTRIES_COLUMN_CODE));

                    Country country = new Country(name, capital, continent, code);
                    country.setId(id);
                    countries.add(country);
                }
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return countries;
    }

    /**
     * Retrieve all stored past quizzes from the database.
     */
    public List<Quiz> retrieveAllQuizzes() {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(CountryQuizDBHelper.TABLE_QUIZZES, allQuizColumns,
                    null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(CountryQuizDBHelper.QUIZZES_COLUMN_ID));
                    String date = cursor.getString(cursor.getColumnIndex(CountryQuizDBHelper.QUIZZES_COLUMN_DATE));
                    String result = cursor.getString(cursor.getColumnIndex(CountryQuizDBHelper.QUIZZES_COLUMN_RESULT));

                    Quiz quiz = new Quiz(date, result);
                    quiz.setId(id);
                    quizzes.add(quiz);
                }
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return quizzes;
    }


    /**
     * Check if the countries table is empty.
     */
    public boolean isTableEmpty() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + CountryQuizDBHelper.TABLE_COUNTRIES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }

    /**
     * Store a quiz result.
     */
    public void storeQuizResult(String date, int result) {
        ContentValues values = new ContentValues();
        values.put(CountryQuizDBHelper.QUIZZES_COLUMN_DATE, date);
        values.put(CountryQuizDBHelper.QUIZZES_COLUMN_RESULT, result);
        db.insert(CountryQuizDBHelper.TABLE_QUIZZES, null, values);
    }
}
