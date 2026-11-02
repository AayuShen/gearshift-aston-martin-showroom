# Gearshift

A native Android app (Java) for browsing and enquiring about Aston Martins.

## Run it

1. In Android Studio choose **File > Open** and select this folder (`E:\Projects\GearShift - APP`).
2. Let Gradle sync, pick the `Pixel_10` emulator (or a phone) and press **Run**.

From a terminal: `gradlew assembleDebug` builds `app/build/outputs/apk/debug/app-debug.apk`.

## Screens

- **Showroom (cinematic homepage)**: 01 full-screen automatic photo slideshow (swipe to change) with "BEYOND THE ORDINARY."; 02 DB12 reveal with giant type layered behind the car; 03 performance HUD with animated gauges; 04 editorial collection with scroll-triggered parallax; 05 exclusive releases, new arrivals and sign-off.
- **Explore**: search, category rail, refinement chips, sorting, listing cards.
- **Models**: Model Discovery for Vantage, DB12, Vanquish, DBX and Valhalla.
- **Favourites**: curated shortlist with telemetry comparison, undo on remove, share.
- **Car Details**: front / left / right / top viewer (tap or swipe), photos, price, colours, specs, torque curve, car history, test drive / message / call buttons.

## Structure

- `app/src/main/java/com/gearshift/app/data`: `Vehicle`, `ModelLine`, `Inventory` (catalogue), `Favourites` (saved in SharedPreferences).
- `app/src/main/java/com/gearshift/app/ui`: fragments, `UiKit` helpers, `TorqueCurveView`.
- `app/src/main/res`: layouts, Manrope font, colours and text styles from the design system.

Studio cutouts of each model in its launch colour (Vantage Ultra Yellow, DB12 Iridescent Emerald, Vanquish Xenon Grey, DBX707 Neutron White, Valhalla Apex Grey; front, left, right, top, backgrounds removed) are bundled in `res/drawable-nodpi` as `car_{model}_{angle}.webp`. Some extra photos load over the internet. Contact buttons show a confirmation sheet; there is no backend yet.

## Light and dark themes

The app follows the phone's system theme (`Theme.Material3.DayNight`). All colours are tokens in `res/values/colors.xml` (light: sage `#A5A79B`, cream `#E8D2B5`, burgundy `#590907`) and `res/values-night/colors.xml` (dark: midnight `#030407`, cobalt `#1558C9`, acid lime `#D4FF24`). Use the semantic names (`bg`, `card`, `text_primary`, `accent`, `highlight`, `media_accent` ...); the older names (`racing_green`, `gold`, `ink` ...) are kept as aliases.

The logo (`drawable/ic_logo.xml`, and `ic_logo_media.xml` for use over photos) is a G-shaped gear ring with a gear-lever crossbar and shift knob. The geometry is identical in both themes; only the colours change.

