package io;

import java.util.Arrays;
import java.util.Locale;

public class LocalizerUnicodeEscaped {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
//        System.out.println("\u00E2\u20AC\u2122");
//        System.out.println("\u00D1\uFFFD");

        System.out.println(Arrays.toString(Character.toString('\u2019').toCharArray()));
    }
}
