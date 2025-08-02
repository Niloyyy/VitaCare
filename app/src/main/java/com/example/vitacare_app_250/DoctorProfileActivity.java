package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);  // your profile layout XML

        TextView nameText = findViewById(R.id.doctorName);
        TextView contactText = findViewById(R.id.doctorContact);
        TextView specialtyText = findViewById(R.id.doctorSpecialty);
        TextView degreeText = findViewById(R.id.doctorDegree);
        TextView chamberText = findViewById(R.id.doctorChamber);

        // Get data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String contact = intent.getStringExtra("contact");
        String specialty = intent.getStringExtra("specialty");
        String field = intent.getStringExtra("field");
        String degree = intent.getStringExtra("degree");
        String chamber = intent.getStringExtra("chamber");

        // Set text or default if null
        nameText.setText(name != null ? "Dr. " + name : "No Name");
        contactText.setText(contact != null ? "Contact: " + contact : "No Contact");
        specialtyText.setText(specialty != null ? "Specialty: " + specialty : "No Specialty");
        degreeText.setText(degree != null ? "Degree: " + degree : "No Degree");
        chamberText.setText(chamber != null ? "Chamber: " + chamber : "No Chamber Info");
    }
}
