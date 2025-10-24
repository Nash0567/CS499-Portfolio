package com.example.nashellisweighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private android.widget.EditText usernameInput;
    private android.widget.EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        createAccountButton = findViewById(R.id.btnCreateAccount);

        loginButton.setOnClickListener(v -> {
            String u = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
            String p = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

            if (TextUtils.isEmpty(u)) {
                usernameInput.setError("Enter username");
                usernameInput.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(p)) {
                passwordInput.setError("Enter password");
                passwordInput.requestFocus();
                return;
            }

            // placeholder behaviour: go to weight log
            Toast.makeText(this, "Logged in as " + u, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, WeightLogActivity.class));
        });

        createAccountButton.setOnClickListener(v -> {
            // placeholder: pretend to create account
            String u = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
            if (u.isEmpty()) {
                Toast.makeText(this, "Enter a username to create account", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Account created (placeholder) for " + u, Toast.LENGTH_SHORT).show();
        });
    }
}