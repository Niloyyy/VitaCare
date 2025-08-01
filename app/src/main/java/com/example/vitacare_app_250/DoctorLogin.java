package com.example.vitacare_app_250;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DoctorLogin extends AppCompatActivity {

    private EditText email, pass;
    private Button add, signup;
    private FirebaseAuth mAuth;

    private static final String PREFS_NAME = "user_session";
    private static final String JWT_KEY = "jwt_token";
    private static final String USER_TYPE_KEY = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        add = findViewById(R.id.button);
        signup = findViewById(R.id.signup_button);
        mAuth = FirebaseAuth.getInstance();

        add.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passInput = pass.getText().toString().trim();

            if (emailInput.isEmpty() || passInput.isEmpty()) {
                Toast.makeText(this, "Fill all credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(emailInput, passInput).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.getIdToken(true).addOnSuccessListener(result -> {
                            String token = result.getToken();

                            // Save JWT and user type
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            prefs.edit()
                                    .putString(JWT_KEY, token)
                                    .putString(USER_TYPE_KEY, "doctor")
                                    .apply();

                            Toast.makeText(this, "Doctor login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, DoctorDashboard.class));
                            finish();
                        });
                    }
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });

        signup.setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorSignUp.class));
        });
    }
}
