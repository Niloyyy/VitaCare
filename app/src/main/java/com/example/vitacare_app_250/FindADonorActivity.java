package com.example.vitacare_app_250;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class FindADonorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_a_donor);

        // Fix: remove 'void' and move inside onCreate
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }
}
