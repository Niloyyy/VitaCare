package com.example.vitacare_app_250;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText phn;
    private EditText pass;
    private Button add;
    private Button signup;

    private FirebaseAuth mAuth;

    private static final String PREFS_NAME = "user_session";
    private static final String JWT_KEY = "jwt_token";
    private static final String USER_TYPE_KEY = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        phn = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        add = findViewById(R.id.button);
        signup = findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = phn.getText().toString().trim();
                String passInput = pass.getText().toString().trim();

                if (ValidationUtils.isEmpty(phn) || ValidationUtils.isEmpty(pass)) {
                    Toast.makeText(MainActivity.this, "Fill in all credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (!ValidationUtils.isValidEmail(emailInput)) {
                    phn.setError("Please enter a valid email address");
                    phn.requestFocus();
                    return;
                }

                add.setEnabled(false);

                mAuth.signInWithEmailAndPassword(emailInput, passInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                String uid = mAuth.getCurrentUser().getUid();
                                FirebaseDatabase.getInstance().getReference("users").child(uid).child("role")
                                    .get().addOnCompleteListener(roleTask -> {
                                        add.setEnabled(true);
                                        if (roleTask.isSuccessful() && roleTask.getResult().exists()) {
                                            String role = roleTask.getResult().getValue(String.class);
                                            if ("patient".equals(role)) {
                                                mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(result -> {
                                                    String token = result.getToken();

                                                    // Save session info
                                                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.putString(JWT_KEY, token);
                                                    editor.putString(USER_TYPE_KEY, "patient");  // Mark user as patient
                                                    editor.apply();

                                                    // Navigate to patient dashboard
                                                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                });
                                            } else {
                                                mAuth.signOut();
                                                Toast.makeText(MainActivity.this, "This account is registered as a Doctor. Please use Doctor Login.", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            mAuth.signOut();
                                            Toast.makeText(MainActivity.this, "Error checking account role. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            } else {
                                add.setEnabled(true);
                                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}

