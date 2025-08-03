public class Doctor {
    public String name;
    public String speciality;
    public String degree;
    public String appointment;

    public Doctor() {
        // Default constructor required for calls to DataSnapshot.getValue(Doctor.class)
    }

    public Doctor(String name, String speciality, String degree, String appointment) {
        this.name = name;
        this.speciality = speciality;
        this.degree = degree;
        this.appointment = appointment;
    }
}