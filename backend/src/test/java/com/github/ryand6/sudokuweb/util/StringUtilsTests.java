package com.github.ryand6.sudokuweb.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTests {

    // -----------------------------
    // normaliseString() tests
    // -----------------------------

    @Test
    void testNormaliseStringSimple() {
        assertEquals("hello", StringUtils.normaliseString("hello"));
    }

    @Test
    void testNormaliseStringUppercase() {
        assertEquals("hello", StringUtils.normaliseString("HELLO"));
    }

    @Test
    void testNormaliseStringLeetspeak() {
        // Using CHAR_SUBSTITUTIONS: "h3ll0" -> "hello", "t3$7" -> "test"
        assertEquals("hello", StringUtils.normaliseString("h3ll0"));
        assertEquals("test", StringUtils.normaliseString("t3$7"));
        assertEquals("bigcat", StringUtils.normaliseString("819<@7"));
    }

    @Test
    void testNormaliseStringWhitespace() {
        assertEquals("hello", StringUtils.normaliseString("  hello  "));
        assertEquals("", StringUtils.normaliseString("   "));
    }

    @Test
    void testNormaliseStringEmpty() {
        assertEquals("", StringUtils.normaliseString(""));
    }

    @Test
    void testNormaliseStringSpecialChars() {
        assertEquals("aib", StringUtils.normaliseString("a!b"));
    }

    // -----------------------------
    // removeNonAlphabeticChars() tests
    // -----------------------------

    @Test
    void testRemoveNonAlphabeticCharsLettersOnly() {
        assertEquals("abc", StringUtils.removeNonAlphabeticChars("abc"));
        assertEquals("ABC", StringUtils.removeNonAlphabeticChars("ABC"));
    }

    @Test
    void testRemoveNonAlphabeticCharsMixed() {
        assertEquals("abc", StringUtils.removeNonAlphabeticChars("a1b2c3"));
        assertEquals("heo", StringUtils.removeNonAlphabeticChars("he!!o"));
        assertEquals("test", StringUtils.removeNonAlphabeticChars("t_e-s+t"));
    }

    @Test
    void testRemoveNonAlphabeticCharsEmptyOrNone() {
        assertEquals("", StringUtils.removeNonAlphabeticChars(""));
        assertEquals("", StringUtils.removeNonAlphabeticChars("123456!@#"));
    }

    // -----------------------------
    // isAsciiString() tests
    // -----------------------------

    @Test
    void testIsAsciiStringAllAscii() {
        assertTrue(StringUtils.isAsciiString("Hello123!@#"));
        assertTrue(StringUtils.isAsciiString(""));
        assertTrue(StringUtils.isAsciiString(" \n\t"));
    }

    @Test
    void testIsAsciiStringNonAscii() {
        assertFalse(StringUtils.isAsciiString("café")); // 'é' > 127
        assertFalse(StringUtils.isAsciiString("こんにちは")); // Japanese chars
        assertFalse(StringUtils.isAsciiString("a\u20ACb")); // contains € symbol
    }

    // -----------------------------
    // Null tests
    // -----------------------------

    @Test
    void testNormaliseStringNullInput() {
        assertThrows(NullPointerException.class, () -> StringUtils.normaliseString(null));
    }

    @Test
    void testRemoveNonAlphabeticCharsNullInput() {
        assertThrows(NullPointerException.class, () -> StringUtils.removeNonAlphabeticChars(null));
    }

    @Test
    void testIsAsciiStringNullInput() {
        assertThrows(NullPointerException.class, () -> StringUtils.isAsciiString(null));
    }

}
