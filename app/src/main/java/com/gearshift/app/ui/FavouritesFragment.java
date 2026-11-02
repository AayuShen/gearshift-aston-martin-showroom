package com.gearshift.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gearshift.app.MainActivity;
import com.gearshift.app.R;
import com.gearshift.app.data.Favourites;
import com.gearshift.app.data.Vehicle;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/** Favourites ("Curated Shortlist"): saved vehicles with a side-by-side telemetry comparison. */
public class FavouritesFragment extends Fragment implements Favourites.Listener {

    private View root;
    private LayoutInflater inflater;
    private boolean compareOpen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        root = inflater.inflate(R.layout.fragment_favourites, container, false);
        MainActivity main = (MainActivity) requireActivity();

        root.findViewById(R.id.btn_share).setOnClickListener(v -> share());
        root.findViewById(R.id.btn_browse).setOnClickListener(v -> main.openTab(R.id.nav_showroom));
        root.findViewById(R.id.btn_specialist).setOnClickListener(v -> UiKit.showConcierge(requireContext(),
                "Thanks for getting in touch",
                "Our team will reply within 24 hours to help with your saved cars."));
        root.findViewById(R.id.btn_compare).setOnClickListener(v -> {
            compareOpen = !compareOpen;
            render();
        });

        Favourites.get(requireContext()).addListener(this);
        render();
        UiKit.tidyIcons(root);
        return root;
    }

    @Override
    public void onDestroyView() {
        Favourites.get(requireContext()).removeListener(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    @Override
    public void onFavouritesChanged() {
        if (root != null) render();
    }

    private void render() {
        Context c = requireContext();
        MainActivity main = (MainActivity) requireActivity();
        List<Vehicle> saved = Favourites.get(c).vehicles();

        TextView subtitle = root.findViewById(R.id.subtitle);
        subtitle.setText(saved.size() + (saved.size() == 1 ? " car saved" : " cars saved")
                + ". Compare them or book a test drive.");

        // Comparison panel
        View panel = root.findViewById(R.id.compare_panel);
        panel.setAlpha(saved.size() < 2 ? 0.5f : 1f);
        TextView compareBtn = root.findViewById(R.id.btn_compare);
        compareBtn.setText("Compare (" + saved.size() + ")");
        compareBtn.setEnabled(saved.size() >= 2);
        View drawer = root.findViewById(R.id.compare_drawer);
        boolean showDrawer = compareOpen && saved.size() >= 2;
        drawer.setVisibility(showDrawer ? View.VISIBLE : View.GONE);
        if (showDrawer) buildComparison(saved);

        // Cards
        LinearLayout list = root.findViewById(R.id.shortlist);
        list.removeAllViews();
        root.findViewById(R.id.empty_state).setVisibility(saved.isEmpty() ? View.VISIBLE : View.GONE);
        for (int i = 0; i < saved.size(); i++) {
            Vehicle v = saved.get(i);
            View card = inflater.inflate(R.layout.item_favourite, list, false);
            UiKit.showCar(card.findViewById(R.id.image), v.photoOnly ? "" : v.modelLine, v.imageUrl, "left", 50, 46);
            TextView status = card.findViewById(R.id.status);
            status.setText(v.status);
            if (v.status.equals("Reserved")) {
                status.setBackgroundResource(R.drawable.bg_pill_white);
                status.setTextColor(UiKit.color(c, R.color.carbon));
            }
            ((TextView) card.findViewById(R.id.specimen_no)).setText(String.format("#%d", i + 1));
            ((TextView) card.findViewById(R.id.location)).setText(v.location);
            ((TextView) card.findViewById(R.id.series)).setText(v.series);
            ((TextView) card.findViewById(R.id.title)).setText("Aston Martin " + v.name);
            ((TextView) card.findViewById(R.id.price)).setText(v.priceLabel());
            UiKit.fillMetrics(card.findViewById(R.id.metrics), new String[][]{
                    {"Output", v.powerHp + " HP", v.engine.replace("4.0L ", "").replace("5.2L ", "")},
                    {"0–100 km/h", v.zeroTo100 + " s", "Acceleration"},
                    {"Top Speed", v.topSpeedKmh + " km/h", "Max"}
            }, true);

            LinearLayout chips = card.findViewById(R.id.option_chips);
            for (String opt : v.options) {
                chips.addView(UiKit.chip(c, opt, UiKit.color(c, R.color.surface_container),
                        UiKit.color(c, R.color.on_surface_variant), 9));
            }

            TextView primary = card.findViewById(R.id.btn_primary);
            if (i % 2 == 1) {
                primary.setText("View Details");
                primary.setBackgroundResource(R.drawable.bg_pill_carbon);
                primary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_visibility, 0, 0, 0);
                primary.setOnClickListener(x -> main.openVehicle(v));
            } else {
                primary.setOnClickListener(x -> UiKit.showConcierge(c, "Test drive requested",
                        "Thanks! We'll contact you to fix a test drive for the " + v.name + " at " + v.location + "."));
            }
            card.findViewById(R.id.media).setOnClickListener(x -> main.openVehicle(v));
            View.OnClickListener remove = x -> removeWithUndo(v);
            card.findViewById(R.id.unsave).setOnClickListener(remove);
            card.findViewById(R.id.btn_delete).setOnClickListener(remove);
            UiKit.tidyIcons(card);
            list.addView(card);
        }
    }

    private void buildComparison(List<Vehicle> saved) {
        Context c = requireContext();
        LinearLayout cols = root.findViewById(R.id.compare_columns);
        cols.removeAllViews();
        Vehicle a = saved.get(0), b = saved.get(1);
        for (Vehicle v : new Vehicle[]{a, b}) {
            LinearLayout col = new LinearLayout(c);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.CENTER_HORIZONTAL);
            col.setBackgroundResource(R.drawable.bg_panel_container);
            col.setPadding(UiKit.dp(c, 10), UiKit.dp(c, 10), UiKit.dp(c, 10), UiKit.dp(c, 10));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (v == a) lp.setMarginEnd(UiKit.dp(c, 8));
            col.setLayoutParams(lp);

            TextView name = new TextView(c, null, 0, R.style.Text_Kicker_Small);
            name.setText(v.name);
            col.addView(name);
            TextView hp = new TextView(c, null, 0, R.style.Text_HeadlineSm);
            hp.setText(v.powerHp + " HP");
            hp.setTextColor(UiKit.color(c, R.color.primary));
            hp.setPadding(0, UiKit.dp(c, 4), 0, UiKit.dp(c, 4));
            col.addView(hp);
            TextView accel = new TextView(c, null, 0, R.style.Text_BodySm);
            accel.setText("0-100: " + v.zeroTo100 + "s");
            accel.setTextSize(11);
            col.addView(accel);
            TextView weight = new TextView(c, null, 0, R.style.Text_BodySm);
            weight.setText("Weight: " + com.gearshift.app.data.Format.number(v.weightKg) + " kg");
            weight.setTextSize(11);
            col.addView(weight);
            cols.addView(col);
        }

        double dt = Double.parseDouble(a.zeroTo100) - Double.parseDouble(b.zeroTo100);
        int dhp = a.powerHp - b.powerHp;
        String faster = dt > 0 ? b.name : a.name;
        String stronger = dhp >= 0 ? a.name : b.name;
        ((TextView) root.findViewById(R.id.compare_delta)).setText(String.format(java.util.Locale.UK,
                "Quick take: %s is %.1fs quicker to 100 km/h · %s has %d HP more", faster, Math.abs(dt), stronger, Math.abs(dhp)));
    }

    private void removeWithUndo(Vehicle v) {
        Favourites favs = Favourites.get(requireContext());
        favs.remove(v.id);
        Snackbar.make(root, v.name + " removed from favourites", Snackbar.LENGTH_LONG)
                .setAction("Undo", x -> favs.toggle(v.id))
                .setActionTextColor(UiKit.color(requireContext(), R.color.primary_fixed))
                .show();
    }

    private void share() {
        StringBuilder text = new StringBuilder("My favourite Aston Martins on Gearshift:\n");
        for (Vehicle v : Favourites.get(requireContext()).vehicles()) {
            text.append("• ").append(v.name).append(" ").append(v.edition).append(" — ").append(v.priceLabel()).append('\n');
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Gearshift Favourites");
        intent.putExtra(Intent.EXTRA_TEXT, text.toString());
        startActivity(Intent.createChooser(intent, "Share favourites"));
    }
}
