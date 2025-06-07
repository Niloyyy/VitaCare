package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchHospitalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("Search Hospital Page\n(Search functionality goes here)");
        tv.setTextSize(24);
        tv.setPadding(20, 20, 20, 20);
        setContentView(tv);
    }
}