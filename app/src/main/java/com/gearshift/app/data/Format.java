package com.gearshift.app.data;

import java.text.NumberFormat;
import java.util.Locale;

public final class Format {

    private Format() {
    }

    /** Formats an amount given in thousands of rupees the Indian way, e.g. 39900 -> "₹3.99 Cr", 565 -> "₹5.65 L". */
    public static String inr(int thousands) {
        if (thousands >= 10000) return String.format(Locale.UK, "₹%.2f Cr", thousands / 10000.0);
        return String.format(Locale.UK, "₹%.2f L", thousands / 100.0);
    }

    public static String number(int value) {
        return NumberFormat.getIntegerInstance(Locale.UK).format(value);
    }
}
