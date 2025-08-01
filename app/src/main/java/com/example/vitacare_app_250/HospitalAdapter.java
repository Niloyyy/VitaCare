package com.example.vitacare_app_250;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class HospitalAdapter extends ArrayAdapter<Hospital> {
    private final Context context;
    private final List<Hospital> hospitals;

    public HospitalAdapter(Context context, List<Hospital> hospitals) {
        super(context, 0, hospitals);
        this.context = context;
        this.hospitals = hospitals;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Hospital hospital = hospitals.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hospital, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.tvName)).setText(hospital.name);
        ((TextView) convertView.findViewById(R.id.tvAddress)).setText(hospital.address);
        ((TextView) convertView.findViewById(R.id.tvBeds)).setText("Beds: " + hospital.available_beds);
        ((TextView) convertView.findViewById(R.id.tvIcu)).setText("ICU: " + hospital.available_icu);

        return convertView;
    }
}
