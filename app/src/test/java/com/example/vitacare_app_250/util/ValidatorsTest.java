package com.example.vitacare_app_250.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.vitacare_app_250.R;

import org.junit.Test;

/**
 * Rules that decide whether a form submits.
 *
 * <p>None of this was testable before: the old {@code ValidationUtils} took
 * {@code EditText} parameters and delegated to {@code android.util.Patterns},
 * which returns null under a plain JVM test. Tests written against it would
 * have thrown or passed vacuously.
 */
public class ValidatorsTest {

    // ------------------------------------------------------------------
    // Email
    // ------------------------------------------------------------------

    @Test
    public void email_acceptsOrdinaryAddresses() {
        assertTrue(Validators.isValid(Validators.email("ayesha@example.com")));
        assertTrue(Validators.isValid(Validators.email("first.last+tag@sub.example.co.uk")));
        assertTrue(Validators.isValid(Validators.email("a_b-c%d@example.org")));
    }

    @Test
    public void email_trimsSurroundingWhitespace() {
        assertTrue(Validators.isValid(Validators.email("  ayesha@example.com  ")));
    }

    @Test
    public void email_rejectsMalformedAddresses() {
        assertEquals(R.string.error_invalid_email, Validators.email("ayesha"));
        assertEquals(R.string.error_invalid_email, Validators.email("ayesha@"));
        assertEquals(R.string.error_invalid_email, Validators.email("@example.com"));
        assertEquals(R.string.error_invalid_email, Validators.email("ayesha@example"));
        assertEquals(R.string.error_invalid_email, Validators.email("ayesha @example.com"));
        assertEquals(R.string.error_invalid_email, Validators.email("two@@example.com"));
    }

    @Test
    public void email_reportsBlankAsRequiredNotMalformed() {
        // The distinction matters: "this field is required" and "that isn't a
        // valid address" call for different corrections.
        assertEquals(R.string.error_required, Validators.email(""));
        assertEquals(R.string.error_required, Validators.email("   "));
        assertEquals(R.string.error_required, Validators.email(null));
    }

    // ------------------------------------------------------------------
    // Password
    // ------------------------------------------------------------------

    @Test
    public void password_acceptsWhenLongEnoughWithDigitAndSymbol() {
        assertTrue(Validators.isValid(Validators.password("Str0ng!pass")));
        assertTrue(Validators.isValid(Validators.password("12345678!")));
        assertTrue(Validators.isValid(Validators.password("a1!bcdef")));
    }

    @Test
    public void password_rejectsShortInput() {
        assertEquals(R.string.error_weak_password, Validators.password("A1!bcde"));
    }

    @Test
    public void password_requiresADigit() {
        assertEquals(R.string.error_weak_password, Validators.password("abcdefgh!"));
    }

    @Test
    public void password_requiresASymbol() {
        assertEquals(R.string.error_weak_password, Validators.password("abcdefg1"));
    }

    @Test
    public void password_rejectsWhitespaceAnywhere() {
        assertEquals(R.string.error_weak_password, Validators.password("a1! bcdef"));
        assertEquals(R.string.error_weak_password, Validators.password(" a1!bcdef"));
    }

    @Test
    public void password_acceptsAnyNonAlphanumericAsTheSymbol() {
        // The old pattern hardcoded [!@#$%^&+=*], silently rejecting passwords
        // containing only, say, a hyphen or a full stop.
        assertTrue(Validators.isValid(Validators.password("abcdefg1-")));
        assertTrue(Validators.isValid(Validators.password("abcdefg1.")));
        assertTrue(Validators.isValid(Validators.password("abcdefg1_")));
        assertTrue(Validators.isValid(Validators.password("abcdefg1£")));
    }

    @Test
    public void password_reportsBlankAsRequired() {
        assertEquals(R.string.error_required, Validators.password(""));
        assertEquals(R.string.error_required, Validators.password(null));
    }

    // ------------------------------------------------------------------
    // Confirmation
    // ------------------------------------------------------------------

    @Test
    public void confirmation_matchesExactly() {
        assertTrue(Validators.isValid(
                Validators.passwordConfirmation("Str0ng!pass", "Str0ng!pass")));
    }

    @Test
    public void confirmation_isCaseSensitive() {
        assertEquals(R.string.error_password_mismatch,
                Validators.passwordConfirmation("Str0ng!pass", "str0ng!pass"));
    }

    @Test
    public void confirmation_reportsBlankAsRequired() {
        assertEquals(R.string.error_required,
                Validators.passwordConfirmation("Str0ng!pass", ""));
    }

    @Test
    public void confirmation_doesNotJudgeStrength() {
        // Two matching weak passwords: this rule passes and password() is what
        // reports the weakness, against the field that owns it.
        assertTrue(Validators.isValid(Validators.passwordConfirmation("abc", "abc")));
    }

    // ------------------------------------------------------------------
    // Phone
    // ------------------------------------------------------------------

    @Test
    public void phone_acceptsLocalAndInternationalForms() {
        assertTrue(Validators.isValid(Validators.phone("01712345678")));
        assertTrue(Validators.isValid(Validators.phone("+8801712345678")));
        assertTrue(Validators.isValid(Validators.phone("+880 171 234 5678")));
        assertTrue(Validators.isValid(Validators.phone("(017) 1234-5678")));
    }

    @Test
    public void phone_rejectsTooFewDigits() {
        assertEquals(R.string.error_invalid_phone, Validators.phone("12345"));
        assertEquals(R.string.error_invalid_phone, Validators.phone("017123456"));
    }

    @Test
    public void phone_rejectsTooManyDigits() {
        assertEquals(R.string.error_invalid_phone, Validators.phone("1234567890123456"));
    }

    @Test
    public void phone_rejectsLetters() {
        assertEquals(R.string.error_invalid_phone, Validators.phone("0171234567a"));
    }

    @Test
    public void phone_reportsBlankAsRequired() {
        assertEquals(R.string.error_required, Validators.phone(""));
        assertEquals(R.string.error_required, Validators.phone(null));
    }

    // ------------------------------------------------------------------
    // Name and licence
    // ------------------------------------------------------------------

    @Test
    public void fullName_acceptsRealNames() {
        assertTrue(Validators.isValid(Validators.fullName("Ayesha Rahman")));
        assertTrue(Validators.isValid(Validators.fullName("Li")));
    }

    @Test
    public void fullName_rejectsSingleCharacter() {
        assertEquals(R.string.error_name_too_short, Validators.fullName("A"));
    }

    @Test
    public void fullName_treatsWhitespaceOnlyAsBlank() {
        assertEquals(R.string.error_required, Validators.fullName("   "));
    }

    @Test
    public void licence_requiresExactLength() {
        assertTrue(Validators.isValid(Validators.licence("ABC1234")));
        assertEquals(R.string.error_invalid_licence, Validators.licence("ABC123"));
        assertEquals(R.string.error_invalid_licence, Validators.licence("ABC12345"));
    }

    @Test
    public void licence_reportsBlankAsRequired() {
        assertEquals(R.string.error_required, Validators.licence(""));
    }

    // ------------------------------------------------------------------
    // Required
    // ------------------------------------------------------------------

    @Test
    public void required_rejectsBlankAndWhitespace() {
        assertFalse(Validators.isValid(Validators.required("")));
        assertFalse(Validators.isValid(Validators.required("  \t ")));
        assertFalse(Validators.isValid(Validators.required(null)));
        assertTrue(Validators.isValid(Validators.required("x")));
    }
}
