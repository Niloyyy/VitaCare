package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText phn;
    private EditText pass;
    private Button add;
    private Button signup;

    private FirebaseAuth mAuth;

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
                String emailInput = phn.getText().toString();
                String passInput = pass.getText().toString();

                if(emailInput.isEmpty() || passInput.isEmpty()){
                    Toast.makeText(MainActivity.this, "Fillup Every Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Using firebase auth signing
                mAuth.signInWithEmailAndPassword(emailInput, passInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) { // Login successful
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else { // Login failed
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
