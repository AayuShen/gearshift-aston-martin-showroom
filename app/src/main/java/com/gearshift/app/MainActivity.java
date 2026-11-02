package com.gearshift.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.gearshift.app.data.Favourites;
import com.gearshift.app.data.Vehicle;
import com.gearshift.app.ui.ExploreFragment;
import com.gearshift.app.ui.FavouritesFragment;
import com.gearshift.app.ui.HomeFragment;
import com.gearshift.app.ui.ModelsFragment;
import com.gearshift.app.ui.UiKit;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements Favourites.Listener {

    private BottomNavigationView nav;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            nav.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        subtitle = findViewById(R.id.screen_subtitle);
        nav = findViewById(R.id.bottom_nav);


        findViewById(R.id.btn_search).setOnClickListener(v -> openExplore(null, null, true));
        findViewById(R.id.btn_concierge).setOnClickListener(v -> UiKit.showConcierge(this,
                "Help & Support",
                "Have a question about a car, a price or a test drive? Our team will reply within 24 hours."));

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_showroom) show(new HomeFragment(), "Showroom");
            else if (id == R.id.nav_explore) show(new ExploreFragment(), "Explore");
            else if (id == R.id.nav_models) show(new ModelsFragment(), "Models");
            else if (id == R.id.nav_favourites) show(new FavouritesFragment(), "Favourites");
            return true;
        });

        Favourites.get(this).addListener(this);
        updateBadge();

        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_showroom);
            show(new HomeFragment(), "Showroom");
        }
    }

    @Override
    protected void onDestroy() {
        Favourites.get(this).removeListener(this);
        super.onDestroy();
    }

    private void show(Fragment fragment, String title) {
        subtitle.setText(title);
        setDarkChrome(fragment instanceof HomeFragment);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content, fragment)
                .commit();
    }

    /**
     * The cinematic homepage runs edge to edge under its own floating header; the other tabs use the top bar.
     * Colours come from the light/dark tokens, so both modes follow the system theme.
     */
    private void setDarkChrome(boolean home) {
        boolean lightBars = getResources().getBoolean(R.bool.light_bars);
        findViewById(R.id.top_bar).setVisibility(home ? android.view.View.GONE : android.view.View.VISIBLE);
        findViewById(R.id.root).setBackgroundColor(UiKit.color(this, R.color.bg));
        WindowInsetsControllerCompat bars = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        bars.setAppearanceLightStatusBars(lightBars);
        bars.setAppearanceLightNavigationBars(lightBars);
        nav.setBackgroundColor(UiKit.color(this, home ? R.color.bg : R.color.card));
        android.content.res.ColorStateList tint = androidx.core.content.ContextCompat.getColorStateList(this,
                home ? R.color.nav_item_dark : R.color.nav_item);
        nav.setItemIconTintList(tint);
        nav.setItemTextColor(tint);
    }

    /** Switches to the Explore tab, optionally pre-filtered by category or model line. */
    public void openExplore(Vehicle.Category category, String modelLine, boolean focusSearch) {
        ExploreFragment.setPendingFilter(category, modelLine, focusSearch);
        if (nav.getSelectedItemId() == R.id.nav_explore) {
            show(new ExploreFragment(), "Explore");
        } else {
            nav.setSelectedItemId(R.id.nav_explore);
        }
    }

    public void openTab(int itemId) {
        nav.setSelectedItemId(itemId);
    }

    public void openVehicle(Vehicle v) {
        Intent intent = new Intent(this, VehicleDetailActivity.class);
        intent.putExtra(VehicleDetailActivity.EXTRA_ID, v.id);
        startActivity(intent);
    }

    @Override
    public void onFavouritesChanged() {
        updateBadge();
    }

    private void updateBadge() {
        int count = Favourites.get(this).size();
        BadgeDrawable badge = nav.getOrCreateBadge(R.id.nav_favourites);
        badge.setBackgroundColor(UiKit.color(this, R.color.racing_green));
        badge.setBadgeTextColor(UiKit.color(this, R.color.white));
        badge.setNumber(count);
        badge.setVisible(count > 0);
    }
}
