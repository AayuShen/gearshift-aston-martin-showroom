package com.gearshift.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.gearshift.app.MainActivity;
import com.gearshift.app.R;
import com.gearshift.app.data.Inventory;
import com.gearshift.app.data.Vehicle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Explore Collection: search, category rail, refinements, sorting and the listing grid. */
public class ExploreFragment extends Fragment {

    private static Vehicle.Category pendingCategory;
    private static String pendingModel;
    private static boolean pendingFocus;

    private static final String[] SORTS = {"Recommended", "Price: High to Low", "Price: Low to High", "Most Powerful", "Newest"};
    private static final String[] REFINES = {"Verified", "Just Added", "V8 Engine", "V12 Engine"};

    private Vehicle.Category category;
    private String modelLine;
    private String query = "";
    private int sort = 0;
    private final boolean[] refine = new boolean[REFINES.length];

    private LinearLayout listings;
    private LinearLayout categoryPills;
    private View emptyState;
    private TextView resultCount;
    private TextView sortLabel;
    private LayoutInflater inflater;

    public static void setPendingFilter(Vehicle.Category category, String modelLine, boolean focusSearch) {
        pendingCategory = category;
        pendingModel = modelLine;
        pendingFocus = focusSearch;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View root = inflater.inflate(R.layout.fragment_explore, container, false);
        category = pendingCategory;
        modelLine = pendingModel;
        boolean focus = pendingFocus;
        setPendingFilter(null, null, false);

        listings = root.findViewById(R.id.listings);
        categoryPills = root.findViewById(R.id.category_pills);
        emptyState = root.findViewById(R.id.empty_state);
        resultCount = root.findViewById(R.id.result_count);
        sortLabel = root.findViewById(R.id.sort_label);

        EditText search = root.findViewById(R.id.search_input);
        View clear = root.findViewById(R.id.search_clear);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                query = s.toString().trim();
                clear.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                render();
            }
        });
        clear.setOnClickListener(v -> search.setText(""));
        if (modelLine != null) search.setText(modelLine);
        if (focus) {
            search.requestFocus();
            search.post(() -> {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
            });
        }

        root.findViewById(R.id.btn_sort).setOnClickListener(v -> chooseSort());
        sortLabel.setOnClickListener(v -> chooseSort());
        root.findViewById(R.id.btn_inquire).setOnClickListener(v -> UiKit.showConcierge(requireContext(),
                "Tell us what you need",
                "Let us know the model, colour and budget you have in mind and we'll message you when a matching car is listed."));

        buildCategoryPills();
        buildRefineChips(root.findViewById(R.id.refine_chips));
        render();
        UiKit.tidyIcons(root);
        return root;
    }

    private void buildCategoryPills() {
        Context c = requireContext();
        categoryPills.removeAllViews();
        TextView all = UiKit.segmentPill(c, "All Models (" + Inventory.all().size() + ")", category == null);
        all.setOnClickListener(v -> selectCategory(null));
        categoryPills.addView(all);
        for (Vehicle.Category cat : Vehicle.Category.values()) {
            TextView pill = UiKit.segmentPill(c, cat.label + " (" + Inventory.countIn(cat) + ")", cat == category);
            pill.setOnClickListener(v -> selectCategory(cat));
            categoryPills.addView(pill);
        }
    }

    private void selectCategory(Vehicle.Category cat) {
        category = cat;
        for (int i = 0; i < categoryPills.getChildCount(); i++) {
            Vehicle.Category pillCat = i == 0 ? null : Vehicle.Category.values()[i - 1];
            UiKit.setSegmentActive((TextView) categoryPills.getChildAt(i), pillCat == cat);
        }
        render();
    }

    private void buildRefineChips(LinearLayout row) {
        Context c = requireContext();
        for (int i = 0; i < REFINES.length; i++) {
            final int index = i;
            TextView chip = UiKit.chip(c, REFINES[i], 0, 0, 11);
            chip.setPadding(UiKit.dp(c, 12), UiKit.dp(c, 6), UiKit.dp(c, 12), UiKit.dp(c, 6));
            UiKit.setRefineActive(chip, false);
            chip.setOnClickListener(v -> {
                refine[index] = !refine[index];
                UiKit.setRefineActive(chip, refine[index]);
                render();
            });
            row.addView(chip);
        }
    }

    private void chooseSort() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sort cars")
                .setSingleChoiceItems(SORTS, sort, (d, which) -> {
                    sort = which;
                    d.dismiss();
                    render();
                })
                .show();
    }

    private boolean matches(Vehicle v) {
        if (category != null && v.category != category) return false;
        if (refine[0] && !v.heritageCertified) return false;
        if (refine[1] && !v.newDelivery) return false;
        if (refine[2] && !v.isV8()) return false;
        if (refine[3] && !v.isV12()) return false;
        if (!query.isEmpty()) {
            String haystack = (v.name + " " + v.modelLine + " " + v.edition + " " + v.colour + " " + v.series + " "
                    + v.engine + " " + v.category.label).toLowerCase(Locale.UK);
            for (String term : query.toLowerCase(Locale.UK).split("\\s+")) {
                if (!haystack.contains(term)) return false;
            }
        }
        return true;
    }

    private void render() {
        if (listings == null) return;
        List<Vehicle> results = new ArrayList<>();
        for (Vehicle v : Inventory.all()) if (matches(v)) results.add(v);
        switch (sort) {
            case 1: results.sort(Comparator.comparingInt((Vehicle v) -> v.isByInquiry() ? Integer.MAX_VALUE : v.priceInrK).reversed()); break;
            case 2: results.sort(Comparator.comparingInt((Vehicle v) -> v.isByInquiry() ? Integer.MAX_VALUE : v.priceInrK)); break;
            case 3: results.sort(Comparator.comparingInt((Vehicle v) -> v.powerHp).reversed()); break;
            case 4: results.sort(Comparator.comparingInt((Vehicle v) -> v.year).reversed()); break;
            default: break;
        }

        resultCount.setText(results.size() + (results.size() == 1 ? " Car Available" : " Cars Available"));
        sortLabel.setText("Sort: " + SORTS[sort] + "  ▾");
        emptyState.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);

        listings.removeAllViews();
        MainActivity main = (MainActivity) requireActivity();
        for (Vehicle v : results) {
            View card = inflater.inflate(R.layout.item_listing, listings, false);
            boolean cutout = UiKit.showCar(card.findViewById(R.id.image), v.photoOnly ? "" : v.modelLine, v.imageUrl, "left", 50, 74);
            card.findViewById(R.id.scrim).setVisibility(cutout ? View.GONE : View.VISIBLE);
            TextView provenance = card.findViewById(R.id.provenance);
            provenance.setText((v.heritageCertified ? "Verified" : v.status) + " · " + v.year);
            if (v.category == Vehicle.Category.SPECIAL || v.priceInrK >= 80000) {
                provenance.setBackgroundResource(R.drawable.bg_pill_tertiary);
                provenance.setTextColor(UiKit.color(main, R.color.white));
            }
            UiKit.bindSave(card.findViewById(R.id.save), v);
            UiKit.fillMetrics(card.findViewById(R.id.glass_specs), new String[][]{
                    {"Output", v.powerHp + " hp"},
                    {"0–100 km/h", v.zeroTo100 + "s"},
                    {"Gearbox", v.gearbox.replace("ZF ", "")},
                    {"Odo", v.odometer}
            }, false);
            ((TextView) card.findViewById(R.id.series)).setText(v.series);
            ((TextView) card.findViewById(R.id.title)).setText("Aston Martin " + v.name);
            ((TextView) card.findViewById(R.id.finish)).setText(v.finishNote);
            TextView price = card.findViewById(R.id.price);
            price.setText(v.priceLabel());
            if (v.isByInquiry()) price.setTextColor(UiKit.color(main, R.color.racing_green));
            TextView cta = card.findViewById(R.id.cta);
            if (results.indexOf(v) % 2 == 1) cta.setBackgroundResource(R.drawable.bg_pill_carbon);
            cta.setOnClickListener(x -> main.openVehicle(v));
            card.setOnClickListener(x -> main.openVehicle(v));
            UiKit.tidyIcons(card);
            listings.addView(card);
        }
    }
}
