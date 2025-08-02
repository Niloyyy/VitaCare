package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorVerification extends AppCompatActivity {

    private EditText name, license, email;
    private Button verifyBtn;
    private DatabaseReference doctorsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_verification);

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        license = findViewById(R.id.editTextLicense);
        verifyBtn = findViewById(R.id.buttonVerify);

        doctorsRef = FirebaseDatabase.getInstance().getReference("doctors");

        verifyBtn.setOnClickListener(v -> {
            String nameInput = name.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String licenseInput = license.getText().toString().trim();

            if (nameInput.isEmpty() || emailInput.isEmpty() || licenseInput.isEmpty()) {
                Toast.makeText(this, "Fill up every credential", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyDoctor(nameInput, emailInput, licenseInput);
        });
    }

    private void verifyDoctor(String nameInput, String emailInput, String licenseInput) {
        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot doctorSnap : snapshot.getChildren()) {
                    String dbEmail = doctorSnap.child("email").getValue(String.class);
                    String dbLicense = doctorSnap.child("license").getValue(String.class);
                    String dbName = doctorSnap.child("name").getValue(String.class);

                    if (nameInput.equalsIgnoreCase(dbName)
                            && emailInput.equalsIgnoreCase(dbEmail)
                            && licenseInput.equalsIgnoreCase(dbLicense)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Toast.makeText(DoctorVerification.this, "Credentials are valid", Toast.LENGTH_SHORT).show();
                    // 🔒 Do NOT store JWT or session here
                    startActivity(new Intent(DoctorVerification.this, DoctorLogin.class));
                    finish();
                } else {
                    Toast.makeText(DoctorVerification.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorVerification.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
