package com.example.vitacare_app_250.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Map;

/** Donor filtering and storage mapping. */
public class BloodDonorTest {

    private static BloodDonor donor(String name, String group, String location) {
        return new BloodDonor("id", name, "01712345678", group, location);
    }

    @Test
    public void matches_filtersByGroup() {
        BloodDonor abPositive = donor("Ayesha", "AB+", "Dhaka");

        assertTrue(abPositive.matches(BloodGroup.AB_POSITIVE, null));
        assertFalse(abPositive.matches(BloodGroup.A_POSITIVE, null));
    }

    @Test
    public void matches_treatsNullGroupAsAllGroups() {
        assertTrue(donor("Ayesha", "AB+", "Dhaka").matches(null, null));
    }

    @Test
    public void matches_filtersByAreaCaseInsensitively() {
        BloodDonor ayesha = donor("Ayesha", "AB+", "Dhanmondi, Dhaka");

        assertTrue(ayesha.matches(null, "dhanmondi"));
        assertTrue(ayesha.matches(null, "DHAKA"));
        assertTrue(ayesha.matches(null, "  dhaka "));
        assertFalse(ayesha.matches(null, "sylhet"));
    }

    @Test
    public void matches_alsoSearchesTheNameSoTypingAPersonWorks() {
        assertTrue(donor("Ayesha", "AB+", "Dhaka").matches(null, "ayesha"));
    }

    @Test
    public void matches_appliesBothFiltersTogether() {
        BloodDonor ayesha = donor("Ayesha", "AB+", "Dhaka");

        assertTrue(ayesha.matches(BloodGroup.AB_POSITIVE, "dhaka"));
        assertFalse(ayesha.matches(BloodGroup.AB_POSITIVE, "sylhet"));
        assertFalse(ayesha.matches(BloodGroup.O_NEGATIVE, "dhaka"));
    }

    @Test
    public void matches_findsDonorsStoredWithAnAwkwardGroupSpelling() {
        // A record saved as " ab+ " must still answer the AB+ filter.
        assertTrue(donor("Ayesha", " ab+ ", "Dhaka")
                .matches(BloodGroup.AB_POSITIVE, null));
    }

    @Test
    public void groupLabel_normalisesWhenParseable() {
        assertEquals("AB+", donor("Ayesha", " ab+ ", "Dhaka").groupLabel());
    }

    @Test
    public void groupLabel_fallsBackToStoredTextWhenUnparseable() {
        // Better to show an odd value than to blank the field on a record
        // someone may still want to call.
        assertEquals("C+", donor("Ayesha", "C+", "Dhaka").groupLabel());
    }

    @Test
    public void toMap_usesTheCapitalisedKeysAlreadyInTheDatabase() {
        Map<String, Object> map = donor("Ayesha", "AB+", "Dhaka").toMap();

        assertEquals("Ayesha", map.get("Name"));
        assertEquals("01712345678", map.get("Phone"));
        assertEquals("AB+", map.get("BloodGroup"));
        assertEquals("Dhaka", map.get("Location"));
        assertEquals(4, map.size());
    }
}
