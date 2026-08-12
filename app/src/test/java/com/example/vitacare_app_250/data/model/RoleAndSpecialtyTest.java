package com.example.vitacare_app_250.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Role and specialty key parsing.
 *
 * <p>Both used to be bare strings compared inline. The tests that matter here
 * are the negative ones: an unrecognised role must not resolve to a usable
 * value, because the old code's fallback of "patient" turned a missing role
 * record into working patient access.
 */
public class RoleAndSpecialtyTest {

    // ------------------------------------------------------------------
    // Role
    // ------------------------------------------------------------------

    @Test
    public void role_parsesStoredKeys() {
        assertEquals(Role.PATIENT, Role.fromKey("patient"));
        assertEquals(Role.DOCTOR, Role.fromKey("doctor"));
    }

    @Test
    public void role_toleratesCasingAndWhitespace() {
        assertEquals(Role.DOCTOR, Role.fromKey("  Doctor "));
        assertEquals(Role.PATIENT, Role.fromKey("PATIENT"));
    }

    @Test
    public void role_returnsNullForAnythingElse() {
        assertNull(Role.fromKey(null));
        assertNull(Role.fromKey(""));
        assertNull(Role.fromKey("admin"));
        assertNull(Role.fromKey("patients"));
    }

    @Test
    public void role_keysAreTheValuesWrittenToTheDatabase() {
        assertEquals("patient", Role.PATIENT.key());
        assertEquals("doctor", Role.DOCTOR.key());
    }

    // ------------------------------------------------------------------
    // Specialty
    // ------------------------------------------------------------------

    @Test
    public void specialty_parsesEveryKey() {
        for (Specialty specialty : Specialty.values()) {
            assertEquals(specialty, Specialty.fromKey(specialty.key()));
        }
    }

    @Test
    public void specialty_toleratesCasingAndWhitespace() {
        assertEquals(Specialty.CARDIOLOGY, Specialty.fromKey("Cardiology"));
        assertEquals(Specialty.NEPHROLOGY, Specialty.fromKey("  NEPHROLOGY  "));
    }

    @Test
    public void specialty_returnsNullForUnknownKeys() {
        assertNull(Specialty.fromKey(null));
        assertNull(Specialty.fromKey(""));
        // The old picker labelled this specialty "Medicine" while writing the
        // key "neurology"; anything not in the enum must now fail loudly rather
        // than filing a doctor under a node no screen reads.
        assertNull(Specialty.fromKey("medicine"));
        assertNull(Specialty.fromKey("select specialist"));
    }

    @Test
    public void specialty_keysAreStableStorageIdentifiers() {
        assertEquals("cardiology", Specialty.CARDIOLOGY.key());
        assertEquals("orthopedics", Specialty.ORTHOPEDICS.key());
        assertEquals("neurology", Specialty.NEUROLOGY.key());
        assertEquals("nephrology", Specialty.NEPHROLOGY.key());
    }
}
