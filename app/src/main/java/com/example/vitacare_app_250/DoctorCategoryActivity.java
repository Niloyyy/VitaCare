package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorCategoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_category);

        Button btnHeart = findViewById(R.id.buttonHeart);
        Button btnOrtho = findViewById(R.id.buttonOrtho);
        Button btnMedicine = findViewById(R.id.buttonMedicine);
        Button btnKidney = findViewById(R.id.buttonKidney);

        btnHeart.setOnClickListener(v -> {
            // Example: Open heart specialist list
            Intent intent = new Intent(this, HeartSpecialistActivity.class);
            startActivity(intent);
        });
    }
}
