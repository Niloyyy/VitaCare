package com.example.vitacare_app_250;

public class Hospital {
    public String name;
    public String address;
    public String available_beds;
    public String available_icu;

    public Hospital() {
        // Required empty constructor for Firebase
    }

    public Hospital(String name, String address, String available_beds, String available_icu) {
        this.name = name;
        this.address = address;
        this.available_beds = available_beds;
        this.available_icu = available_icu;
    }
}
