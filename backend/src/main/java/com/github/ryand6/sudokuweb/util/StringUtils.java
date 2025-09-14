package com.github.ryand6.sudokuweb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StringUtils {

    // Immutable static map used for normalisation
    private static final Map<String, List<String>> CHAR_SUBSTITUTIONS;

    static {
        // Standard hashmap is mutable
        Map<String, List<String>> tempMap = new HashMap<>();
        tempMap.put("a", List.of("@", "4"));
        tempMap.put("e", List.of("3"));
        tempMap.put("i", List.of("1", "!"));
        tempMap.put("o", List.of("0"));
        tempMap.put("b", List.of("8", "6"));
        tempMap.put("c", List.of("<"));
        tempMap.put("g", List.of("9"));
        tempMap.put("s", List.of("$", "5"));
        tempMap.put("t", List.of("7", "+"));

        // Make immutable copy of map and store as constant
        CHAR_SUBSTITUTIONS = Map.copyOf(tempMap);
    }

    // private constructor prevents class instantiation
    private StringUtils() {};

    // Convert string to lowercase, strip leading and trailing whitespace, substitute leetspeak chars
    public static String normaliseString(String s) {
        s = s.toLowerCase().strip();
        return s.codePoints().mapToObj(c -> new String(Character.toChars(c))).map(StringUtils::substituteChar).collect(Collectors.joining());
    }

    // Loop through substitutions map to see if char is found in any of the value sets, and if so replace with the key
    private static String substituteChar(String c) {
        for (String key: CHAR_SUBSTITUTIONS.keySet()) {
            if (CHAR_SUBSTITUTIONS.get(key).contains(c)) {
                return key;
            }
        }
        return c;
    }

    public static String removeNonAlphabeticChars(String s) {
        return s.replaceAll("[^A-Za-z]", "");
    }

    // Check that all characters in string within ASCII range by checking their unicode values (ASCII = 0 to 127)
    public static boolean isAsciiString(String s) {
        return s.chars().allMatch(c -> c < 128);
    }

}
