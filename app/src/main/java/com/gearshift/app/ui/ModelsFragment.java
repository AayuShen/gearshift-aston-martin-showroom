package com.gearshift.app.ui;

import android.content.Context;
import android.os.Bundle;
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
import com.gearshift.app.data.Inventory;
import com.gearshift.app.data.ModelLine;
import com.gearshift.app.data.Vehicle;

/** Model Discovery: browse the Aston Martin model families and jump to their specimens. */
public class ModelsFragment extends Fragment {

    private Vehicle.Category segment;
    private LinearLayout list;
    private LinearLayout pills;
    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View root = inflater.inflate(R.layout.fragment_models, container, false);
        list = root.findViewById(R.id.model_list);
        pills = root.findViewById(R.id.segment_pills);
        buildSegments();
        render();
        return root;
    }

    private void buildSegments() {
        Context c = requireContext();
        TextView all = UiKit.segmentPill(c, "All Lines", true);
        all.setOnClickListener(v -> select(null));
        pills.addView(all);
        for (Vehicle.Category cat : Vehicle.Category.values()) {
            TextView pill = UiKit.segmentPill(c, cat.label, false);
            pill.setOnClickListener(v -> select(cat));
            pills.addView(pill);
        }
    }

    private void select(Vehicle.Category cat) {
        segment = cat;
        for (int i = 0; i < pills.getChildCount(); i++) {
            Vehicle.Category pillCat = i == 0 ? null : Vehicle.Category.values()[i - 1];
            UiKit.setSegmentActive((TextView) pills.getChildAt(i), pillCat == cat);
        }
        render();
    }

    private void render() {
        list.removeAllViews();
        MainActivity main = (MainActivity) requireActivity();
        for (ModelLine m : Inventory.modelLines()) {
            if (segment != null && m.category != segment) continue;
            View card = inflater.inflate(R.layout.item_model_line, list, false);
            boolean cutout = UiKit.showCar(card.findViewById(R.id.image), m.name, m.imageUrl, "left", 44, 44);
            card.findViewById(R.id.scrim).setVisibility(cutout ? View.GONE : View.VISIBLE);
            ((TextView) card.findViewById(R.id.badge)).setText(m.badge);
            int count = Inventory.countForModel(m.name);
            TextView availability = card.findViewById(R.id.availability);
            availability.setText(count == 0 ? "ON REQUEST" : count + (count == 1 ? " CAR" : " CARS"));
            ((TextView) card.findViewById(R.id.kicker)).setText("Aston Martin · " + m.tagline);
            ((TextView) card.findViewById(R.id.title)).setText(m.name);
            ((TextView) card.findViewById(R.id.description)).setText(m.description);
            ((TextView) card.findViewById(R.id.price)).setText(m.fromPrice);
            UiKit.fillMetrics(card.findViewById(R.id.metrics), new String[][]{
                    {"Power", m.power}, {"0–100", m.zeroTo100}, {"Top Speed", m.topSpeed}
            }, true);

            TextView cta = card.findViewById(R.id.cta);
            View.OnClickListener open;
            if (count == 0) {
                cta.setText("Ask");
                cta.setBackgroundResource(R.drawable.bg_pill_green);
                open = v -> UiKit.showConcierge(requireContext(), m.name + " availability",
                        "There are no " + m.name + " cars listed right now. We'll let you know as soon as one is added.");
            } else {
                open = v -> main.openExplore(null, m.name, false);
            }
            cta.setOnClickListener(open);
            card.setOnClickListener(open);
            UiKit.tidyIcons(card);
            list.addView(card);
        }
    }
}
