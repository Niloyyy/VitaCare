package com.example.vitacare_app_250;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail , loginPassword;
    private TextView sighnupRedirectText;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.phone);
        loginPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.button);
        sighnupRedirectText = findViewById(R.id.signup_text);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(email , pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(MainActivity.this , "Login successful" , Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this , MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this , "Login failed" , Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    else{
                        loginPassword.setError("Password can't be blank");
                    }
                }else if(email.isEmpty()){
                    loginEmail.setError("Email can't be empty");
                }else{
                    loginEmail.setError("Please enter a valid email");
                }
            }
        });

        sighnupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , SignupActivity.class));
            }
        });
    }
}

