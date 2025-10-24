package com.example.nashellisweighttracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightLogActivity extends AppCompatActivity {

    private TextView tvGoalWeight;
    private RecyclerView rvWeightEntries;
    private FloatingActionButton fabAddWeight;
    private WeightLogAdapter adapter;
    private List<String> weightEntries;
    private int goalWeight = 170; // Default
    private static final int REQUEST_SMS_PERMISSION = 101;

    private SQLiteHelper dbHelper;
    private String username;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);

        // Initialize DB helper and UI components
        dbHelper = new SQLiteHelper(this);
        tvGoalWeight = findViewById(R.id.tvGoalWeight);
        rvWeightEntries = findViewById(R.id.rvWeightEntries);
        fabAddWeight = findViewById(R.id.fabAddWeight);

        // Get logged-in username
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Error: No user found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch userId from DB
        userId = dbHelper.getUserId(username);

        // Load goal weight
        goalWeight = (int) dbHelper.getGoalWeight(userId);
        updateGoalWeightText();

        // Load previous weight entries
        weightEntries = new ArrayList<>();
        Cursor cursor = dbHelper.getAllWeightEntries(userId);

        // Ensures weights always load in same order as they were entered
        if (cursor.moveToFirst()) {
            do {
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                weightEntries.add(weight + " lbs - " + date);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Setup RecyclerView
        adapter = new WeightLogAdapter(weightEntries);
        rvWeightEntries.setLayoutManager(new LinearLayoutManager(this));
        rvWeightEntries.setAdapter(adapter);

        // Add new weight
        fabAddWeight.setOnClickListener(view -> showAddWeightDialog());

        // Change goal weight
        tvGoalWeight.setOnClickListener(view -> showChangeGoalWeightDialog());
    }

    private void showAddWeightDialog() {
        final EditText weightInput = new EditText(WeightLogActivity.this);
        weightInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weightInput.setHint("Enter your weight in lbs");

        new AlertDialog.Builder(WeightLogActivity.this)
                .setTitle("Add Current Weight")
                .setView(weightInput)
                .setPositiveButton("Add", (dialog, which) -> {
                    String weightText = weightInput.getText().toString();
                    if (!weightText.isEmpty()) {
                        try {
                            float currentWeight = Float.parseFloat(weightText);
                            String entry = weightText + " lbs - " + getCurrentDate();

                            weightEntries.add(entry);
                            adapter.notifyDataSetChanged();

                            // Save to DB
                            dbHelper.insertWeight(userId, currentWeight, getCurrentDate());

                            // Check for goal reached
                            if (currentWeight == goalWeight) {
                                sendSMSNotification();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangeGoalWeightDialog() {
        final EditText goalWeightInput = new EditText(WeightLogActivity.this);
        goalWeightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        goalWeightInput.setHint("Enter your new goal weight");
        goalWeightInput.setText(String.valueOf(goalWeight));

        new AlertDialog.Builder(WeightLogActivity.this)
                .setTitle("Change Goal Weight")
                .setView(goalWeightInput)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newGoalWeightText = goalWeightInput.getText().toString();
                    if (!newGoalWeightText.isEmpty()) {
                        try {
                            goalWeight = Integer.parseInt(newGoalWeightText);
                            dbHelper.updateGoalWeight(userId, goalWeight);
                            updateGoalWeightText();
                            Toast.makeText(this, "Goal weight updated", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid weight", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please enter a valid goal weight", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateGoalWeightText() {
        String label = getString(R.string.goal_weight_label, goalWeight);
        tvGoalWeight.setText(label);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void sendSMSNotification() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            new SendSmsAsyncTask().execute();
        }
    }

    private class SendSmsAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SmsManager smsManager = SmsManager.getDefault();
            String phoneNumber = "1234567890"; // Replace this
            String message = "🎉 Congrats! You've reached your goal weight of " + goalWeight + " lbs!";
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(WeightLogActivity.this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMSNotification();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
