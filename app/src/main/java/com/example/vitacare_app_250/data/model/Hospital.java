package com.example.vitacare_app_250.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.util.Texts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

/**
 * A hospital and its reported bed availability.
 *
 * <p>Bed counts arrive as strings. The previous sort called
 * {@code Integer.parseInt} directly on them, so one record with a blank,
 * "N/A" or "12 free" value crashed the whole hospital screen the moment the
 * user tapped Sort. Parsing happens once here, into {@link #availableBeds()}
 * and {@link #availableIcu()}, with {@link #UNKNOWN} standing in for anything
 * unparseable.
 */
@IgnoreExtraProperties
public final class Hospital {

    /** Sentinel for a count the hospital has not reported. */
    public static final int UNKNOWN = -1;

    /** Below this many free beds the hospital reads as "limited". */
    private static final int LIMITED_THRESHOLD = 5;

    private String id = "";
    private String name = "";
    private String address = "";
    private String contact = "";
    private int beds = UNKNOWN;
    private int icu = UNKNOWN;

    /** Required by the Firebase mapper. */
    public Hospital() {
    }

    public Hospital(@NonNull String id,
                    @NonNull String name,
                    @NonNull String address,
                    @NonNull String contact,
                    int beds,
                    int icu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.beds = beds;
        this.icu = icu;
    }

    /**
     * Reads a hospital from a snapshot.
     *
     * @return the hospital, or {@code null} if the record has no name
     */
    public static @Nullable Hospital fromSnapshot(@Nullable DataSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists()) {
            return null;
        }
        String name = Texts.asString(snapshot.child("name").getValue());
        if (name.isEmpty()) {
            return null;
        }
        return new Hospital(
                Texts.asString(snapshot.getKey()),
                name,
                Texts.asString(snapshot.child("address").getValue()),
                Texts.asString(snapshot.child("contact").getValue()),
                Texts.asCount(snapshot.child("available_beds").getValue(), UNKNOWN),
                Texts.asCount(snapshot.child("available_icu").getValue(), UNKNOWN));
    }

    @Exclude
    public @NonNull String getId() {
        return id;
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull String getAddress() {
        return address;
    }

    public @NonNull String getContact() {
        return contact;
    }

    /** Free general beds, or {@link #UNKNOWN}. */
    @Exclude
    public int availableBeds() {
        return beds;
    }

    /** Free ICU beds, or {@link #UNKNOWN}. */
    @Exclude
    public int availableIcu() {
        return icu;
    }

    @Exclude
    public boolean hasBedData() {
        return beds != UNKNOWN || icu != UNKNOWN;
    }

    /**
     * Sort key for "most beds first". Unreported counts sort last rather than
     * ahead of hospitals that genuinely have none free.
     */
    @Exclude
    public int bedsSortKey() {
        return beds == UNKNOWN ? Integer.MIN_VALUE : beds;
    }

    @Exclude
    public int icuSortKey() {
        return icu == UNKNOWN ? Integer.MIN_VALUE : icu;
    }

    /** Coarse availability, used to pick the status pill's colour and label. */
    @Exclude
    public @NonNull Availability availability() {
        if (!hasBedData()) {
            return Availability.UNKNOWN_STATUS;
        }
        int free = Math.max(beds, 0) + Math.max(icu, 0);
        if (free <= 0) {
            return Availability.FULL;
        }
        return free < LIMITED_THRESHOLD ? Availability.LIMITED : Availability.AVAILABLE;
    }

    /** Case-insensitive match on name or address. Empty query matches all. */
    @Exclude
    public boolean matches(@Nullable String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        String needle = Texts.foldForSearch(query.trim());
        return Texts.foldForSearch(name).contains(needle)
                || Texts.foldForSearch(address).contains(needle);
    }

    /** How full a hospital is, at the granularity the UI shows. */
    public enum Availability {
        AVAILABLE,
        LIMITED,
        FULL,
        UNKNOWN_STATUS
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hospital)) {
            return false;
        }
        Hospital other = (Hospital) o;
        return beds == other.beds
                && icu == other.icu
                && id.equals(other.id)
                && name.equals(other.name)
                && address.equals(other.address)
                && contact.equals(other.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, contact, beds, icu);
    }
}
