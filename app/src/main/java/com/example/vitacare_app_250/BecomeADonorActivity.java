package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

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

        // Setup blood group dropdown
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_blood_group_dropdown_item, bloodGroups);
        ((AutoCompleteTextView) bloodGroup).setAdapter(adapter);
        bloodGroup.setOnClickListener(v -> ((AutoCompleteTextView) bloodGroup).showDropDown());

        submitButton.setOnClickListener(v -> {
            if (ValidationUtils.isEmpty(nameInput) || ValidationUtils.isEmpty(contactNumber) || ValidationUtils.isEmpty(bloodGroup)) {
                Toast.makeText(this, "❌ Please fill out all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = nameInput.getText().toString().trim();
            String number = contactNumber.getText().toString().trim();
            String bloodGrp = bloodGroup.getText().toString().trim().toUpperCase();

            List<String> validBloodGroups = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

            if (!validBloodGroups.contains(bloodGrp)) {
                bloodGroup.setError("Invalid blood group!");
                bloodGroup.requestFocus();
                return;
            }

            if (!ValidationUtils.isValidPhone(number)) {
                contactNumber.setError("Please enter a valid phone number!");
                contactNumber.requestFocus();
                return;
            }

            submitButton.setEnabled(false);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("blood_donors");

            Query query = ref.orderByChild("Phone").equalTo(number);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean alreadyExists = false;

                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        String dbName = snapshot.child("Name").getValue(String.class);
                        String dbGroup = snapshot.child("BloodGroup").getValue(String.class);

                        if (dbName != null && dbGroup != null &&
                                dbName.equalsIgnoreCase(name) &&
                                dbGroup.equalsIgnoreCase(bloodGrp)) {
                            alreadyExists = true;
                            break;
                        }
                    }

                    if (alreadyExists) {
                        submitButton.setEnabled(true);
                        Toast.makeText(this, "❗Donor already exists!", Toast.LENGTH_LONG).show();
                    } else {
                        BloodDonor donor = new BloodDonor(name, number, bloodGrp);
                        ref.push().setValue(donor).addOnSuccessListener(aVoid -> {
                            submitButton.setEnabled(true);
                            Toast.makeText(this, "Donor Added!", Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(e -> {
                            submitButton.setEnabled(true);
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    submitButton.setEnabled(true);
                }
            });
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

    }
}
