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
        Button nephrologyButton = findViewById(R.id.buttonNephrology);
        Button orthopedicsButton = findViewById(R.id.buttonOrthopedics);
        Button neurologyButton = findViewById(R.id.buttonNeurology);

        cardiologyButton.setOnClickListener(v -> openDoctorList("cardiology"));
        nephrologyButton.setOnClickListener(v -> openDoctorList("nephrology"));
        orthopedicsButton.setOnClickListener(v -> openDoctorList("orthopedics"));
        neurologyButton.setOnClickListener(v -> openDoctorList("neurology"));
    }

    private void openDoctorList(String specialty) {
        Intent intent = new Intent(this, DoctorListActivity.class);
        intent.putExtra("specialty", specialty.toLowerCase()); // Ensures lowercase match with Firebase
        startActivity(intent);
    }
}
