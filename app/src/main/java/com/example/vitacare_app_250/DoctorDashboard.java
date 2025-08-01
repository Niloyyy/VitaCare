package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DoctorDashboard extends AppCompatActivity {

    EditText doctorName, doctorContact, doctorMainSpecialty, doctorSpecialist, doctorDegree, doctorChamberAddress;
    Button signUpButton;
    TextView loginRedirectText;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_info);

        // Bind views
        doctorName = findViewById(R.id.doctor_name);
        doctorContact = findViewById(R.id.doctor_contact);
        doctorMainSpecialty = findViewById(R.id.doctor_main_specialty);
        doctorSpecialist = findViewById(R.id.doctor_specialist);
        doctorDegree = findViewById(R.id.doctor_degree);
        doctorChamberAddress = findViewById(R.id.doctor_chamber_address);
        signUpButton = findViewById(R.id.doctor_signup_button);
        loginRedirectText = findViewById(R.id.doctor_login_redirect_text);
        backButton = findViewById(R.id.backButton);

        // Back button action
        backButton.setOnClickListener(v -> finish());

        // Sign up logic can be added here
        signUpButton.setOnClickListener(v -> {
            String name = doctorName.getText().toString().trim();
            String contact = doctorContact.getText().toString().trim();
            String specialty = doctorMainSpecialty.getText().toString().trim();
            String specialist = doctorSpecialist.getText().toString().trim();
            String degree = doctorDegree.getText().toString().trim();
            String chamber = doctorChamberAddress.getText().toString().trim();

            // You can now save these values to Firebase or validate them
        });

        // Login redirect action can be added if needed
        loginRedirectText.setOnClickListener(v -> {
            // Example: startActivity(new Intent(this, DoctorLoginActivity.class));
        });
    }
}
