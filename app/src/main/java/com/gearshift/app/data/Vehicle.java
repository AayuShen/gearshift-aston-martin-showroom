package com.gearshift.app.data;

/** A single Aston Martin specimen listed for acquisition. */
public class Vehicle {

    public enum Category {
        TOURER("Super Tourers"),
        SPORTS("Sports Cars"),
        SUV("DBX SUVs"),
        SPECIAL("Special Editions");

        public final String label;

        Category(String label) {
            this.label = label;
        }
    }

    public final String id;
    public final String modelLine;
    public final String name;
    public final String edition;
    public final String series;
    public final Category category;
    public final int year;
    public final String colour;
    public final String finishNote;
    /** Price in thousands of rupees, or 0 when the price is on request. */
    public final int priceInrK;
    /** Estimated monthly EMI in thousands of rupees. */
    public final int emiInrK;
    public final String status;
    public final String location;
    public final String commission;
    public final String imageUrl;
    public final String[] gallery;

    public final int powerHp;
    public final int torqueNm;
    public final String zeroTo100;
    public final int topSpeedKmh;
    public final String engine;
    public final String gearbox;
    public final String drivetrain;
    public final String odometer;
    public final int weightKg;
    public final String[] options;

    public final boolean heritageCertified;
    /** True when this listing is a different colour from the model cutouts, so its own photos are shown. */
    public final boolean photoOnly;
    public final boolean newDelivery;

    public Vehicle(Builder b) {
        id = b.id;
        modelLine = b.modelLine;
        name = b.name;
        edition = b.edition;
        series = b.series;
        category = b.category;
        year = b.year;
        colour = b.colour;
        finishNote = b.finishNote;
        priceInrK = b.priceInrK;
        emiInrK = b.emiInrK;
        status = b.status;
        location = b.location;
        commission = b.commission;
        imageUrl = b.imageUrl;
        gallery = b.gallery != null ? b.gallery : new String[]{b.imageUrl};
        powerHp = b.powerHp;
        torqueNm = b.torqueNm;
        zeroTo100 = b.zeroTo100;
        topSpeedKmh = b.topSpeedKmh;
        engine = b.engine;
        gearbox = b.gearbox;
        drivetrain = b.drivetrain;
        odometer = b.odometer;
        weightKg = b.weightKg;
        options = b.options != null ? b.options : new String[0];
        heritageCertified = b.heritageCertified;
        photoOnly = b.photoOnly;
        newDelivery = b.newDelivery;
    }

    public boolean isByInquiry() {
        return priceInrK <= 0;
    }

    public String priceLabel() {
        return isByInquiry() ? "Price on Request" : Format.inr(priceInrK);
    }

    public boolean isV12() {
        return engine.contains("V12");
    }

    public boolean isV8() {
        return engine.contains("V8");
    }

    public static class Builder {
        String id, modelLine, name, edition = "", series, colour, finishNote = "", status = "", location = "",
                commission = "", imageUrl, zeroTo100, engine, gearbox, drivetrain, odometer;
        Category category;
        int year, priceInrK, emiInrK, powerHp, torqueNm, topSpeedKmh, weightKg;
        String[] gallery, options;
        boolean heritageCertified, newDelivery, photoOnly;

        public Builder(String id, String modelLine, String name) {
            this.id = id;
            this.modelLine = modelLine;
            this.name = name;
        }

        public Builder edition(String v) { edition = v; return this; }
        public Builder series(String v) { series = v; return this; }
        public Builder category(Category v) { category = v; return this; }
        public Builder year(int v) { year = v; return this; }
        public Builder colour(String colour, String finishNote) { this.colour = colour; this.finishNote = finishNote; return this; }
        public Builder price(int inrThousands, int emiThousands) { priceInrK = inrThousands; emiInrK = emiThousands; return this; }
        public Builder status(String v) { status = v; return this; }
        public Builder location(String v) { location = v; return this; }
        public Builder commission(String v) { commission = v; return this; }
        public Builder image(String v) { imageUrl = v; return this; }
        public Builder gallery(String... v) { gallery = v; return this; }
        public Builder performance(int hp, int nm, String zeroTo100, int topSpeed) {
            powerHp = hp; torqueNm = nm; this.zeroTo100 = zeroTo100; topSpeedKmh = topSpeed; return this;
        }
        public Builder drivetrain(String engine, String gearbox, String drivetrain) {
            this.engine = engine; this.gearbox = gearbox; this.drivetrain = drivetrain; return this;
        }
        public Builder odometer(String v) { odometer = v; return this; }
        public Builder weight(int v) { weightKg = v; return this; }
        public Builder options(String... v) { options = v; return this; }
        public Builder heritage() { heritageCertified = true; return this; }
        public Builder newDelivery() { newDelivery = true; return this; }
        public Builder photoOnly() { photoOnly = true; return this; }

        public Vehicle build() {
            return new Vehicle(this);
        }
    }
}
