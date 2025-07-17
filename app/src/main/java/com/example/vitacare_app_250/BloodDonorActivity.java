package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BloodDonorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_donor);


        findViewById(R.id.becomeadonor).setOnClickListener( v -> {
            startActivity(new Intent(this, BecomeADonorActivity.class));
        });

        findViewById(R.id.findadonor).setOnClickListener(v -> {
            startActivity(new Intent(this, FindADonorActivity.class));
        });
    }

}
