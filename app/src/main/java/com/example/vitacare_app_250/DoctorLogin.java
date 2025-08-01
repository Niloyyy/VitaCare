package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class DoctorLogin extends AppCompatActivity {

    private EditText email , pass;
    private Button add , signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        add = findViewById(R.id.button);
        signup = findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString();
                String passInput = pass.getText().toString();

                if(emailInput.isEmpty() || passInput.isEmpty()){
                    Toast.makeText(DoctorLogin.this, "Fillup Every Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(emailInput , passInput).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(DoctorLogin.this , DoctorDashboard.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(DoctorLogin.this , "Invalid Credentials!" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signup.setOnClickListener(v -> {
           Intent intent = new Intent(DoctorLogin.this , DoctorSignUp.class);
           startActivity(intent);
           //finish();
        });
    }
}