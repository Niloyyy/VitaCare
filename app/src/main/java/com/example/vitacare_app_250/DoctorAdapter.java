package com.example.vitacare_app_250;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private final ArrayList<HashMap<String, String>> doctorList;

    public DoctorAdapter(ArrayList<HashMap<String, String>> doctorList) {
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        HashMap<String, String> doctor = doctorList.get(position);

        String name = doctor.getOrDefault("name", "No Name");
        String degree = doctor.getOrDefault("degree", "No Degree Info");
        String contact = doctor.getOrDefault("contact", "No Contact Info");

        String speciality = doctor.get("speciality");
        if (speciality == null) {
            speciality = doctor.getOrDefault("specialty", "No Speciality");
        }

        String field = doctor.getOrDefault("field", "No Field Info");
        String chamber = doctor.getOrDefault("chamber", "No Chamber Info");

        holder.nameText.setText("Dr. " + name);
        holder.degreeText.setText("Degree: " + degree);
        holder.contactText.setText("Phone: " + contact);
        holder.specialityText.setText("Speciality: " + speciality);

        final String finalSpeciality = speciality != null ? speciality : "No Speciality";

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DoctorProfileActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("degree", degree);
            intent.putExtra("contact", contact);
            intent.putExtra("speciality", finalSpeciality);
            intent.putExtra("field", field);
            intent.putExtra("chamber", chamber);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, degreeText, contactText, specialityText;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textDoctorName);
            degreeText = itemView.findViewById(R.id.textDoctorDegree);
            contactText = itemView.findViewById(R.id.textDoctorContact);
            specialityText = itemView.findViewById(R.id.textDoctorSpeciality);
        }
    }
}
