package com.example.vitacare_app_250.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Map;

/** Doctor search, publishability and storage mapping. */
public class DoctorTest {

    private static Doctor doctor(String name, String degree, String focus) {
        return new Doctor("uid", name, "01712345678", degree,
                "Chamber Road", Specialty.CARDIOLOGY.key(), focus);
    }

    @Test
    public void matches_searchesNameQualificationAndFocus() {
        Doctor ayesha = doctor("Ayesha Rahman", "MBBS, FCPS", "Heart surgery");

        assertTrue(ayesha.matches("ayesha"));
        assertTrue(ayesha.matches("rahman"));
        assertTrue(ayesha.matches("fcps"));
        assertTrue(ayesha.matches("HEART"));
        assertFalse(ayesha.matches("orthopaedic"));
    }

    @Test
    public void matches_treatsAnEmptyQueryAsNoFilter() {
        Doctor ayesha = doctor("Ayesha Rahman", "MBBS", "Heart surgery");

        assertTrue(ayesha.matches(""));
        assertTrue(ayesha.matches("   "));
        assertTrue(ayesha.matches(null));
    }

    @Test
    public void specialty_resolvesTheStoredKey() {
        assertEquals(Specialty.CARDIOLOGY,
                doctor("Ayesha", "MBBS", "Heart surgery").specialty());
    }

    @Test
    public void isPublishable_requiresNameContactAndSpecialty() {
        assertTrue(doctor("Ayesha", "MBBS", "Heart surgery").isPublishable());

        Doctor noContact = new Doctor("uid", "Ayesha", "", "MBBS",
                "Chamber", Specialty.CARDIOLOGY.key(), "Heart");
        assertFalse(noContact.isPublishable());

        Doctor noSpecialty = new Doctor("uid", "Ayesha", "01712345678", "MBBS",
                "Chamber", "", "Heart");
        assertFalse(noSpecialty.isPublishable());
    }

    @Test
    public void toMap_writesTheFieldsTheDirectoryReads() {
        Map<String, Object> map = doctor("Ayesha", "MBBS", "Heart surgery").toMap();

        assertEquals("Ayesha", map.get("name"));
        assertEquals("01712345678", map.get("contact"));
        assertEquals("MBBS", map.get("degree"));
        assertEquals("Chamber Road", map.get("chamber"));
        assertEquals("cardiology", map.get("specialist"));
        assertEquals("Heart surgery", map.get("speciality"));
    }

    @Test
    public void toMap_omitsTheIdBecauseItIsTheRecordKey() {
        // The uid is the node name under docs/<specialty>/, not a child field;
        // writing it twice would let the two drift apart.
        assertFalse(doctor("Ayesha", "MBBS", "Heart").toMap().containsKey("id"));
    }

    @Test
    public void settersFoldNullOntoEmptySoGettersNeverReturnNull() {
        Doctor blank = new Doctor();
        blank.setName(null);
        blank.setContact(null);
        blank.setDegree(null);
        blank.setChamber(null);
        blank.setSpecialist(null);
        blank.setSpeciality(null);

        assertEquals("", blank.getName());
        assertEquals("", blank.getContact());
        assertEquals("", blank.getDegree());
        assertEquals("", blank.getChamber());
        assertEquals("", blank.getSpecialist());
        assertEquals("", blank.getSpeciality());
    }
}
