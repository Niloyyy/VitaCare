package com.example.vitacare_app_250.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Coercion of schemaless database values.
 *
 * <p>{@link Texts#asCount} in particular replaces a bare
 * {@code Integer.parseInt(hospital.available_beds)} that ran inside a sort
 * comparator, where a single unparseable record crashed the hospital screen.
 */
public class TextsTest {

    // ------------------------------------------------------------------
    // asString
    // ------------------------------------------------------------------

    @Test
    public void asString_passesThroughAndTrimsStrings() {
        assertEquals("Dhaka Medical", Texts.asString("  Dhaka Medical  "));
    }

    @Test
    public void asString_rendersNullAsEmpty() {
        assertEquals("", Texts.asString(null));
    }

    @Test
    public void asString_rendersWholeNumbersWithoutADecimalTail() {
        // A phone number written as a number would otherwise read
        // "1.712345678E9" or "1712345678.0".
        assertEquals("1712345678", Texts.asString(1712345678L));
        assertEquals("1712345678", Texts.asString(1712345678.0d));
        assertEquals("42", Texts.asString(42));
    }

    @Test
    public void asString_keepsGenuineFractions() {
        assertEquals("1.5", Texts.asString(1.5d));
    }

    // ------------------------------------------------------------------
    // asCount
    // ------------------------------------------------------------------

    @Test
    public void asCount_readsNumbersAndNumericStrings() {
        assertEquals(42, Texts.asCount(42L, -1));
        assertEquals(42, Texts.asCount("42", -1));
        assertEquals(42, Texts.asCount(" 42 ", -1));
        assertEquals(0, Texts.asCount("0", -1));
    }

    @Test
    public void asCount_readsALeadingNumberOutOfDecoratedText() {
        assertEquals(12, Texts.asCount("12 beds", -1));
        assertEquals(3, Texts.asCount("3/20", -1));
    }

    @Test
    public void asCount_fallsBackWhenNothingNumericIsPresent() {
        assertEquals(-1, Texts.asCount("", -1));
        assertEquals(-1, Texts.asCount(null, -1));
        assertEquals(-1, Texts.asCount("N/A", -1));
        assertEquals(-1, Texts.asCount("unknown", -1));
        assertEquals(-1, Texts.asCount("about 12", -1));
    }

    @Test
    public void asCount_treatsNegativesAsUnreported() {
        // A negative bed count is not meaningful data.
        assertEquals(-1, Texts.asCount(-5L, -1));
    }

    @Test
    public void asCount_doesNotOverflowOnAbsurdInput() {
        assertEquals(Integer.MAX_VALUE, Texts.asCount("99999999999999", -1));
    }

    // ------------------------------------------------------------------
    // collapseWhitespace
    // ------------------------------------------------------------------

    @Test
    public void collapseWhitespace_normalisesInternalRuns() {
        assertEquals("Dhaka Medical College",
                Texts.collapseWhitespace("  Dhaka   Medical\tCollege  "));
        assertEquals("", Texts.collapseWhitespace(null));
    }

    // ------------------------------------------------------------------
    // toDialable
    // ------------------------------------------------------------------

    @Test
    public void toDialable_keepsDigitsAndALeadingPlus() {
        assertEquals("+8801712345678", Texts.toDialable("+880 171-234-5678"));
        assertEquals("01712345678", Texts.toDialable("(017) 1234 5678"));
    }

    @Test
    public void toDialable_dropsAPlusThatIsNotLeading() {
        assertEquals("8801712345678", Texts.toDialable("880+1712345678"));
    }

    @Test
    public void toDialable_returnsEmptyWhenThereIsNoNumber() {
        assertEquals("", Texts.toDialable(null));
        assertEquals("", Texts.toDialable("call me"));
    }
}
