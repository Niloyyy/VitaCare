package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Button joinAsDoctor = findViewById(R.id.joinAsDoctor);
        Button joinAsPatient = findViewById(R.id.joinAsPatient);

        joinAsDoctor.setOnClickListener(v -> {
            startActivity(new Intent(JoinActivity.this, DoctorCategoryActivity.class));
        });

        joinAsPatient.setOnClickListener(v -> {
            startActivity(new Intent(JoinActivity.this, MainActivity.class));
        });
    }
}
