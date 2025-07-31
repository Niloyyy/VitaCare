package com.example.vitacare_app_250;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_session";
    private static final String TOKEN_KEY = "jwt_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //token check before loading layout
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, JoinActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.buttonDoctorInfo).setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorInfoActivity.class));
        });

        findViewById(R.id.buttonSearchDoctor).setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorCategoryActivity.class));
        });

        findViewById(R.id.buttonSearchHospital).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchHospitalActivity.class));
        });

        findViewById(R.id.buttonblooddonor).setOnClickListener(v -> {
            startActivity(new Intent(this, BloodDonorActivity.class));
        });

        findViewById(R.id.buttonMyProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, UserProfileActivity.class));
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            prefs.edit().remove(TOKEN_KEY).apply();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, JoinActivity.class));
            finish();
        });
    }
}
