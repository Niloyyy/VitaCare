package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HospitalDetailActivity extends AppCompatActivity {

    TextView hospitalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);

        hospitalInfo = findViewById(R.id.textViewHospitalDetails);

        String hospitalName = getIntent().getStringExtra("hospital_name");

        if (hospitalName != null) {
            switch (hospitalName) {
                case "Mount Adora Hospital":
                    hospitalInfo.setText("Name: Mount Adora Hospital\nLocation: Nayasarak, Sylhet\nCapacity: 200 beds\nFacilities: ICU, CCU, Pharmacy, 24/7 Emergency");
                    break;
                case "Osmani Hospital":
                    hospitalInfo.setText("Name: Osmani Medical College Hospital\nLocation: Uposhohor, Sylhet\nCapacity: 500+ beds\nFacilities: Govt. Hospital, Emergency, Surgery, Blood Bank");
                    break;
                default:
                    hospitalInfo.setText("Details not available.");
                    break;
            }
        }
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }

}
