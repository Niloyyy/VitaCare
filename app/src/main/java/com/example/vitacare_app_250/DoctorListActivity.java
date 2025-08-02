package com.example.vitacare_app_250;

import android.os.Bundle;
import android.util.Log;
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
    DatabaseReference docsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list); // make sure this layout exists

        recyclerView = findViewById(R.id.recyclerViewDoctors); // ensure this ID matches your XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(doctorList);
        recyclerView.setAdapter(doctorAdapter);

        // Get the specialty from Intent
        String specialty = getIntent().getStringExtra("specialty");
        if (specialty == null || specialty.trim().isEmpty()) {
            Toast.makeText(this, "No specialty provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        specialty = specialty.toLowerCase();

        docsRef = FirebaseDatabase.getInstance().getReference("docs").child(specialty);
        loadDoctors();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDoctors);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

    }

    private void loadDoctors() {
        docsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    HashMap<String, String> doctor = (HashMap<String, String>) child.getValue();
                    if (doctor != null) {
                        doctorList.add(doctor);
                    }
                }
                doctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorListActivity.this, "Error loading doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
