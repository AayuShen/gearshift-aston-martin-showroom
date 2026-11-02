package com.gearshift.app.data;

/** An Aston Martin model family shown on the Model Discovery screen. */
public class ModelLine {

    public final String name;
    public final String tagline;
    public final Vehicle.Category category;
    public final String description;
    public final String imageUrl;
    public final String fromPrice;
    public final String power;
    public final String zeroTo100;
    public final String topSpeed;
    public final String engine;
    public final String badge;

    public ModelLine(String name, String tagline, Vehicle.Category category, String description, String imageUrl,
                     String fromPrice, String power, String zeroTo100, String topSpeed, String engine, String badge) {
        this.name = name;
        this.tagline = tagline;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.fromPrice = fromPrice;
        this.power = power;
        this.zeroTo100 = zeroTo100;
        this.topSpeed = topSpeed;
        this.engine = engine;
        this.badge = badge;
    }
}
