package com.example.vitacare_app_250;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindADonorActivity extends AppCompatActivity {

    private TableLayout donorTable;
    private Spinner bloodGroupSpinner;
    private EditText searchLocationEditText;
    private android.widget.ProgressBar progressBar;
    private ArrayList<Donor> donorList = new ArrayList<>();

    private static class Donor {
        String name, contact, bloodGroup, location;

        Donor(String name, String contact, String bloodGroup, String location) {
            this.name = name;
            this.contact = contact;
            this.bloodGroup = bloodGroup;
            this.location = location;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_a_donor);

        donorTable = findViewById(R.id.donorTable);
        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        searchLocationEditText = findViewById(R.id.searchLocationEditText);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        setupSpinner();
        
        searchLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDonors(bloodGroupSpinner.getSelectedItem() != null ? bloodGroupSpinner.getSelectedItem().toString() : "All", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadDonorList();
    }

    private void setupSpinner() {
        String[] bloodGroups = {"All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups);
        bloodGroupSpinner.setAdapter(adapter);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDonors(bloodGroups[position], searchLocationEditText.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadDonorList() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("blood_donors");

        donorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(android.view.View.GONE);
                donorList.clear();
                for (DataSnapshot donorSnap : snapshot.getChildren()) {
                    String name = donorSnap.child("Name").getValue(String.class);
                    String contact = donorSnap.child("Phone").getValue(String.class);
                    String bloodGroup = donorSnap.child("BloodGroup").getValue(String.class);
                    String location = donorSnap.child("Location").getValue(String.class);
                    
                    if (location == null) location = "N/A";

                    if (name != null && contact != null && bloodGroup != null) {
                        donorList.add(new Donor(name, contact, bloodGroup, location));
                    }
                }

                String currentGroup = bloodGroupSpinner.getSelectedItem() != null ? bloodGroupSpinner.getSelectedItem().toString() : "All";
                filterDonors(currentGroup, searchLocationEditText.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(FindADonorActivity.this, "Failed to load donors: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterDonors(String selectedGroup, String locationQuery) {
        // Remove all rows except the header (index 0)
        donorTable.removeViews(1, donorTable.getChildCount() - 1);
        String query = locationQuery.toLowerCase().trim();

        for (Donor donor : donorList) {
            boolean matchGroup = selectedGroup.equals("All") || donor.bloodGroup.equalsIgnoreCase(selectedGroup);
            boolean matchLocation = query.isEmpty() || donor.location.toLowerCase().contains(query);
            
            if (matchGroup && matchLocation) {
                addDonorToTable(donor.name, donor.contact, donor.bloodGroup, donor.location);
            }
        }
    }

    private void addDonorToTable(String name, String contact, String bloodGroup, String location) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 4, 0, 4);

        TextView nameText = createCell(name);
        TextView contactText = createCell(contact);
        TextView bloodText = createCell(bloodGroup);
        TextView locText = createCell(location);

        row.addView(nameText);
        row.addView(contactText);
        row.addView(bloodText);
        row.addView(locText);

        donorTable.addView(row);

        View line = new View(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2);
        params.span = 4;
        line.setLayoutParams(params);
        line.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        donorTable.addView(line);
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setPadding(16, 8, 16, 8);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }
}

