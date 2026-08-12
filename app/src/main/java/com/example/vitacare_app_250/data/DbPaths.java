package com.example.vitacare_app_250.data;

/**
 * The database's node names, in one place.
 *
 * <p>These were previously repeated as string literals across a dozen
 * activities ({@code "docs"}, {@code "users"}, {@code "blood_donors"}, …),
 * which is how {@code speciality} and {@code specialty} ended up as two
 * different fields on the same record.
 */
public final class DbPaths {

    /** {@code users/<uid>} — account record, holds the role. */
    public static final String USERS = "users";

    /** {@code docs/<specialtyKey>/<uid>} — published practice profiles. */
    public static final String DOCTORS = "docs";

    /** {@code doctors/<pushId>} — the licence register checked at verification. */
    public static final String DOCTOR_REGISTER = "doctors";

    /** {@code hospitals/<pushId>} — bed and ICU availability. */
    public static final String HOSPITALS = "hospitals";

    /** {@code blood_donors/<pushId>} — volunteer donor register. */
    public static final String BLOOD_DONORS = "blood_donors";

    // -------------------------------------------------------------------
    // Field names. Used where a query orders by child or a snapshot is read
    // key-by-key rather than mapped onto a model class.
    // -------------------------------------------------------------------

    public static final String FIELD_ROLE = "role";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_LICENSE = "license";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PHONE = "Phone";

    private DbPaths() {
    }
}
