package com.example.nashellisweighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeightTracker.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_WEIGHTS = "weights";

    // Columns for users table
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_GOAL_WEIGHT = "goal_weight";

    // Columns for weights table
    private static final String COLUMN_WEIGHT_ID = "id";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_USER_ID_FK = "user_id";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_GOAL_WEIGHT + " REAL DEFAULT 0)";
        db.execSQL(createUsersTable);

        String createWeightsTable = "CREATE TABLE " + TABLE_WEIGHTS + " (" +
                COLUMN_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " +
                TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createWeightsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    // Users CRUD

    public boolean registerUser(String username, String password, float goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?",
                new String[]{username})) {
            if (cursor.getCount() > 0) return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_GOAL_WEIGHT, goalWeight);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password})) {
            return cursor.getCount() > 0;
        }
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username})) {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        }
        return -1;
    }

    public void updateGoalWeight(int userId, float newGoalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_WEIGHT, newGoalWeight);
        db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public float getGoalWeight(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_GOAL_WEIGHT + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) return cursor.getFloat(0);
        }
        return 0;
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COLUMN_USER_ID_FK + "=?", new String[]{String.valueOf(userId)});
        db.delete(TABLE_USERS, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }


    // Weight Entries CRUD

    public boolean insertWeight(int userId, float weight, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_DATE, date);
        long result = db.insert(TABLE_WEIGHTS, null, values);
        return result != -1;
    }

    public Cursor getAllWeightEntries(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_WEIGHTS +
                        " WHERE " + COLUMN_USER_ID_FK + " = ? " +
                        "ORDER BY " + COLUMN_WEIGHT_ID + " ASC",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean updateWeight(int weightId, float newWeight, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT, newWeight);
        values.put(COLUMN_DATE, newDate);
        int rows = db.update(TABLE_WEIGHTS, values, COLUMN_WEIGHT_ID + "=?", new String[]{String.valueOf(weightId)});
        return rows > 0;
    }

    public boolean deleteWeight(int weightId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_WEIGHTS, COLUMN_WEIGHT_ID + "=?", new String[]{String.valueOf(weightId)});
        return rows > 0;
    }

    public boolean deleteAllWeights(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_WEIGHTS, COLUMN_USER_ID_FK + "=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }
}
