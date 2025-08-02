package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SearchDoctorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor);

        Button cardiologyButton = findViewById(R.id.buttonCardiology);

        cardiologyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DoctorListActivity.class);
            intent.putExtra("specialty", "cardiology");
            startActivity(intent);
        });
    }
}
