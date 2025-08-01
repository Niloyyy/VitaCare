package com.example.vitacare_app_250;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class JoinActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_session";
    private static final String JWT_KEY = "jwt_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check for stored JWT token
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedToken = prefs.getString(JWT_KEY, null);

        if (savedToken != null) {
            // Token exists, skip login
            Intent intent = new Intent(JoinActivity.this, DoctorVerification.class);
            startActivity(intent);
            finish();
            return;
        }

        //no token found, show the Join screen
        setContentView(R.layout.activity_join);

        Button joinAsDoctor = findViewById(R.id.joinAsDoctor);
        Button joinAsPatient = findViewById(R.id.joinAsPatient);

        joinAsDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(JoinActivity.this, DoctorVerification.class);
            startActivity(intent);
        });

        joinAsPatient.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                currentUser.getIdToken(true).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        //save the token in SharedPreferences
                        prefs.edit().putString(JWT_KEY, token).apply();

                        // Navigate to MainActivity
                        Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //token fetch failed, fallback
                        startActivity(new Intent(JoinActivity.this, MainActivity.class));
                        finish();
                    }
                });
            } else {
                // No logged-in user, proceed to login/register
                startActivity(new Intent(JoinActivity.this, MainActivity.class));
            }
        });
    }
}
