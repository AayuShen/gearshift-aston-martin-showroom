package com.gearshift.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** The buyer's saved shortlist, persisted in SharedPreferences. */
public final class Favourites {

    public interface Listener {
        void onFavouritesChanged();
    }

    private static final String PREFS = "gearshift";
    private static final String KEY = "favourites";
    private static Favourites instance;

    private final SharedPreferences prefs;
    private final LinkedHashSet<String> ids = new LinkedHashSet<>();
    private final List<Listener> listeners = new ArrayList<>();

    private Favourites(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY)) {
            // Seed the shortlist with two cars so the screen is not empty on first launch.
            ids.add("db12-launch");
            ids.add("vantage-2024");
            save();
        } else {
            String stored = prefs.getString(KEY, "");
            for (String id : stored.split(",")) {
                if (!id.isEmpty() && Inventory.byId(id) != null) ids.add(id);
            }
        }
    }

    public static synchronized Favourites get(Context context) {
        if (instance == null) instance = new Favourites(context);
        return instance;
    }

    public boolean contains(String id) {
        return ids.contains(id);
    }

    /** Toggles the vehicle and returns true when it is now saved. */
    public boolean toggle(String id) {
        boolean saved;
        if (ids.contains(id)) {
            ids.remove(id);
            saved = false;
        } else {
            ids.add(id);
            saved = true;
        }
        save();
        notifyListeners();
        return saved;
    }

    public void remove(String id) {
        if (ids.remove(id)) {
            save();
            notifyListeners();
        }
    }

    public int size() {
        return ids.size();
    }

    public List<Vehicle> vehicles() {
        List<Vehicle> list = new ArrayList<>();
        for (String id : ids) {
            Vehicle v = Inventory.byId(id);
            if (v != null) list.add(v);
        }
        return list;
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    private void notifyListeners() {
        for (Listener l : new ArrayList<>(listeners)) l.onFavouritesChanged();
    }

    private void save() {
        Set<String> copy = new LinkedHashSet<>(ids);
        prefs.edit().putString(KEY, String.join(",", copy)).apply();
    }
}
