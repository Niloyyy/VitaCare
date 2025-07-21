package com.example.vitacare_app_250;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BecomeADonorActivity extends AppCompatActivity {

    private EditText nameInput, contactNumber, bloodGroup;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_a_donor);

        nameInput = findViewById(R.id.Name);
        contactNumber = findViewById(R.id.Phone);
        bloodGroup = findViewById(R.id.BloodGrp);

        submitButton = findViewById(R.id.submitBtn);

        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String number = contactNumber.getText().toString().trim();
            String bloodGrp = bloodGroup.getText().toString().trim().toUpperCase();

            List<String> validBloodGroups = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

            if (!validBloodGroups.contains(bloodGrp)) {
                Toast.makeText(this, "Invalid blood group! Please enter a valid one.", Toast.LENGTH_SHORT).show();
                return;
            }

            BloodDonor donor = new BloodDonor(name, number, bloodGrp);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("blood_donors");
            ref.push().setValue(donor).addOnSuccessListener(aVoid -> Toast.makeText(this, "Donor Added!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
