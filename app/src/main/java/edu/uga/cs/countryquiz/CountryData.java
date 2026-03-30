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


    // Retrieve all countries and return them as a List.

    public List<Country> retrieveAllCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        Cursor cursor = null;
        int columnIndex;
        try {
            // Execute the select query and get the Cursor to iterate over the retrieved rows
            cursor = db.query( CountryQuizDBHelper.TABLE_COUNTRIES, allColumns,
                    null, null, null, null, null );

            // collect all job leads into a List
            if( cursor != null && cursor.getCount() > 0 ) {

                while( cursor.moveToNext() ) {

                    if( cursor.getColumnCount() >= 5) {

                        // get all attribute values of this job lead
                        columnIndex = cursor.getColumnIndex( CountryQuizDBHelper.COUNTRIES_COLUMN_ID );
                        long id = cursor.getLong( columnIndex );
                        columnIndex = cursor.getColumnIndex( CountryQuizDBHelper.COUNTRIES_COLUMN_NAME );
                        String name = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( CountryQuizDBHelper.COUNTRIES_COLUMN_CAPITAL );
                        String capital = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( CountryQuizDBHelper.COUNTRIES_COLUMN_CONTINENT );
                        String continent = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( CountryQuizDBHelper.COUNTRIES_COLUMN_CODE );
                        String code = cursor.getString( columnIndex );

                        // create a new JobLead object and set its state to the retrieved values
                        Country country = new Country( name, capital, continent, code );
                        country.setId(id); // set the id (the primary key) of this object
                        // add it to the list
                        countries.add( country );
                        Log.d(DEBUG_TAG, "Retrieved JobLead: " + country);
                    }
                }
            }
            if( cursor != null )
                Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount() );
            else
                Log.d( DEBUG_TAG, "Number of records from DB: 0" );
        }
        catch( Exception e ){
            Log.d( DEBUG_TAG, "Exception caught: " + e );
        }
        finally{
            // we should close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        // return a list of retrieved job leads
        return countries;
    }

    // Store a new country in the DB
    public Country storeCountry ( Country country ) {
        ContentValues values = new ContentValues();
        values.put( CountryQuizDBHelper.COUNTRIES_COLUMN_NAME, country.getCountryName());
        values.put( CountryQuizDBHelper.COUNTRIES_COLUMN_CAPITAL, country.getCapital());
        values.put( CountryQuizDBHelper.COUNTRIES_COLUMN_CONTINENT, country.getContinent());
        values.put( CountryQuizDBHelper.COUNTRIES_COLUMN_CODE, country.getCode());

        // Insert the new row into the database table;
        long id = db.insert( CountryQuizDBHelper.TABLE_COUNTRIES, null, values );

        // store id in country instance
        country.setId( id );

        return country;
    }



}

