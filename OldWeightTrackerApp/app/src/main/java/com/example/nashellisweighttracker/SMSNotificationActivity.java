package com.example.nashellisweighttracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SMSNotificationActivity extends AppCompatActivity {

    private static final int REQUEST_SMS = 101;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsnotification);

        tvResult = findViewById(R.id.tvResult);

        // This activity may not need to be used explicitly unless you are directly dealing with permission
        // removal of manual button logic from XML, so the UI simply informs about permission status
    }

    // When you need to send SMS in your main logic (e.g., in WeightLogActivity), use the following method:
    private void checkAndSendSMS() {
        // Check if the SEND_SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS);
        } else {
            // If permission is granted, send SMS
            sendSMS();
        }
    }

    // Method to send SMS when permission is granted
    private void sendSMS() {
        // Logic to send SMS after goal weight is reached
        // (You can integrate this with the sendSMS() method from WeightLogActivity)
        String phoneNumber = "1234567890"; // Replace with an actual number
        String message = "Congratulations! You've reached your goal weight!";
        // Send SMS logic here
        Toast.makeText(this, "SMS sent: " + message, Toast.LENGTH_SHORT).show();
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tvResult.setText("Permission granted. Notifications enabled.");
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                tvResult.setText("Permission denied. Notifications disabled.");
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}