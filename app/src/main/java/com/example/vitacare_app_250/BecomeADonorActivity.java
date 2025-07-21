package com.example.vitacare_app_250;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
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

//        submitButton.setOnClickListener(v -> {
//            String name = nameInput.getText().toString().trim();
//            String number = contactNumber.getText().toString().trim();
//            String bloodGrp = bloodGroup.getText().toString().trim().toUpperCase();
//
//            List<String> validBloodGroups = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
//
//            if (!validBloodGroups.contains(bloodGrp)) {
//                Toast.makeText(this, "Invalid blood group! Please enter a valid one.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            //BloodDonor donor = new BloodDonor(name, number, bloodGrp);
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("blood_donors");
//            //ref.push().setValue(donor).addOnSuccessListener(aVoid -> Toast.makeText(this, "Donor Added!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//
//            ref.orderByChild("number").equalTo(number).get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    boolean alreadyExists = false;
//                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
//                        String dbName = snapshot.child("name").getValue(String.class);
//                        String dbGroup = snapshot.child("bloodGrp").getValue(String.class);
//                        if (name.equalsIgnoreCase(dbName) && bloodGrp.equalsIgnoreCase(dbGroup)) {
//                            alreadyExists = true;
//                            break;
//                        }
//                    }
//
//                    if (alreadyExists) {
//                        Toast.makeText(this, "Donor already exists!", Toast.LENGTH_LONG).show();
//                    } else {
//                        BloodDonor donor = new BloodDonor(name, number, bloodGrp);
//                        ref.push().setValue(donor)
//                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Donor Added!", Toast.LENGTH_LONG).show())
//                                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                    }
//                } else {
//                    Toast.makeText(this, "Database error!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });

        // if same credentials are submitted multiple time then then it'll not add the donor info to RT DB
        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String number = contactNumber.getText().toString().trim();
            String bloodGrp = bloodGroup.getText().toString().trim().toUpperCase();

            List<String> validBloodGroups = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

            if (!validBloodGroups.contains(bloodGrp)) {
                Toast.makeText(this, "Invalid blood group!", Toast.LENGTH_SHORT).show();
                return;
            }

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
                        Toast.makeText(this, "❗Donor already exists!", Toast.LENGTH_LONG).show();
                    } else {
                        BloodDonor donor = new BloodDonor(name, number, bloodGrp);
                        ref.push().setValue(donor).addOnSuccessListener(aVoid -> Toast.makeText(this, "Donor Added!", Toast.LENGTH_LONG).show()).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        });


    }
}
