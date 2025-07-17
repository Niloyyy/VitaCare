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

        // Use this line to go directly to DoctorCategoryActivity
        findViewById(R.id.buttonSearchDoctor).setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorCategoryActivity.class));
        });

        findViewById(R.id.buttonSearchHospital).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchHospitalActivity.class));
        });

        findViewById(R.id.buttonblooddonor).setOnClickListener(v -> {
            startActivity(new Intent(this, BloodDonorActivity.class));
        });
    }
}
