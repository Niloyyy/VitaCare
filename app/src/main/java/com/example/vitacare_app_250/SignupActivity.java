package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText signupPhone;
    private EditText signupPass;
    private Button signupSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupPhone = findViewById(R.id.signup_phone);
        signupPass = findViewById(R.id.signup_password);
        signupSubmit = findViewById(R.id.signup_submit);

        signupSubmit.setOnClickListener(v -> {
            String phone = signupPhone.getText().toString();
            String pass = signupPass.getText().toString();

            Toast.makeText(SignupActivity.this, "Account created (not stored): " + phone, Toast.LENGTH_SHORT).show();
            finish(); // Go back to login
        });
    }
}
