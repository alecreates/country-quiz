package edu.uga.cs.countryquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public CountryData (Context context ) {
        countryQuizDbHelper = CountryQuizDBHelper.getInstance( context );
    }

    // open the database
    public void open() {
        db = countryQuizDbHelper.getWritableDatabase();
    }

    // close the database
    public void close() {
        if (countryQuizDbHelper != null) {
            countryQuizDbHelper.close();
        }
    }

    public boolean isDBOpen() {
        return db.isOpen();
    }

    // Retrieve all job leads and return them as a List.
    // This is how we restore persistent objects stored as rows in the job leads table in the database.
    // For each retrieved row, we create a new JobLead (Java POJO object) instance and add it to the list.

    // public List<Country> retrieveAllCountries() {}



    // Store new country in the DB

    // public Country storeCountry ( Country country) {}



}

