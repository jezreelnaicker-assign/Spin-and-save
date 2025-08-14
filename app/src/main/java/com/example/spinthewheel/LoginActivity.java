package com.example.spinthewheel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText firstNameInput, lastNameInput, emailInput;
    TextInputLayout firstNameLayout, lastNameLayout, emailLayout;
    MaterialButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        firstNameLayout = findViewById(R.id.firstNameLayout);
        lastNameLayout = findViewById(R.id.lastNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        loginBtn = findViewById(R.id.loginBtn);

        // Add text change listeners for real-time validation
        setupTextWatchers();

        loginBtn.setOnClickListener(v -> validateAndLogin());
    }

    private void setupTextWatchers() {
        // Clear errors when user starts typing
        firstNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (firstNameLayout.getError() != null) {
                    firstNameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lastNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (lastNameLayout.getError() != null) {
                    lastNameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (emailLayout.getError() != null) {
                    emailLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateAndLogin() {
        // Get input values
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Clear previous errors
        clearErrors();

        // Validate fields
        boolean isValid = true;

        if (firstName.isEmpty()) {
            firstNameLayout.setError("First name is required");
            isValid = false;
        } else if (!firstName.equalsIgnoreCase("Jezreel")) {
            firstNameLayout.setError("Invalid first name");
            isValid = false;
        }

        if (lastName.isEmpty()) {
            lastNameLayout.setError("Last name is required");
            isValid = false;
        } else if (!lastName.equalsIgnoreCase("Naicker")) {
            lastNameLayout.setError("Invalid last name");
            isValid = false;
        }

        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!email.equalsIgnoreCase("jezreelnaicker@gmail.com")) {
            emailLayout.setError("Invalid email address");
            isValid = false;
        }

        if (isValid) {
            // All validations passed
            loginBtn.setEnabled(false);
            loginBtn.setText("LOGGING IN...");
            
            Toast.makeText(this, "Login successful! Welcome back!", Toast.LENGTH_SHORT).show();
            
            // Small delay for better UX
            loginBtn.postDelayed(() -> {
                startActivity(new Intent(this, RoyalActivity.class));
                finish();
            }, 1000);
        } else {
            Toast.makeText(this, "Please check your credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrors() {
        firstNameLayout.setError(null);
        lastNameLayout.setError(null);
        emailLayout.setError(null);
    }
}

