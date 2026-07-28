package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DoctorSignUp extends AppCompatActivity {

    private EditText email, pass, conPass;
    private Button signupButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_sign_up);

        email = findViewById(R.id.signup_email);
        pass = findViewById(R.id.pass);
        conPass = findViewById(R.id.conpass);
        signupButton = findViewById(R.id.signup_submit_button);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(v -> {
            String inputEmail = email.getText().toString().trim();
            String inputPass = pass.getText().toString();
            String inputConPass = conPass.getText().toString();

            if (ValidationUtils.isEmpty(email) || ValidationUtils.isEmpty(pass) || ValidationUtils.isEmpty(conPass)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!ValidationUtils.isValidEmail(inputEmail)) {
                email.setError("Please enter a valid email address");
                email.requestFocus();
                return;
            }
            
            if (!ValidationUtils.isValidPassword(inputPass)) {
                pass.setError("Password must be at least 8 characters long, with 1 number and 1 special character");
                pass.requestFocus();
                return;
            }
            
            if (!inputPass.equals(inputConPass)) {
                conPass.setError("Passwords do not match");
                conPass.requestFocus();
                return;
            }

            signupButton.setEnabled(false);

            mAuth.createUserWithEmailAndPassword(inputEmail, inputPass).addOnCompleteListener(task -> {
                signupButton.setEnabled(true);
                if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                    
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", inputEmail);
                    userData.put("role", "doctor");
                    
                    userRef.setValue(userData).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Toast.makeText(this, "Doctor account created successfully", Toast.LENGTH_SHORT).show();
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