package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private TextView emailTextView, nameTextView, phoneTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();

        emailTextView = findViewById(R.id.email);
        nameTextView = findViewById(R.id.name);
        phoneTextView = findViewById(R.id.phone);
        backButton = findViewById(R.id.backButton);

        if (user != null) {
            emailTextView.setText("Email: " + user.getEmail());
            String displayName = user.getDisplayName();
            nameTextView.setText("Name: " + (displayName != null ? displayName : "Not set"));
            // You can set phone number if using phone auth:
            phoneTextView.setText("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "Not set"));
        }

        backButton.setOnClickListener(v -> finish());
    }
}
