package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class DoctorDashboard extends AppCompatActivity {

    EditText doctorName, doctorContact, doctorDegree, doctorChamberAddress;
    Spinner specialistSpinner;
    Button signUpButton;
    ImageButton backButton;

    DatabaseReference docsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_info);

        // Bind views
        doctorName = findViewById(R.id.doctor_name);
        doctorContact = findViewById(R.id.doctor_contact);
        doctorDegree = findViewById(R.id.doctor_degree);
        doctorChamberAddress = findViewById(R.id.doctor_chamber_address);
        specialistSpinner = findViewById(R.id.doctor_specialist_spinner);
        signUpButton = findViewById(R.id.doctor_signup_button);
        backButton = findViewById(R.id.backButton);

        // Setup Firebase reference to "docs"
        docsRef = FirebaseDatabase.getInstance().getReference("docs");

        // Setup Spinner with your 4 specialist options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.specialist_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialistSpinner.setAdapter(adapter);

        // Back button finishes activity
        backButton.setOnClickListener(v -> finish());

        // Sign up button click listener
        signUpButton.setOnClickListener(v -> {
            String name = doctorName.getText().toString().trim();
            String contact = doctorContact.getText().toString().trim();
            String degree = doctorDegree.getText().toString().trim();
            String chamber = doctorChamberAddress.getText().toString().trim();
            String specialist = specialistSpinner.getSelectedItem().toString().toLowerCase();

            if (name.isEmpty() || contact.isEmpty() || specialist.equalsIgnoreCase("select specialist")) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create unique ID for doctor
            String docId = UUID.randomUUID().toString();

            // Prepare doctor data
            HashMap<String, String> doctorMap = new HashMap<>();
            doctorMap.put("name", name);
            doctorMap.put("contact", contact);
            doctorMap.put("specialist", specialist);
            doctorMap.put("degree", degree);
            doctorMap.put("chamber", chamber);

            // Save to Firebase under docs/specialist/docId
            docsRef.child(specialist).child(docId).setValue(doctorMap)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Doctor added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add doctor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
