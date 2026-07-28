package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
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
    List<Hospital> fullHospitalList = new ArrayList<>();
    HospitalAdapter adapter;
    EditText searchHospitalEditText;
    android.widget.ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hospital);

        hospitalListView = findViewById(R.id.hospitalListView);
        searchHospitalEditText = findViewById(R.id.searchHospitalEditText);
        progressBar = findViewById(R.id.progressBar);
        adapter = new HospitalAdapter(this, hospitalList);
        hospitalListView.setAdapter(adapter);

        loadHospitals();

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Search Filter
        searchHospitalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterHospitals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sort ICU Descending
        findViewById(R.id.btnSortICU).setOnClickListener(v -> {
            Collections.sort(hospitalList, (a, b) -> Integer.compare(
                    Integer.parseInt(b.available_icu != null ? b.available_icu : "0"),
                    Integer.parseInt(a.available_icu != null ? a.available_icu : "0")
            ));
            adapter.notifyDataSetChanged();
        });

        // Sort Beds Descending
        findViewById(R.id.btnSortBeds).setOnClickListener(v -> {
            Collections.sort(hospitalList, (a, b) -> Integer.compare(
                    Integer.parseInt(b.available_beds != null ? b.available_beds : "0"),
                    Integer.parseInt(a.available_beds != null ? a.available_beds : "0")
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

    private void filterHospitals(String text) {
        hospitalList.clear();
        if (text.isEmpty()) {
            hospitalList.addAll(fullHospitalList);
        } else {
            text = text.toLowerCase().trim();
            for (Hospital hospital : fullHospitalList) {
                if (hospital.name != null && hospital.name.toLowerCase().contains(text)) {
                    hospitalList.add(hospital);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadHospitals() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("hospitals");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(android.view.View.GONE);
                fullHospitalList.clear();
                hospitalList.clear();
                for (DataSnapshot hospitalSnap : snapshot.getChildren()) {
                    Hospital hospital = hospitalSnap.getValue(Hospital.class);
                    if (hospital != null) {
                        fullHospitalList.add(hospital);
                        hospitalList.add(hospital);
                    }
                }
                
                String currentSearch = searchHospitalEditText.getText().toString();
                if (!currentSearch.isEmpty()) {
                    filterHospitals(currentSearch);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(SearchHospitalActivity.this, "Error loading data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


