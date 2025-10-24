package com.example.nashellisweighttracker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
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
    private int goalWeight = 170; // Default goal weight

    private static final int REQUEST_SMS_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);

        // Find views
        tvGoalWeight = findViewById(R.id.tvGoalWeight);
        rvWeightEntries = findViewById(R.id.rvWeightEntries);
        fabAddWeight = findViewById(R.id.fabAddWeight);

        // Set goal weight text
        updateGoalWeightText();  // Call the method to update the goal weight text

        // Sample data
        weightEntries = new ArrayList<>();

        // Setup RecyclerView
        adapter = new WeightLogAdapter(weightEntries);
        rvWeightEntries.setLayoutManager(new LinearLayoutManager(this));
        rvWeightEntries.setAdapter(adapter);

        // Floating action button click
        fabAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show dialog to add weight
                showAddWeightDialog();
            }
        });

        // Make the goal weight text view clickable
        tvGoalWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show dialog to change goal weight
                showChangeGoalWeightDialog();
            }
        });
    }

    // Method to show a dialog for adding a new weight entry
    private void showAddWeightDialog() {
        final EditText weightInput = new EditText(WeightLogActivity.this);
        weightInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weightInput.setHint("Enter your weight in lbs");

        new AlertDialog.Builder(WeightLogActivity.this)
                .setTitle("Add Current Weight")
                .setView(weightInput)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String weightText = weightInput.getText().toString();
                        if (!weightText.isEmpty()) {
                            // Add weight entry to RecyclerView
                            String weightEntry = weightText + " lbs - " + getCurrentDate();
                            weightEntries.add(weightEntry);
                            adapter.notifyDataSetChanged();

                            // Check if the user reached the goal weight and send SMS
                            int currentWeight = Integer.parseInt(weightText);
                            if (currentWeight == goalWeight) {
                                sendSMSNotification();
                            }
                        } else {
                            Toast.makeText(WeightLogActivity.this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Method to show a dialog for changing the goal weight
    private void showChangeGoalWeightDialog() {
        final EditText goalWeightInput = new EditText(WeightLogActivity.this);
        goalWeightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        goalWeightInput.setHint("Enter your new goal weight");

        // Pre-fill with the current goal weight
        goalWeightInput.setText(String.valueOf(goalWeight));

        new AlertDialog.Builder(WeightLogActivity.this)
                .setTitle("Change Goal Weight")
                .setView(goalWeightInput)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newGoalWeightText = goalWeightInput.getText().toString();
                        if (!newGoalWeightText.isEmpty()) {
                            try {
                                goalWeight = Integer.parseInt(newGoalWeightText);
                                updateGoalWeightText(); // Update the UI with the new goal weight
                            } catch (NumberFormatException e) {
                                Toast.makeText(WeightLogActivity.this, "Invalid weight", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WeightLogActivity.this, "Please enter a valid goal weight", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Method to update the goal weight TextView
    private void updateGoalWeightText() {
        String label = getString(R.string.goal_weight_label, goalWeight);
        tvGoalWeight.setText(label);
    }

    // Helper method to get the current date
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Method to send an SMS notification when goal weight is reached
    private void sendSMSNotification() {
        // Check if the SEND_SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            // Permission granted, send SMS (in background thread)
            new SendSmsAsyncTask().execute();
        }
    }

    // AsyncTask to send SMS in the background (to avoid blocking UI thread)
    private class SendSmsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SmsManager smsManager = SmsManager.getDefault();
            String phoneNumber = "1234567890"; // Replace with the phone number to send SMS to
            String message = "Congratulations! You've reached your goal weight of " + goalWeight + " lbs!";
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(WeightLogActivity.this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);  // Call super first

        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the SMS
                sendSMSNotification();
            } else {
                // Permission denied, show message
                Toast.makeText(this, "SMS permission denied. Cannot send SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
