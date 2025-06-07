package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText phoneInput, passwordInput, confirmPasswordInput;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        phoneInput = findViewById(R.id.signup_phone);
        passwordInput = findViewById(R.id.pass);
        confirmPasswordInput = findViewById(R.id.conpass);
        signupButton = findViewById(R.id.signup_submit_button);

        signupButton.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Account created (not stored yet)", Toast.LENGTH_SHORT).show();
                finish(); // closes sign up and returns to login
            }
        });
    }
}
