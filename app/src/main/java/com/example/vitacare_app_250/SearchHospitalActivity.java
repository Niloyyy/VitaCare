package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class SearchHospitalActivity extends AppCompatActivity {

    ListView hospitalList;
    ArrayAdapter<String> adapter;
    ArrayList<String> hospitalNames = new ArrayList<>();
    ArrayList<Hospital> hospitalObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hospital);

        hospitalList = findViewById(R.id.hospitalListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hospitalNames);
        hospitalList.setAdapter(adapter);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("hospitals");

        // 👉 TEMPORARY: Upload hospital data ONCE
//        Hospital hospital1 = new Hospital("Mount Adora Hospital", "Nayasarak, Sylhet", "200 beds", "ICU, CCU, Pharmacy, 24/7 Emergency");
//        Hospital hospital2 = new Hospital("Osmani Medical College Hospital", "Uposhohor, Sylhet", "500+ beds", "Govt. Hospital, Emergency, Surgery, Blood Bank");
//
//        dbRef.push().setValue(hospital1);
//        dbRef.push().setValue(hospital2);
        Toast.makeText(this, "Hospital data uploaded to Firebase", Toast.LENGTH_SHORT).show();

        // 🔽 Load hospital list from Firebase
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hospitalNames.clear();
                hospitalObjects.clear();

                for (DataSnapshot hospitalSnap : snapshot.getChildren()) {
                    Hospital hospital = hospitalSnap.getValue(Hospital.class);
                    hospitalNames.add(hospital.name);
                    hospitalObjects.add(hospital);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SearchHospitalActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        hospitalList.setOnItemClickListener((parent, view, position, id) -> {
            Hospital selected = hospitalObjects.get(position);
            Intent intent = new Intent(this, HospitalDetailActivity.class);
            intent.putExtra("name", selected.name);
            intent.putExtra("address", selected.address);
            intent.putExtra("available_beds", selected.available_beds);
            intent.putExtra("available_icu", selected.available_icu);
            startActivity(intent);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
