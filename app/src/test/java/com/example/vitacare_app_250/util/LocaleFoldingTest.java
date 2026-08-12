package com.example.vitacare_app_250.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.data.model.Specialty;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

/**
 * Case folding under a locale with unusual casing rules.
 *
 * <p>Turkish is the standard counterexample: {@code "I".toLowerCase()} gives a
 * dotless {@code ı}, not {@code i}. With the default locale in force, a Turkish
 * device parsing the stored key {@code "CARDIOLOGY"} produced
 * {@code "cardıology"}, which matches no specialty — every cardiologist would
 * have silently vanished from the directory on those devices, and a doctor's
 * role of {@code "PATIENT"} would have failed to resolve at sign-in.
 */
public class LocaleFoldingTest {

    private Locale original;

    @Before
    public void setUp() {
        original = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));
    }

    @After
    public void tearDown() {
        Locale.setDefault(original);
    }

    @Test
    public void turkishLocaleReallyDoesFoldDottedIDifferently() {
        // Guards the premise: if this ever stops holding, the tests below stop
        // proving anything.
        assertEquals("cardıology", "CARDIOLOGY".toLowerCase());
    }

    @Test
    public void specialtyKeysParseUnderTurkishLocale() {
        assertEquals(Specialty.CARDIOLOGY, Specialty.fromKey("CARDIOLOGY"));
        assertEquals(Specialty.CARDIOLOGY, Specialty.fromKey("Cardiology"));
        assertEquals(Specialty.ORTHOPEDICS, Specialty.fromKey("ORTHOPEDICS"));
        assertEquals(Specialty.NEPHROLOGY, Specialty.fromKey("NEPHROLOGY"));
        assertEquals(Specialty.NEUROLOGY, Specialty.fromKey("NEUROLOGY"));
    }

    @Test
    public void roleKeysParseUnderTurkishLocale() {
        assertEquals(Role.PATIENT, Role.fromKey("PATIENT"));
        assertEquals(Role.DOCTOR, Role.fromKey("DOCTOR"));
    }

    @Test
    public void foldKeyIsLocaleIndependent() {
        assertEquals("cardiology", Texts.foldKey("CARDIOLOGY"));
        assertEquals("patient", Texts.foldKey("  PATIENT  "));
    }

    @Test
    public void searchStillMatchesWhenBothSidesFoldTheSameWay() {
        // foldForSearch deliberately uses the default locale, so a Turkish user
        // searching Turkish text gets Turkish folding on both sides.
        Doctor doctor = new Doctor("uid", "IŞIL Demir", "01712345678",
                "MBBS", "Chamber", Specialty.CARDIOLOGY.key(), "Kardiyoloji");

        assertTrue(doctor.matches("ışıl"));
        assertTrue(doctor.matches("IŞIL"));
        assertTrue(doctor.matches("demir"));
    }
}
