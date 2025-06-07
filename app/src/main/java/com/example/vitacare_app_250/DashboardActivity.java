package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.buttonDoctorInfo).setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorInfoActivity.class));
        });

        findViewById(R.id.buttonSearchDoctor).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchDoctorActivity.class));
        });

        findViewById(R.id.buttonSearchHospital).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchHospitalActivity.class));
        });
    }
}
