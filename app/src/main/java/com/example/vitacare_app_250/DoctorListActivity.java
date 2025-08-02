package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class DoctorListActivity extends AppCompatActivity {

    ListView doctorListView;
    ArrayList<String> doctorList;
    ArrayAdapter<String> adapter;

    String selectedSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        selectedSpecialty = getIntent().getStringExtra("specialty");

        if (selectedSpecialty == null) {
            Toast.makeText(this, "No specialty provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        doctorListView = findViewById(R.id.doctor_list_view);
        doctorList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, doctorList);
        doctorListView.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("docs").child(selectedSpecialty.toLowerCase());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear();
                for (DataSnapshot docSnap : snapshot.getChildren()) {
                    String name = docSnap.child("name").getValue(String.class);
                    String degree = docSnap.child("degree").getValue(String.class);
                    String chamber = docSnap.child("chamber").getValue(String.class);

                    doctorList.add(name + "\n" + degree + "\n" + chamber);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
