package com.example.vitacare_app_250.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.util.Texts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A doctor's published practice profile.
 *
 * <p>Replaces the untyped {@code HashMap<String, String>} the list screen used
 * to pass around. That map was cast straight out of a {@code DataSnapshot}, so
 * a single numeric value written by any other client — a phone number stored as
 * a number rather than a string — would have thrown {@code ClassCastException}
 * deep inside the adapter's bind. Reading field-by-field through
 * {@link #fromSnapshot} cannot fail that way.
 *
 * <p>Records are keyed by the doctor's Firebase uid at
 * {@code docs/<specialty>/<uid>}, so re-saving updates the profile instead of
 * appending another copy.
 */
@IgnoreExtraProperties
public final class Doctor {

    private String id = "";
    private String name = "";
    private String contact = "";
    private String degree = "";
    private String chamber = "";
    /** Specialty key, e.g. {@code "cardiology"}. */
    private String specialist = "";
    /** Free-text focus area shown under the specialty, e.g. "Heart surgeon". */
    private String speciality = "";

    /** Required by the Firebase mapper. */
    public Doctor() {
    }

    public Doctor(@NonNull String id,
                  @NonNull String name,
                  @NonNull String contact,
                  @NonNull String degree,
                  @NonNull String chamber,
                  @NonNull String specialist,
                  @NonNull String speciality) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.degree = degree;
        this.chamber = chamber;
        this.specialist = specialist;
        this.speciality = speciality;
    }

    /**
     * Reads a doctor from a snapshot, defensively.
     *
     * <p>Every field is coerced through {@link Texts#asString}, so a value
     * stored with the wrong type yields an empty string rather than an
     * exception.
     *
     * @return the doctor, or {@code null} if the record has no usable name
     */
    public static @Nullable Doctor fromSnapshot(@Nullable DataSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists()) {
            return null;
        }
        String name = Texts.asString(snapshot.child("name").getValue());
        if (name.isEmpty()) {
            // A nameless doctor cannot be presented or contacted meaningfully.
            return null;
        }

        // "speciality" is the field the doctor form writes; "specialty" exists
        // on older records created before the spelling was settled. Read both.
        String focus = Texts.asString(snapshot.child("speciality").getValue());
        if (focus.isEmpty()) {
            focus = Texts.asString(snapshot.child("specialty").getValue());
        }

        return new Doctor(
                Texts.asString(snapshot.getKey()),
                name,
                Texts.asString(snapshot.child("contact").getValue()),
                Texts.asString(snapshot.child("degree").getValue()),
                Texts.asString(snapshot.child("chamber").getValue()),
                Texts.asString(snapshot.child("specialist").getValue()),
                focus);
    }

    /** The map written to {@code docs/<specialty>/<uid>}. */
    @Exclude
    public @NonNull Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("contact", contact);
        map.put("degree", degree);
        map.put("chamber", chamber);
        map.put("specialist", specialist);
        map.put("speciality", speciality);
        return map;
    }

    /** Firebase uid; also the record's key. Not stored as a child field. */
    @Exclude
    public @NonNull String getId() {
        return id;
    }

    @Exclude
    public void setId(@NonNull String id) {
        this.id = id;
    }

    public @NonNull String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = Texts.orEmpty(name);
    }

    public @NonNull String getContact() {
        return contact;
    }

    public void setContact(@Nullable String contact) {
        this.contact = Texts.orEmpty(contact);
    }

    public @NonNull String getDegree() {
        return degree;
    }

    public void setDegree(@Nullable String degree) {
        this.degree = Texts.orEmpty(degree);
    }

    public @NonNull String getChamber() {
        return chamber;
    }

    public void setChamber(@Nullable String chamber) {
        this.chamber = Texts.orEmpty(chamber);
    }

    public @NonNull String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(@Nullable String specialist) {
        this.specialist = Texts.orEmpty(specialist);
    }

    public @NonNull String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(@Nullable String speciality) {
        this.speciality = Texts.orEmpty(speciality);
    }

    /** The parsed specialty, or {@code null} if the stored key is unknown. */
    @Exclude
    public @Nullable Specialty specialty() {
        return Specialty.fromKey(specialist);
    }

    /**
     * Whether {@code query} matches this doctor by name, qualification or
     * focus area. Case-insensitive; an empty query matches everything.
     */
    @Exclude
    public boolean matches(@Nullable String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        String needle = Texts.foldForSearch(query.trim());
        return Texts.foldForSearch(name).contains(needle)
                || Texts.foldForSearch(degree).contains(needle)
                || Texts.foldForSearch(speciality).contains(needle);
    }

    /** True when enough is present to be worth listing. */
    @Exclude
    public boolean isPublishable() {
        return !name.isEmpty() && !contact.isEmpty() && !specialist.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Doctor)) {
            return false;
        }
        Doctor other = (Doctor) o;
        return id.equals(other.id)
                && name.equals(other.name)
                && contact.equals(other.contact)
                && degree.equals(other.degree)
                && chamber.equals(other.chamber)
                && specialist.equals(other.specialist)
                && speciality.equals(other.speciality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contact, degree, chamber, specialist, speciality);
    }
}
