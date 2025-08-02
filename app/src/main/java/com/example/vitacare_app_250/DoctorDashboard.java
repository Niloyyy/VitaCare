package com.example.vitacare_app_250;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DoctorDashboard extends AppCompatActivity {

    private EditText doctorName, doctorContact, doctorMainSpecialty, doctorSpecialist, doctorDegree, doctorChamberAddress;
    private Button signUpButton, logoutButton;
    private TextView loginRedirectText;
    private ImageButton backButton;

    private static final String PREFS_NAME = "user_session";
    private static final String JWT_KEY = "jwt_token";
    private static final String USER_TYPE_KEY = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        // Bind views
        doctorName = findViewById(R.id.doctor_name);
        doctorContact = findViewById(R.id.doctor_contact);
        doctorMainSpecialty = findViewById(R.id.doctor_main_specialty);
        doctorSpecialist = findViewById(R.id.doctor_specialist);
        doctorDegree = findViewById(R.id.doctor_degree);
        doctorChamberAddress = findViewById(R.id.doctor_chamber_address);
        signUpButton = findViewById(R.id.doctor_signup_button);
        logoutButton = findViewById(R.id.logoutButton); // Make sure this ID matches your XML
        loginRedirectText = findViewById(R.id.doctor_login_redirect_text);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        signUpButton.setOnClickListener(v -> {
            String name = doctorName.getText().toString().trim();
            String contact = doctorContact.getText().toString().trim();
            String specialty = doctorMainSpecialty.getText().toString().trim();
            String specialist = doctorSpecialist.getText().toString().trim();
            String degree = doctorDegree.getText().toString().trim();
            String chamber = doctorChamberAddress.getText().toString().trim();

            Toast.makeText(this, "Doctor info captured (not yet saved)", Toast.LENGTH_SHORT).show();
        });

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorLogin.class));
        });

        logoutButton.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().remove(JWT_KEY).remove(USER_TYPE_KEY).apply();

            FirebaseAuth.getInstance().signOut();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, JoinActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
