package com.example.vitacare_app_250;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_session";
    private static final String JWT_KEY = "jwt_token";
    private static final String USER_TYPE_KEY = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedToken = prefs.getString(JWT_KEY, null);
        String userType = prefs.getString(USER_TYPE_KEY, null);

        if (savedToken != null && userType != null) {
            if (userType.equals("doctor")) {
                startActivity(new Intent(this, DoctorDashboard.class));
            } else if (userType.equals("patient")) {
                startActivity(new Intent(this, DashboardActivity.class));
            }
            finish();
            return;
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
