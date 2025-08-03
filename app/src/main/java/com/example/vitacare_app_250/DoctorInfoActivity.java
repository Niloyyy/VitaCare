package com.example.vitacare_app_250;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class DoctorInfoActivity extends AppCompatActivity {

    ImageView searchIcon;
    EditText searchEditText;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> doctorList;

    DatabaseReference docsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);

        // Initialize views
        searchIcon = findViewById(R.id.searchIcon);
        searchEditText = findViewById(R.id.searchEditText);
        listView = findViewById(R.id.listViewDoctors);

        doctorList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, doctorList);
        listView.setAdapter(adapter);

        // Firebase reference to 'docs'
        docsRef = FirebaseDatabase.getInstance().getReference("docs");

        // Fetch all doctors from all specialties
        loadDoctorsFromFirebase();

        // Search logic
        searchIcon.setOnClickListener(v -> {
            searchIcon.setVisibility(View.GONE);
            searchEditText.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void loadDoctorsFromFirebase() {
        docsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear(); // clear any old data

                for (DataSnapshot specialistSnap : snapshot.getChildren()) {
                    for (DataSnapshot doctorSnap : specialistSnap.getChildren()) {
                        String doctorName = doctorSnap.child("name").getValue(String.class);
                        if (doctorName != null) {
                            doctorList.add(doctorName);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
