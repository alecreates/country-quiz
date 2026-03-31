package edu.uga.cs.countryquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CountryQuizDBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "CountryQuizDBHelper";
    private static final String DB_NAME = "countries.db";
    private static final int DB_VERSION = 1;

    // Table names
    public static final String TABLE_COUNTRIES = "countries";
    public static final String TABLE_QUIZZES = "quizzes";

    // Countries table columns
    public static final String COUNTRIES_COLUMN_ID = "_id";
    public static final String COUNTRIES_COLUMN_NAME = "name";
    public static final String COUNTRIES_COLUMN_CAPITAL = "capital";
    public static final String COUNTRIES_COLUMN_CONTINENT = "continent";
    public static final String COUNTRIES_COLUMN_CODE = "code";

    // Quizzes table columns (storing results)
    public static final String QUIZZES_COLUMN_ID = "_id";
    public static final String QUIZZES_COLUMN_DATE = "quiz_date";
    public static final String QUIZZES_COLUMN_RESULT = "quiz_result";

    private static CountryQuizDBHelper helperInstance;

    // SQL to create countries table
    private static final String CREATE_COUNTRIES =
            "create table " + TABLE_COUNTRIES + " ("
                    + COUNTRIES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COUNTRIES_COLUMN_NAME + " TEXT, "
                    + COUNTRIES_COLUMN_CAPITAL + " TEXT, "
                    + COUNTRIES_COLUMN_CONTINENT + " TEXT, "
                    + COUNTRIES_COLUMN_CODE + " TEXT"
                    + ")";

    // SQL to create quizzes table
    private static final String CREATE_QUIZZES =
            "create table " + TABLE_QUIZZES + " ("
                    + QUIZZES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUIZZES_COLUMN_DATE + " TEXT, "
                    + QUIZZES_COLUMN_RESULT + " INTEGER"
                    + ")";

    private CountryQuizDBHelper( Context context ) {
        super( context, DB_NAME, null, DB_VERSION );
    }

    public synchronized static CountryQuizDBHelper getInstance( Context context ) {
        if( helperInstance == null ) {
            helperInstance = new CountryQuizDBHelper( context.getApplicationContext() );
        }
        return helperInstance;
    }

    @Override
    public void onCreate( SQLiteDatabase db) {
        db.execSQL(CREATE_COUNTRIES);
        db.execSQL(CREATE_QUIZZES);
        Log.d( DEBUG_TAG, "Tables created" );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "drop table if exists " + TABLE_COUNTRIES );
        db.execSQL( "drop table if exists " + TABLE_QUIZZES );
        onCreate( db );
        Log.d( DEBUG_TAG, "Tables upgraded" );
    }
}
