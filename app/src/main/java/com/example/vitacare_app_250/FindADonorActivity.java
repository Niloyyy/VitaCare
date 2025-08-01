package com.example.vitacare_app_250;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private ArrayList<Donor> donorList = new ArrayList<>();

    private static class Donor {
        String name, contact, bloodGroup;

        Donor(String name, String contact, String bloodGroup) {
            this.name = name;
            this.contact = contact;
            this.bloodGroup = bloodGroup;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_a_donor);

        donorTable = findViewById(R.id.donorTable);
        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        setupSpinner();
        loadDonorList();
    }

    private void setupSpinner() {
        String[] bloodGroups = {"All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups);
        bloodGroupSpinner.setAdapter(adapter);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDonors(bloodGroups[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadDonorList() {
        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("blood_donors");

        donorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                donorList.clear();
                for (DataSnapshot donorSnap : snapshot.getChildren()) {
                    String name = donorSnap.child("Name").getValue(String.class);
                    String contact = donorSnap.child("Phone").getValue(String.class);
                    String bloodGroup = donorSnap.child("BloodGroup").getValue(String.class);

                    if (name != null && contact != null && bloodGroup != null) {
                        donorList.add(new Donor(name, contact, bloodGroup));
                    }
                }

                filterDonors("All"); // Initially show all
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FindADonorActivity.this, "Failed to load donors.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterDonors(String selectedGroup) {
        // Remove all rows except the header (index 0)
        donorTable.removeViews(1, donorTable.getChildCount() - 1);

        for (Donor donor : donorList) {
            if (selectedGroup.equals("All") || donor.bloodGroup.equalsIgnoreCase(selectedGroup)) {
                addDonorToTable(donor.name, donor.contact, donor.bloodGroup);
            }
        }
    }

    private void addDonorToTable(String name, String contact, String bloodGroup) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 4, 0, 4);

        TextView nameText = createCell(name);
        TextView contactText = createCell(contact);
        TextView bloodText = createCell(bloodGroup);

        row.addView(nameText);
        row.addView(contactText);
        row.addView(bloodText);

        donorTable.addView(row);

        View line = new View(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2);
        params.span = 3;
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
