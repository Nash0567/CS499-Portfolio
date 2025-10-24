package com.example.nashellisweighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;

    private SQLiteHelper dbHelper;

    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        createAccountButton = findViewById(R.id.btnCreateAccount);

        dbHelper = new SQLiteHelper(this);

        // Login Button
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!isValidInput(username, password)) return;

            boolean validLogin = dbHelper.validateLogin(username, password);
            if (validLogin) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                int userId = dbHelper.getUserId(username);

                Intent intent = new Intent(LoginActivity.this, WeightLogActivity.class);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Create Account Button
        createAccountButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!isValidInput(username, password)) return;

            boolean success = dbHelper.registerUser(username, password, 0.0f);
            if (success) {
                Toast.makeText(this, "Account created for " + username, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidInput(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Enter username");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Enter password");
            return false;
        }
        if (username.length() < MIN_LENGTH || username.length() > MAX_LENGTH) {
            usernameInput.setError("Username must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
            return false;
        }
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            passwordInput.setError("Password must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
            return false;
        }
        return true;
    }
}