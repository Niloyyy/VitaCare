package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchHospitalActivity extends AppCompatActivity {

    String[] hospitalNames = {"Mount Adora Hospital", "Osmani Hospital"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hospital);

        ListView hospitalList = findViewById(R.id.hospitalListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, hospitalNames);
        hospitalList.setAdapter(adapter);

        hospitalList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, HospitalDetailActivity.class);
            intent.putExtra("hospital_name", hospitalNames[position]);
            startActivity(intent);
        });
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }
}
