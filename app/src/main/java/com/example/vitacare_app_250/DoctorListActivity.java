package com.example.vitacare_app_250;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DoctorAdapter doctorAdapter;
    ArrayList<HashMap<String, String>> doctorList;
    ArrayList<HashMap<String, String>> fullDoctorList;
    DatabaseReference docsRef;
    EditText searchDoctorEditText;
    android.widget.ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        recyclerView = findViewById(R.id.recyclerViewDoctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchDoctorEditText = findViewById(R.id.searchDoctorEditText);
        progressBar = findViewById(R.id.progressBar);

        doctorList = new ArrayList<>();
        fullDoctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(doctorList);
        recyclerView.setAdapter(doctorAdapter);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        String specialty = getIntent().getStringExtra("specialty");
        if (specialty == null || specialty.trim().isEmpty()) {
            Toast.makeText(this, "No specialty provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        specialty = specialty.toLowerCase();

        docsRef = FirebaseDatabase.getInstance().getReference("docs").child(specialty);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        searchDoctorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadDoctors();
    }

    private void filter(String text) {
        doctorList.clear();
        if (text.isEmpty()) {
            doctorList.addAll(fullDoctorList);
        } else {
            text = text.toLowerCase();
            for (HashMap<String, String> doctor : fullDoctorList) {
                String name = doctor.get("name");
                if (name != null && name.toLowerCase().contains(text)) {
                    doctorList.add(doctor);
                }
            }
        }
        doctorAdapter.notifyDataSetChanged();
    }

    private void loadDoctors() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        docsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(android.view.View.GONE);
                fullDoctorList.clear();
                doctorList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    HashMap<String, String> doctor = (HashMap<String, String>) child.getValue();
                    if (doctor != null) {
                        fullDoctorList.add(doctor);
                        doctorList.add(doctor);
                    }
                }
                
                String currentSearchText = searchDoctorEditText.getText().toString();
                if (!currentSearchText.isEmpty()) {
                    filter(currentSearchText);
                } else {
                    doctorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(DoctorListActivity.this, "Error loading doctors: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

