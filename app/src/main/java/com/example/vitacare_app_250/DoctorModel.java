package com.example.vitacare_app_250;

public class DoctorModel {
    public String name, contact, degree, chamber, specialist, speciality;

    public DoctorModel() {
        // required for Firebase
    }

    public DoctorModel(String name, String contact, String degree, String chamber, String specialist, String speciality) {
        this.name = name;
        this.contact = contact;
        this.degree = degree;
        this.chamber = chamber;
        this.specialist = specialist;
        this.speciality = speciality;
    }
}