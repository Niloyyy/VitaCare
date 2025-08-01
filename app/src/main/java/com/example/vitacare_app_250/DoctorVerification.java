package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorVerification extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText name;
    private EditText license;
    private EditText email;
    private Button add;
    private DatabaseReference doctorsRef;

    private static final String PREFS_NAME = "user_session";
    private static final String JWT_KEY = "jwt_token";
    private static final String USER_TYPE_KEY = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_verification);

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        license = findViewById(R.id.editTextLicense);
        add = findViewById(R.id.buttonVerify);

        mAuth = FirebaseAuth.getInstance();
        doctorsRef = FirebaseDatabase.getInstance().getReference("doctors");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameInput = name.getText().toString().trim();
                String emailInput = email.getText().toString().trim();
                String licenseInput = license.getText().toString().trim();

                if (nameInput.isEmpty() || emailInput.isEmpty() || licenseInput.isEmpty()) {
                    Toast.makeText(DoctorVerification.this, "Fillup Every Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyDoctor(nameInput, emailInput, licenseInput);
            }
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

                    if (nameInput.equalsIgnoreCase(dbName) && emailInput.equalsIgnoreCase(dbEmail) && licenseInput.equalsIgnoreCase(dbLicense)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Toast.makeText(DoctorVerification.this, "Your credentials are valid", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DoctorVerification.this, DoctorLogin.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DoctorVerification.this, "Your credentials are not valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorVerification.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
