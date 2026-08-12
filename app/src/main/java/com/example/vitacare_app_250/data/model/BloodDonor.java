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
 * A volunteer on the blood donor register.
 *
 * <p>The field names are capitalised ({@code Name}, {@code Phone},
 * {@code BloodGroup}, {@code Location}) because that is how the existing
 * records are stored. Renaming them would orphan every donor already on the
 * register, so the storage names are pinned with {@code @PropertyName}-style
 * explicit mapping in {@link #toMap()} and {@link #fromSnapshot} while the Java
 * side keeps conventional naming.
 */
@IgnoreExtraProperties
public final class BloodDonor {

    private String id = "";
    private String name = "";
    private String phone = "";
    private String bloodGroup = "";
    private String location = "";

    /** Required by the Firebase mapper. */
    public BloodDonor() {
    }

    public BloodDonor(@NonNull String id,
                      @NonNull String name,
                      @NonNull String phone,
                      @NonNull String bloodGroup,
                      @NonNull String location) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.location = location;
    }

    /**
     * @return the donor, or {@code null} if the record lacks a name, phone
     *         number or blood group — without all three it cannot be acted on
     */
    public static @Nullable BloodDonor fromSnapshot(@Nullable DataSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists()) {
            return null;
        }
        String name = Texts.asString(snapshot.child("Name").getValue());
        String phone = Texts.asString(snapshot.child("Phone").getValue());
        String group = Texts.asString(snapshot.child("BloodGroup").getValue());
        if (name.isEmpty() || phone.isEmpty() || group.isEmpty()) {
            return null;
        }
        return new BloodDonor(
                Texts.asString(snapshot.getKey()),
                name,
                phone,
                group,
                Texts.asString(snapshot.child("Location").getValue()));
    }

    /** The map written to {@code blood_donors/<pushId>}. */
    @Exclude
    public @NonNull Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Name", name);
        map.put("Phone", phone);
        map.put("BloodGroup", bloodGroup);
        map.put("Location", location);
        return map;
    }

    @Exclude
    public @NonNull String getId() {
        return id;
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull String getPhone() {
        return phone;
    }

    /** Raw stored label; use {@link #group()} for the parsed value. */
    public @NonNull String getBloodGroup() {
        return bloodGroup;
    }

    public @NonNull String getLocation() {
        return location;
    }

    /** The parsed group, or {@code null} if the stored text is not valid. */
    @Exclude
    public @Nullable BloodGroup group() {
        return BloodGroup.fromLabel(bloodGroup);
    }

    /**
     * Display form of the group: the canonical label when it parses, otherwise
     * whatever was stored, so an odd record is still shown rather than blanked.
     */
    @Exclude
    public @NonNull String groupLabel() {
        BloodGroup parsed = group();
        return parsed != null ? parsed.label() : bloodGroup;
    }

    /**
     * Whether this donor passes the two filters on the Find a Donor screen.
     *
     * @param group    required blood group, or {@code null} for any
     * @param areaText free-text area filter; empty matches any area
     */
    @Exclude
    public boolean matches(@Nullable BloodGroup group, @Nullable String areaText) {
        if (group != null && group != group()) {
            return false;
        }
        if (areaText == null || areaText.trim().isEmpty()) {
            return true;
        }
        String needle = Texts.foldForSearch(areaText.trim());
        return Texts.foldForSearch(location).contains(needle)
                || Texts.foldForSearch(name).contains(needle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BloodDonor)) {
            return false;
        }
        BloodDonor other = (BloodDonor) o;
        return id.equals(other.id)
                && name.equals(other.name)
                && phone.equals(other.phone)
                && bloodGroup.equals(other.bloodGroup)
                && location.equals(other.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, bloodGroup, location);
    }
}
