package com.romanpulov.piastriawss.utils;

import java.util.HashMap;
import java.util.Map;

public class TransliterationUtils {
    private static final Map<Character, String> cyrillicToLatinMap = new HashMap<>();

    static {
        // Lowercase Cyrillic to Latin mapping
        cyrillicToLatinMap.put('а', "a"); cyrillicToLatinMap.put('б', "b");
        cyrillicToLatinMap.put('в', "v"); cyrillicToLatinMap.put('г', "h");
        cyrillicToLatinMap.put('д', "d"); cyrillicToLatinMap.put('е', "e");
        cyrillicToLatinMap.put('є', "ye"); cyrillicToLatinMap.put('ж', "zh");
        cyrillicToLatinMap.put('з', "z");  cyrillicToLatinMap.put('и', "y");
        cyrillicToLatinMap.put('і', "i");  cyrillicToLatinMap.put('ї', "yi");
        cyrillicToLatinMap.put('й', "y");  cyrillicToLatinMap.put('к', "k");
        cyrillicToLatinMap.put('л', "l");  cyrillicToLatinMap.put('м', "m");
        cyrillicToLatinMap.put('н', "n");  cyrillicToLatinMap.put('о', "o");
        cyrillicToLatinMap.put('п', "p");  cyrillicToLatinMap.put('р', "r");
        cyrillicToLatinMap.put('с', "s");  cyrillicToLatinMap.put('т', "t");
        cyrillicToLatinMap.put('у', "u");  cyrillicToLatinMap.put('ф', "f");
        cyrillicToLatinMap.put('х', "kh"); cyrillicToLatinMap.put('ц', "ts");
        cyrillicToLatinMap.put('ч', "ch"); cyrillicToLatinMap.put('ш', "sh");
        cyrillicToLatinMap.put('щ', "shch"); cyrillicToLatinMap.put('ю', "yu");
        cyrillicToLatinMap.put('я', "ya");
        // Optional additions for Russian specific characters if needed:
        cyrillicToLatinMap.put('ы', "y");  cyrillicToLatinMap.put('э', "e");
        cyrillicToLatinMap.put('ё', "yo");
    }

    /**
     * Builds a short code from a text: the first letter (transliterated to Latin, lowercase)
     * followed by all digits of the text, e.g. "Киев 15/23" -> "k1523".
     * The result starts with the letter whenever the text contains one.
     */
    public static String toLetterDigitCode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // 1. Lowercase the input string
        String lowerInput = input.toLowerCase();

        // 2. Transliterate to Latin
        StringBuilder transliterated = new StringBuilder();
        for (char c : lowerInput.toCharArray()) {
            String replacement = cyrillicToLatinMap.get(c);
            if (replacement != null) {
                transliterated.append(replacement);
            } else {
                transliterated.append(c);
            }
        }

        // 3. Take the very first letter, then all figures in their original order
        String latinString = transliterated.toString();
        StringBuilder digits = new StringBuilder();
        Character firstLetter = null;

        for (int i = 0; i < latinString.length(); i++) {
            char c = latinString.charAt(i);

            if (Character.isLetter(c)) {
                if (firstLetter == null) {
                    firstLetter = c;
                }
            } else if (Character.isDigit(c)) {
                digits.append(c);
            }
        }

        return firstLetter == null ? digits.toString() : firstLetter + digits.toString();
    }

    public static void main(String[] args) {
        // Test case: Киев 15/23 -> k1523
        String input = "Киев 15/23";
        String output = toLetterDigitCode(input);
        System.out.println("Input: " + input);
        System.out.println("Output: " + output);
    }
}
