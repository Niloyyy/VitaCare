package com.example.vitacare_app_250.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Blood group parsing.
 *
 * <p>A donor whose group fails to parse is invisible to the group filter, so
 * the tolerance here is the difference between a donor being findable and not.
 */
public class BloodGroupTest {

    @Test
    public void fromLabel_parsesEveryCanonicalLabel() {
        for (BloodGroup group : BloodGroup.values()) {
            assertEquals(group, BloodGroup.fromLabel(group.label()));
        }
    }

    @Test
    public void fromLabel_ignoresCaseAndWhitespace() {
        assertEquals(BloodGroup.AB_POSITIVE, BloodGroup.fromLabel(" ab+ "));
        assertEquals(BloodGroup.O_NEGATIVE, BloodGroup.fromLabel("o-"));
        assertEquals(BloodGroup.A_POSITIVE, BloodGroup.fromLabel("A +"));
    }

    @Test
    public void fromLabel_foldsUnicodeDashesOntoAHyphen() {
        // Phone keyboards and copy-paste from documents readily produce these,
        // and a donor record saved with one would never match "A-".
        assertEquals(BloodGroup.A_NEGATIVE, BloodGroup.fromLabel("A−"));  // minus sign
        assertEquals(BloodGroup.A_NEGATIVE, BloodGroup.fromLabel("A–"));  // en dash
        assertEquals(BloodGroup.A_NEGATIVE, BloodGroup.fromLabel("A—"));  // em dash
    }

    @Test
    public void fromLabel_rejectsNonGroups() {
        assertNull(BloodGroup.fromLabel(null));
        assertNull(BloodGroup.fromLabel(""));
        assertNull(BloodGroup.fromLabel("   "));
        assertNull(BloodGroup.fromLabel("C+"));
        assertNull(BloodGroup.fromLabel("A"));
        assertNull(BloodGroup.fromLabel("AB"));
        assertNull(BloodGroup.fromLabel("positive"));
    }

    @Test
    public void isValid_agreesWithFromLabel() {
        assertTrue(BloodGroup.isValid("ab-"));
        assertFalse(BloodGroup.isValid("Z+"));
    }

    @Test
    public void labels_areTheEightGroupsInClinicalOrder() {
        List<String> expected = Arrays.asList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        assertEquals(expected, BloodGroup.labels());
    }

    @Test
    public void labels_useAsciiHyphensSoTheyMatchStoredRecords() {
        // A typographic minus here would render identically and silently stop
        // matching every negative-group donor already on the register.
        for (String label : BloodGroup.labels()) {
            assertFalse("label contains a non-ASCII dash: " + label,
                    label.contains("−") || label.contains("–") || label.contains("—"));
        }
    }

    @Test
    public void labelsRoundTripThroughFromLabel() {
        // The picker and the filter chips both display labels() and feed the
        // selection back through fromLabel; this is that loop.
        for (String label : BloodGroup.labels()) {
            assertEquals(label, BloodGroup.fromLabel(label).label());
        }
    }
}
