package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HospitalDetailActivity extends AppCompatActivity {

    TextView nameText, addressText, bedCountText, icuCountText;
    ImageButton backButton;
    ImageView hospitalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);

        // View bindings
        hospitalImage = findViewById(R.id.hospitalImage);
        nameText = findViewById(R.id.hospitalName);
        addressText = findViewById(R.id.hospitalAddress);
        bedCountText = findViewById(R.id.hospitalCapacity);  // Reusing old ID
        icuCountText = findViewById(R.id.hospitalICU); // Reusing old ID
        backButton = findViewById(R.id.backButton);

        // Get data from Intent
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String beds = getIntent().getStringExtra("beds");
        String icu = getIntent().getStringExtra("icu");

        // Set data to views
        nameText.setText(name != null ? name : "N/A");
        addressText.setText(address != null ? address : "N/A");
        bedCountText.setText("Available Beds: " + (beds != null ? beds : "N/A"));
        icuCountText.setText("Available ICU Beds: " + (icu != null ? icu : "N/A"));

        // Back button action
        backButton.setOnClickListener(v -> finish());
    }
}
