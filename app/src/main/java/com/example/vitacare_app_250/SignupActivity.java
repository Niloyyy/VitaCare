package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button signupButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailInput = findViewById(R.id.signup_email);
        passwordInput = findViewById(R.id.pass);
        confirmPasswordInput = findViewById(R.id.conpass);
        signupButton = findViewById(R.id.signup_submit_button);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (ValidationUtils.isEmpty(emailInput) || ValidationUtils.isEmpty(passwordInput) || ValidationUtils.isEmpty(confirmPasswordInput)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!ValidationUtils.isValidEmail(email)) {
                emailInput.setError("Please enter a valid email address");
                emailInput.requestFocus();
                return;
            }
            
            if (!ValidationUtils.isValidPassword(password)) {
                passwordInput.setError("Password must be at least 8 characters long, with 1 number and 1 special character");
                passwordInput.requestFocus();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                confirmPasswordInput.requestFocus();
                return;
            }

            signupButton.setEnabled(false); // prevent multiple clicks

            mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                signupButton.setEnabled(true);
                if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                    
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("role", "patient");
                    
                    userRef.setValue(userData).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Toast.makeText(this, "Patient account created successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Go to the login page
                        } else {
                            Toast.makeText(this, "Failed to save user role data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }
}
