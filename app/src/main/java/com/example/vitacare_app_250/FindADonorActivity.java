package com.example.vitacare_app_250;

import android.os.Bundle;
import android.view.View;
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

public class FindADonorActivity extends AppCompatActivity {

    private TableLayout donorTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_a_donor);

        donorTable = findViewById(R.id.donorTable);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        loadDonorList();
    }

    private void loadDonorList() {
        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("blood_donors");

        donorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot donorSnap : snapshot.getChildren()) {
                    String name = donorSnap.child("Name").getValue(String.class);
                    String contact = donorSnap.child("Phone").getValue(String.class);
                    String bloodGroup = donorSnap.child("BloodGroup").getValue(String.class);

                    addDonorToTable(name, contact, bloodGroup);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FindADonorActivity.this, "Failed to load donors.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDonorToTable(String name, String contact, String bloodGroup) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 4, 0, 4); // space between rows
        row.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        TextView nameText = createCell(name);
        TextView contactText = createCell(contact);
        TextView bloodText = createCell(bloodGroup);

        row.addView(nameText);
        row.addView(contactText);
        row.addView(bloodText);

        donorTable.addView(row);

        // Optional separator line
        View line = new View(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, 2);
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
//        tv.setBackgroundColor(getResources().getColor(android.R.color.white));
        return tv;
    }
}
