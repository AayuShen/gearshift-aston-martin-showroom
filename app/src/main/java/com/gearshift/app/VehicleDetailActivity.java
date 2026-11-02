package com.gearshift.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gearshift.app.data.Angles;
import com.gearshift.app.data.Format;
import com.gearshift.app.data.Inventory;
import com.gearshift.app.data.Vehicle;
import com.gearshift.app.ui.UiKit;

import java.util.ArrayList;
import java.util.List;

/** Car Details: multi-angle viewer, price, colours, specifications, history and contact buttons. */
public class VehicleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "vehicle_id";

    private static final String[][] PALETTE = {
            {"#004B3C", "Racing Green", "Aston Martin's classic British racing colour"},
            {"#B8B8B2", "Titanium Silver", "A bright metallic silver with a satin finish"},
            {"#171717", "Carbon Black", "A deep glossy black"},
            {"#0E2E20", "Minotaur Green", "A darker green with a vintage feel"},
            {"#8E171B", "Supernova Red", "A rich metallic red"}
    };

    /** One frame of the viewer: either a bundled cutout (res != 0) or a photo URL. */
    private static final class Frame {
        final String label;
        final int res;
        final String url;

        Frame(String label, int res, String url) {
            this.label = label;
            this.res = res;
            this.url = url;
        }
    }

    private Vehicle vehicle;
    private ImageView hero;
    private TextView angleHint;
    private LinearLayout thumbs;
    private LinearLayout anglePills;
    private LinearLayout swatches;
    private final List<Frame> frames = new ArrayList<>();
    private int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(((ViewGroup) root).getChildAt(0), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        vehicle = Inventory.byId(getIntent().getStringExtra(EXTRA_ID));
        if (vehicle == null) {
            finish();
            return;
        }
        Vehicle v = vehicle;

        findViewById(R.id.btn_back).setOnClickListener(x -> finish());
        UiKit.bindSave(findViewById(R.id.btn_save), v);
        findViewById(R.id.btn_chat).setOnClickListener(x -> messageSeller());

        ((TextView) findViewById(R.id.commission)).setText("Listing ID " + v.commission);
        ((TextView) findViewById(R.id.edition_tag)).setText(v.category == Vehicle.Category.SPECIAL ? "Limited Edition" : "Verified Listing");
        ((TextView) findViewById(R.id.year_badge)).setText(v.year + " Model · " + (v.isByInquiry() ? "On Request" : "In Stock"));

        hero = findViewById(R.id.hero_image);
        angleHint = findViewById(R.id.angle_hint);
        thumbs = findViewById(R.id.thumbs);
        anglePills = findViewById(R.id.angle_pills);
        buildViewer();

        ((TextView) findViewById(R.id.series)).setText(v.series);
        ((TextView) findViewById(R.id.status)).setText(v.status);
        ((TextView) findViewById(R.id.title)).setText(v.name);
        ((TextView) findViewById(R.id.edition)).setText(v.edition);
        ((TextView) findViewById(R.id.price)).setText(v.priceLabel());
        if (v.isByInquiry()) {
            findViewById(R.id.lease_block).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.lease)).setText(Format.inr(v.emiInrK) + "/mo");
        }

        swatches = findViewById(R.id.swatches);
        buildPalette();
        buildSpecs(findViewById(R.id.spec_grid));
        ((TextView) findViewById(R.id.torque_label)).setText(Format.number(v.torqueNm) + " Nm max torque");
        buildHistory(findViewById(R.id.dossier));

        findViewById(R.id.btn_enquire).setOnClickListener(x -> UiKit.showConcierge(this, "Test drive requested",
                "Thanks! The seller will contact you to arrange a test drive of the " + v.name + " at " + v.location + "."));
        findViewById(R.id.btn_concierge_chat).setOnClickListener(x -> messageSeller());
        findViewById(R.id.btn_call).setOnClickListener(x -> UiKit.showConcierge(this, "Call the seller",
                "The team at " + v.location + " is available Monday to Saturday, 9am to 6pm. They'll call you back shortly."));

        UiKit.tidyIcons(root);
    }

    private void messageSeller() {
        UiKit.showConcierge(this, "Message sent",
                "Your message about the " + vehicle.name + " has been sent to the seller. You'll get a reply within 24 hours.");
    }

    // ---------- Multi-angle viewer ----------

    private void buildViewer() {
        String key = Angles.keyFor(vehicle);
        for (int i = 0; i < Angles.ALL.length; i++) {
            int res = Angles.drawable(this, key, Angles.ALL[i]);
            if (res != 0) frames.add(new Frame(Angles.LABELS[i], res, null));
        }
        boolean hasAngles = !frames.isEmpty();
        for (String url : vehicle.gallery) frames.add(new Frame("Photo", 0, url));

        for (int i = 0; i < frames.size(); i++) {
            final int index = i;
            Frame f = frames.get(i);
            FrameLayout cell = new FrameLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UiKit.dp(this, 80), UiKit.dp(this, 56));
            lp.setMarginEnd(UiKit.dp(this, 8));
            cell.setLayoutParams(lp);
            cell.setBackgroundResource(R.drawable.bg_thumb);
            cell.setClipToOutline(true);

            ImageView img = new ImageView(this);
            img.setClipToOutline(true);
            cell.addView(img, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (f.res != 0) {
                img.setBackgroundResource(R.drawable.studio_backdrop);
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                img.setPadding(UiKit.dp(this, 6), UiKit.dp(this, 6), UiKit.dp(this, 6), UiKit.dp(this, 6));
                img.setImageResource(f.res);
            } else {
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                UiKit.loadImage(img, f.url);
            }
            cell.setOnClickListener(x -> show(index));
            thumbs.addView(cell);
        }

        if (hasAngles) {
            for (int i = 0; i < Angles.LABELS.length; i++) {
                final int index = i;
                TextView pill = UiKit.segmentPill(this, Angles.LABELS[i], i == 0);
                pill.setOnClickListener(x -> show(index));
                anglePills.addView(pill);
            }
            enableSwipe();
        } else {
            anglePills.setVisibility(View.GONE);
            angleHint.setVisibility(View.GONE);
        }
        show(0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void enableSwipe() {
        GestureDetector detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float vx, float vy) {
                if (e1 == null || Math.abs(vx) < Math.abs(vy)) return false;
                int next = current + (vx < 0 ? 1 : -1);
                show((next + frames.size()) % frames.size());
                return true;
            }
        });
        hero.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
    }

    private void show(int index) {
        current = index;
        Frame f = frames.get(index);
        hero.animate().alpha(0.3f).setDuration(110).withEndAction(() -> {
            if (f.res != 0) {
                hero.setBackgroundResource(R.drawable.studio_backdrop);
                hero.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int side = UiKit.dp(this, 20);
                hero.setPadding(side, UiKit.dp(this, 52), side, UiKit.dp(this, 36));
                hero.setImageResource(f.res);
            } else {
                hero.setBackgroundColor(UiKit.color(this, R.color.surface_container));
                hero.setPadding(0, 0, 0, 0);
                hero.setScaleType(ImageView.ScaleType.CENTER_CROP);
                UiKit.loadImage(hero, f.url);
            }
            hero.animate().alpha(1f).setDuration(180).start();
        }).start();

        angleHint.setText(f.res != 0 ? f.label + " view  ·  swipe to rotate" : "Photo");
        for (int i = 0; i < thumbs.getChildCount(); i++) {
            View t = thumbs.getChildAt(i);
            boolean active = i == index;
            t.setForeground(active ? getDrawable(R.drawable.bg_thumb_selected) : null);
            t.setAlpha(active ? 1f : 0.7f);
        }
        for (int i = 0; i < anglePills.getChildCount(); i++) {
            UiKit.setSegmentActive((TextView) anglePills.getChildAt(i), i == index);
        }
    }

    // ---------- Colours ----------

    private void buildPalette() {
        TextView name = findViewById(R.id.colour_name);
        TextView desc = findViewById(R.id.colour_desc);
        name.setText(vehicle.colour + " (this car)");
        desc.setText(vehicle.finishNote);
        for (String[] p : PALETTE) {
            FrameLayout swatch = new FrameLayout(this);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor(p[0]));
            swatch.setBackground(bg);
            swatch.setElevation(UiKit.dp(this, 2));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UiKit.dp(this, 40), UiKit.dp(this, 40));
            lp.setMarginEnd(UiKit.dp(this, 12));
            swatch.setLayoutParams(lp);

            ImageView check = new ImageView(this);
            check.setImageResource(R.drawable.ic_check);
            check.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            check.setVisibility(View.INVISIBLE);
            FrameLayout.LayoutParams clp = new FrameLayout.LayoutParams(UiKit.dp(this, 18), UiKit.dp(this, 18), Gravity.CENTER);
            swatch.addView(check, clp);

            swatch.setOnClickListener(x -> {
                for (int i = 0; i < swatches.getChildCount(); i++) {
                    ((FrameLayout) swatches.getChildAt(i)).getChildAt(0).setVisibility(View.INVISIBLE);
                    ((GradientDrawable) swatches.getChildAt(i).getBackground()).setStroke(0, 0);
                }
                check.setVisibility(View.VISIBLE);
                bg.setStroke(UiKit.dp(this, 3), (UiKit.color(this, R.color.accent) & 0x00FFFFFF) | 0x55000000);
                name.setText(p[1] + " (selected)");
                desc.setText(p[2]);
            });
            swatches.addView(swatch);
        }
    }

    // ---------- Specifications ----------

    private void buildSpecs(GridLayout grid) {
        Vehicle v = vehicle;
        String[][] specs = {
                {"bolt", "Engine", v.engine.replace(" Twin-Turbo", ""), v.engine.contains("Twin-Turbo") ? "Twin-turbocharged" : "Petrol hybrid"},
                {"speed", "Power", v.powerHp + " HP", Format.number(v.torqueNm) + " Nm torque"},
                {"timer", "0-100 km/h", v.zeroTo100 + " sec", "Acceleration"},
                {"speed", "Top Speed", v.topSpeedKmh + " km/h", Math.round(v.topSpeedKmh / 1.609) + " mph"},
                {"tune", "Gearbox", v.gearbox, "Automatic"},
                {"car", "Drive", v.drivetrain, Format.number(v.weightKg) + " kg"},
        };
        for (int i = 0; i < specs.length; i++) {
            String[] s = specs[i];
            LinearLayout tile = new LinearLayout(this);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setBackgroundResource(R.drawable.bg_card);
            tile.setElevation(UiKit.dp(this, 1));
            tile.setPadding(UiKit.dp(this, 16), UiKit.dp(this, 16), UiKit.dp(this, 16), UiKit.dp(this, 16));

            LinearLayout top = new LinearLayout(this);
            top.setGravity(Gravity.CENTER_VERTICAL);
            ImageView icon = new ImageView(this);
            icon.setImageResource(getResources().getIdentifier("ic_" + s[0], "drawable", getPackageName()));
            icon.setImageTintList(ColorStateList.valueOf(UiKit.color(this, R.color.primary)));
            top.addView(icon, new LinearLayout.LayoutParams(UiKit.dp(this, 24), UiKit.dp(this, 24)));
            View spacer = new View(this);
            top.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1f));
            TextView key = new TextView(this, null, 0, R.style.Text_Kicker);
            key.setText(s[1]);
            key.setTextSize(9);
            top.addView(key);
            tile.addView(top);

            View gap = new View(this);
            tile.addView(gap, new LinearLayout.LayoutParams(1, 0, 1f));

            TextView value = new TextView(this, null, 0, R.style.Text_HeadlineSm);
            value.setText(s[2]);
            value.setTextSize(18);
            value.setMaxLines(1);
            if (i == 2) value.setTextColor(UiKit.color(this, R.color.primary));
            tile.addView(value);
            TextView sub = new TextView(this, null, 0, R.style.Text_BodySm);
            sub.setText(s[3]);
            sub.setTextSize(12);
            sub.setMaxLines(1);
            sub.setPadding(0, UiKit.dp(this, 4), 0, 0);
            tile.addView(sub);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(i / 2), GridLayout.spec(i % 2, 1f));
            lp.width = 0;
            lp.height = UiKit.dp(this, 132);
            lp.setMargins(UiKit.dp(this, 6), UiKit.dp(this, 6), UiKit.dp(this, 6), UiKit.dp(this, 6));
            grid.addView(tile, lp);
        }
    }

    // ---------- Car history ----------

    private void buildHistory(LinearLayout list) {
        String[][] rows = {
                {"Year", String.valueOf(vehicle.year)},
                {"Kilometres", vehicle.odometer},
                {"Warranty", "3 years"},
                {"Location", vehicle.location},
                {"Delivery", "Home delivery available"},
        };
        Context c = this;
        for (int i = 0; i < rows.length; i++) {
            LinearLayout row = new LinearLayout(c);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, UiKit.dp(c, 10), 0, UiKit.dp(c, 10));
            TextView k = new TextView(c, null, 0, R.style.Text_Body);
            k.setText(rows[i][0]);
            k.setTextColor(UiKit.color(c, R.color.secondary));
            k.setTextSize(14);
            row.addView(k, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            TextView val = new TextView(c, null, 0, R.style.Text_Body);
            val.setText(rows[i][1]);
            val.setTextSize(14);
            val.setGravity(Gravity.END);
            val.setTypeface(UiKit.font(c, R.font.manrope_medium));
            if (i == 2) val.setTextColor(UiKit.color(c, R.color.primary));
            row.addView(val, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
            list.addView(row);
            if (i < rows.length - 1) {
                View hair = new View(c);
                hair.setBackgroundColor(UiKit.color(c, R.color.hairline));
                list.addView(hair, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }
        }
    }
}
