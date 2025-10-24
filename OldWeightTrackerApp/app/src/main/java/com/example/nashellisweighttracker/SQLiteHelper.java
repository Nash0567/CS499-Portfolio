package com.example.nashellisweighttracker;

import android.content.Context;
import android.database.Cursor;  // Ensure this import is present
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "weight_tracker_db";

    // Table Name
    private static final String TABLE_WEIGHT_LOG = "weight_log";

    // Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_DATE = "date";

    // SQL query to create the database table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_WEIGHT_LOG + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WEIGHT + " REAL, " +
                    COLUMN_DATE + " TEXT)";

    // Constructor
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists and create the new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT_LOG);
        onCreate(db);
    }

    // Method to insert data into the database
    public void insertWeight(float weight, String date, SQLiteDatabase db) {
        String insertQuery = "INSERT INTO " + TABLE_WEIGHT_LOG + "(" +
                COLUMN_WEIGHT + ", " + COLUMN_DATE + ") VALUES (" + weight + ", '" + date + "')";
        db.execSQL(insertQuery);
    }

    // Method to read data from the database
    public Cursor getAllWeightEntries(SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + TABLE_WEIGHT_LOG;
        return db.rawQuery(selectQuery, null);  // Returning a Cursor object
    }
}
