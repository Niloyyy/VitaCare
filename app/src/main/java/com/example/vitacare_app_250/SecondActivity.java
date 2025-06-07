package com.example.vitacare_app_250;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.succcessactivity); // ✅ MUST be called first!

        //TextView textView = findViewById(R.id.successText);
        //textView.setText("Login Successful!");
    }
}
