package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.utils.TransliterationUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransliterationUtilsTest {

    @Test
    void toLetterDigitCode_classCommentCase_returnsFirstLetterAndDigits() {
        // "Киев 15/23" -> "k1523"
        assertEquals("k1523", TransliterationUtils.toLetterDigitCode("Киев 15/23"));
    }

    @Test
    void toLetterDigitCode_nullOrEmpty_returnsEmpty() {
        assertEquals("", TransliterationUtils.toLetterDigitCode(null));
        assertEquals("", TransliterationUtils.toLetterDigitCode(""));
    }

    @Test
    void toLetterDigitCode_multiLetterTransliteration_keepsOnlyFirstLatinLetter() {
        assertEquals("s1", TransliterationUtils.toLetterDigitCode("Щербаки 1")); // "shch..." -> "s"
        assertEquals("z", TransliterationUtils.toLetterDigitCode("Жук"));        // "zh..." -> "z"
    }

    @Test
    void toLetterDigitCode_uppercaseIsLowercased() {
        assertEquals("k", TransliterationUtils.toLetterDigitCode("КИЕВ"));
    }

    @Test
    void toLetterDigitCode_latinInput_keepsFirstLetterAndDigits() {
        assertEquals("k1523", TransliterationUtils.toLetterDigitCode("Kyiv 15/23"));
    }

    @Test
    void toLetterDigitCode_leadingDigits_letterStillComesFirst() {
        assertEquals("k123", TransliterationUtils.toLetterDigitCode("12 Киев-3"));
        assertEquals("a7", TransliterationUtils.toLetterDigitCode("7 а"));
    }

    @Test
    void toLetterDigitCode_noDigits_returnsLetterOnly() {
        assertEquals("k", TransliterationUtils.toLetterDigitCode("Киев"));
    }

    @Test
    void toLetterDigitCode_noLetters_returnsDigitsOnly() {
        assertEquals("1523", TransliterationUtils.toLetterDigitCode("15/23"));
    }

    @Test
    void toLetterDigitCode_noLettersOrDigits_returnsEmpty() {
        assertEquals("", TransliterationUtils.toLetterDigitCode(" -/. "));
    }
}
