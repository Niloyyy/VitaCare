package com.example.vitacare_app_250;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class DoctorInfoActivity extends AppCompatActivity {

    ImageView searchIcon;
    EditText searchEditText;
    ListView listView;
    ArrayAdapter<String> adapter;

    String[] doctors = {
            "Dr. Dipu Debnath",
            "Dr. Husne Ara",
            "Dr. MD Masum",
            "Dr. Sadia Sultana",
            "Dr. Forhad Rabbi",
            "Dr. HUMA",
            "Dr. Bayzid"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);

        searchIcon = findViewById(R.id.searchIcon);
        searchEditText = findViewById(R.id.searchEditText);
        listView = findViewById(R.id.listViewDoctors);

        ArrayList<String> doctorList = new ArrayList<>(Arrays.asList(doctors));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, doctorList);
        listView.setAdapter(adapter);

        // When search icon is clicked, show the EditText and focus it
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchIcon.setVisibility(View.GONE);
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();

                // Optional: Show keyboard
                // InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                // imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // Listen for text input in search EditText and filter list accordingly
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}

            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }
}
