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
    private static final String USER_TYPE_KEY = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userType = prefs.getString(USER_TYPE_KEY, null);

        if (currentUser != null && userType != null) {
            if (userType.equals("doctor")) {
                startActivity(new Intent(this, DoctorDashboard.class));
            } else if (userType.equals("patient")) {
                startActivity(new Intent(this, DashboardActivity.class));
            }
            finish();
            return;
        } else if (currentUser != null) {
            // User is logged in but role is unknown in prefs, sign out to be safe
            FirebaseAuth.getInstance().signOut();
        }

        setContentView(R.layout.activity_join);

        Button joinAsDoctor = findViewById(R.id.joinAsDoctor);
        Button joinAsPatient = findViewById(R.id.joinAsPatient);

        joinAsDoctor.setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorVerification.class));
        });

        joinAsPatient.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}

