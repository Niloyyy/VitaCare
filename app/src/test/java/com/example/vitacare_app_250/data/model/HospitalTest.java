package com.example.vitacare_app_250.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Hospital availability banding, sorting and search.
 */
public class HospitalTest {

    private static Hospital hospital(String name, int beds, int icu) {
        return new Hospital(name, name, "somewhere", "", beds, icu);
    }

    // ------------------------------------------------------------------
    // Availability
    // ------------------------------------------------------------------

    @Test
    public void availability_reportsAvailableWhenBedsAreComfortable() {
        assertEquals(Hospital.Availability.AVAILABLE, hospital("A", 40, 5).availability());
    }

    @Test
    public void availability_reportsLimitedNearTheThreshold() {
        assertEquals(Hospital.Availability.LIMITED, hospital("A", 2, 1).availability());
    }

    @Test
    public void availability_reportsFullAtZero() {
        assertEquals(Hospital.Availability.FULL, hospital("A", 0, 0).availability());
    }

    @Test
    public void availability_distinguishesUnreportedFromZero() {
        // "We have no beds" and "we have not said" must not look alike to
        // someone deciding where to go.
        assertEquals(Hospital.Availability.UNKNOWN_STATUS,
                hospital("A", Hospital.UNKNOWN, Hospital.UNKNOWN).availability());
        assertEquals(Hospital.Availability.FULL, hospital("A", 0, 0).availability());
    }

    @Test
    public void availability_usesWhicheverCountIsReported() {
        assertEquals(Hospital.Availability.AVAILABLE,
                hospital("A", Hospital.UNKNOWN, 9).availability());
    }

    @Test
    public void hasBedData_reflectsWhetherEitherCountIsKnown() {
        assertTrue(hospital("A", 0, Hospital.UNKNOWN).hasBedData());
        assertFalse(hospital("A", Hospital.UNKNOWN, Hospital.UNKNOWN).hasBedData());
    }

    // ------------------------------------------------------------------
    // Sorting
    // ------------------------------------------------------------------

    @Test
    public void bedsSort_ordersDescendingWithUnreportedLast() {
        List<Hospital> hospitals = new ArrayList<>(Arrays.asList(
                hospital("Unknown", Hospital.UNKNOWN, Hospital.UNKNOWN),
                hospital("Few", 3, 0),
                hospital("Many", 30, 2),
                hospital("None", 0, 0)));

        Collections.sort(hospitals, (a, b) ->
                Integer.compare(b.bedsSortKey(), a.bedsSortKey()));

        assertEquals("Many", hospitals.get(0).getName());
        assertEquals("Few", hospitals.get(1).getName());
        assertEquals("None", hospitals.get(2).getName());
        // A hospital that reported nothing must not outrank one that honestly
        // reported zero.
        assertEquals("Unknown", hospitals.get(3).getName());
    }

    @Test
    public void icuSort_ordersDescendingWithUnreportedLast() {
        List<Hospital> hospitals = new ArrayList<>(Arrays.asList(
                hospital("Unknown", 5, Hospital.UNKNOWN),
                hospital("Some", 5, 4)));

        Collections.sort(hospitals, (a, b) ->
                Integer.compare(b.icuSortKey(), a.icuSortKey()));

        assertEquals("Some", hospitals.get(0).getName());
        assertEquals("Unknown", hospitals.get(1).getName());
    }

    // ------------------------------------------------------------------
    // Search
    // ------------------------------------------------------------------

    @Test
    public void matches_isCaseInsensitiveAcrossNameAndAddress() {
        Hospital dmch = new Hospital(
                "1", "Dhaka Medical College", "Secretariat Road, Dhaka", "", 10, 2);

        assertTrue(dmch.matches("dhaka"));
        assertTrue(dmch.matches("MEDICAL"));
        assertTrue(dmch.matches("secretariat"));
        assertTrue(dmch.matches("  college  "));
        assertFalse(dmch.matches("chittagong"));
    }

    @Test
    public void matches_treatsAnEmptyQueryAsNoFilter() {
        Hospital any = hospital("Anything", 1, 1);
        assertTrue(any.matches(""));
        assertTrue(any.matches("   "));
        assertTrue(any.matches(null));
    }
}
