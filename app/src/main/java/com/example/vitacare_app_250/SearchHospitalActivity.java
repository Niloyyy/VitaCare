package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;



import androidx.appcompat.app.AppCompatActivity;

public class SearchHospitalActivity extends AppCompatActivity {

    ListView hospitalListView;
    List<Hospital> hospitalList = new ArrayList<>();
    HospitalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hospital);

        hospitalListView = findViewById(R.id.hospitalListView);
        adapter = new HospitalAdapter(this, hospitalList);
        hospitalListView.setAdapter(adapter);

        loadHospitals();

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Sort ICU Descending
        findViewById(R.id.btnSortICU).setOnClickListener(v -> {
            Collections.sort(hospitalList, (a, b) -> Integer.compare(
                    Integer.parseInt(b.available_icu),
                    Integer.parseInt(a.available_icu)
            ));
            adapter.notifyDataSetChanged();
        });

        // Sort Beds Descending
        findViewById(R.id.btnSortBeds).setOnClickListener(v -> {
            Collections.sort(hospitalList, (a, b) -> Integer.compare(
                    Integer.parseInt(b.available_beds),
                    Integer.parseInt(a.available_beds)
            ));
            adapter.notifyDataSetChanged();
        });

        hospitalListView.setOnItemClickListener((parent, view, position, id) -> {
            Hospital selected = hospitalList.get(position);
            Intent intent = new Intent(this, HospitalDetailActivity.class);
            intent.putExtra("name", selected.name);
            intent.putExtra("address", selected.address);
            intent.putExtra("beds", selected.available_beds);
            intent.putExtra("icu", selected.available_icu);
            startActivity(intent);
        });
    }

    private void loadHospitals() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("hospitals");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hospitalList.clear();
                for (DataSnapshot hospitalSnap : snapshot.getChildren()) {
                    Hospital hospital = hospitalSnap.getValue(Hospital.class);
                    if (hospital != null) {
                        hospitalList.add(hospital);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchHospitalActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

