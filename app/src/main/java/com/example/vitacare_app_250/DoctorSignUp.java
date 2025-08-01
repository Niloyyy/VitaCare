package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class DoctorSignUp extends AppCompatActivity {

    private EditText email , pass , conPass;
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

        signupButton.setOnClickListener(v ->{
            String inputEmail = email.getText().toString();
            String inputPass = pass.getText().toString();
            String inputConPass = conPass.getText().toString();

            if (inputEmail.isEmpty() || inputPass.isEmpty() || inputConPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if (!inputPass.equals(inputConPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else{
                mAuth.createUserWithEmailAndPassword(inputEmail , inputPass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Go to the login page
                    } else {
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }
}