package com.gearshift.app.data;

import android.content.Context;

/** Background-free studio cutouts of each model, bundled in res/drawable-nodpi as car_{model}_{angle}. */
public final class Angles {

    public static final String[] ALL = {"front", "left", "right", "top"};
    public static final String[] LABELS = {"Front", "Left", "Right", "Top"};

    private Angles() {
    }

    /** Which cutout set to use for a model line, or null when we have no cutouts for it. */
    public static String keyForModel(String modelLine) {
        switch (modelLine) {
            case "Vantage": return "vantage";
            case "DB12": return "db12";
            case "Vanquish": return "vanquish";
            case "DBX": return "dbx";
            case "Valhalla": return "valhalla";
            default: return null;
        }
    }

    /** Cutout set for a specific listing; null when the listing is shown with its own photos instead. */
    public static String keyFor(Vehicle v) {
        return v.photoOnly ? null : keyForModel(v.modelLine);
    }

    public static int drawable(Context c, String key, String angle) {
        if (key == null) return 0;
        return c.getResources().getIdentifier("car_" + key + "_" + angle, "drawable", c.getPackageName());
    }
}
