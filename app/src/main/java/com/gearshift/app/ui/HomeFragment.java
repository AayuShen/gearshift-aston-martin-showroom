package com.gearshift.app.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import com.gearshift.app.MainActivity;
import com.gearshift.app.R;
import com.gearshift.app.data.Angles;
import com.gearshift.app.data.Inventory;
import com.gearshift.app.data.ModelLine;
import com.gearshift.app.data.Vehicle;
import com.gearshift.app.ui.cinema.HeroSlideshowView;
import com.gearshift.app.ui.cinema.HudGaugeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Cinematic homepage. Five full-screen "scenes": an automatic photo slideshow, a layered-typography model
 * reveal, an animated performance HUD, the editorial collection and exclusive releases.
 * Every scene is driven by the scroll position (parallax, reveals and one-shot counters).
 */
public class HomeFragment extends Fragment {

    /** Something that reacts to the scroll position. */
    private interface ScrollEffect {
        void apply(int scrollY, int viewport);
    }

    private final List<ScrollEffect> effects = new ArrayList<>();
    private NestedScrollView scroll;
    private LinearLayout sections;
    private HeroSlideshowView hero;
    private View heroSection;
    private boolean heroPlaying;
    private final List<HudGaugeView> gauges = new ArrayList<>();
    private boolean gaugesPlayed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity main = (MainActivity) requireActivity();

        scroll = root.findViewById(R.id.scroll);
        sections = root.findViewById(R.id.sections);
        hero = root.findViewById(R.id.hero);
        heroSection = root.findViewById(R.id.hero_section);

        root.findViewById(R.id.btn_hero_explore).setOnClickListener(v -> main.openExplore(null, null, false));
        root.findViewById(R.id.btn_hero_models).setOnClickListener(v -> main.openTab(R.id.nav_models));
        root.findViewById(R.id.btn_home_search).setOnClickListener(v -> main.openExplore(null, null, true));
        root.findViewById(R.id.btn_home_fav).setOnClickListener(v -> main.openTab(R.id.nav_favourites));
        root.findViewById(R.id.btn_view_all).setOnClickListener(v -> main.openExplore(null, null, false));
        root.findViewById(R.id.btn_contact).setOnClickListener(v -> UiKit.showConcierge(requireContext(),
                "Thanks for getting in touch",
                "Our team will get back to you within 24 hours to help with any questions about the cars on Gearshift."));

        setupHero(root);
        setupReveal(root, main);
        setupPerformance(root);
        buildCollection(root.findViewById(R.id.collection), main);
        buildStories(root.findViewById(R.id.stories), main);
        buildArrivals(root.findViewById(R.id.arrivals), main);
        goldText(root.findViewById(R.id.footer_mark));

        setupHeader(root);

        scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, x, y, ox, oy) -> applyEffects());
        root.post(() -> {
            // The opening scene fills exactly one screen.
            ViewGroup.LayoutParams lp = heroSection.getLayoutParams();
            lp.height = scroll.getHeight();
            heroSection.setLayoutParams(lp);
            scroll.post(this::applyEffects);
        });

        UiKit.tidyIcons(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scroll != null) scroll.post(this::applyEffects);
    }

    @Override
    public void onPause() {
        hero.stop();
        heroPlaying = false;
        super.onPause();
    }

    private void applyEffects() {
        if (scroll == null) return;
        int y = scroll.getScrollY(), vh = scroll.getHeight();
        for (ScrollEffect e : effects) e.apply(y, vh);
        boolean heroVisible = y < heroSection.getHeight();
        if (heroVisible && !heroPlaying && isResumed()) {
            hero.start();
            heroPlaying = true;
        } else if (!heroVisible && heroPlaying) {
            hero.stop();
            heroPlaying = false;
        }
    }

    /** 0 when the view's top sits at the bottom of the screen, 1 once it has risen to `until` of the screen. */
    private float enter(View v, int scrollY, int viewport, float until) {
        int top = 0;
        View p = v;
        while (p != null && p != sections) {
            top += p.getTop();
            p = (View) p.getParent();
        }
        float onScreen = top - scrollY;
        return clamp((viewport - onScreen) / (viewport * (1f - until)));
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private static float ease(float t) {
        return 1f - (float) Math.pow(1f - t, 3);
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    // ---------- 01 · Cinematic introduction ----------

    private void setupHero(View root) {
        View kicker = root.findViewById(R.id.hero_kicker);
        TextView title = root.findViewById(R.id.hero_title);
        View sub = root.findViewById(R.id.hero_sub);
        View ctas = root.findViewById(R.id.hero_ctas);
        View copy = root.findViewById(R.id.hero_copy);

        // Opening titles: the headline settles in while its tracking tightens, like a film title card.
        fadeUp(kicker, 700);
        title.setTranslationY(dp(24));
        title.animate().alpha(1f).translationY(0).setStartDelay(1100).setDuration(1100)
                .setInterpolator(new DecelerateInterpolator()).start();
        ValueAnimator tracking = ValueAnimator.ofFloat(0.45f, 0.04f);
        tracking.setStartDelay(1100);
        tracking.setDuration(1600);
        tracking.setInterpolator(new DecelerateInterpolator(2f));
        tracking.addUpdateListener(a -> title.setLetterSpacing((float) a.getAnimatedValue()));
        tracking.start();
        fadeUp(sub, 1900);
        fadeUp(ctas, 2300);

        View line = root.findViewById(R.id.scroll_line);
        line.setPivotY(0);
        ObjectAnimator cue = ObjectAnimator.ofFloat(line, View.SCALE_Y, 0.2f, 1f);
        cue.setDuration(1200);
        cue.setRepeatCount(ValueAnimator.INFINITE);
        cue.setRepeatMode(ValueAnimator.REVERSE);
        cue.start();

        // Parallax: the film drifts slower than the page and the titles fade as you leave the scene.
        effects.add((y, vh) -> {
            hero.setTranslationY(y * 0.45f);
            copy.setTranslationY(-y * 0.15f);
            copy.setAlpha(1f - clamp(y / (vh * 0.5f)));
        });
    }

    private void fadeUp(View v, long delay) {
        v.setTranslationY(dp(16));
        v.animate().alpha(1f).translationY(0).setStartDelay(delay).setDuration(800)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    // ---------- 02 · Latest model reveal ----------

    private void setupReveal(View root, MainActivity main) {
        Vehicle db12 = Inventory.byId("db12-launch");
        View section = root.findViewById(R.id.reveal_section);
        TextView word = root.findViewById(R.id.reveal_word);
        View car = root.findViewById(R.id.reveal_car);
        View shadow = root.findViewById(R.id.reveal_shadow);
        ((TextView) root.findViewById(R.id.reveal_specs)).setText(
                db12.powerHp + " HP  ·  0–100 in " + db12.zeroTo100 + " s  ·  " + db12.priceLabel());
        View.OnClickListener open = v -> main.openVehicle(db12);
        root.findViewById(R.id.btn_reveal).setOnClickListener(open);
        car.setOnClickListener(open);

        // Massive gold type that fades towards the bottom, where the car sits in front of it.
        word.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> word.getPaint().setShader(
                new LinearGradient(0, 0, 0, b - t, new int[]{highlightSoft(), highlight(), (highlight() & 0x00FFFFFF) | 0x33000000},
                        new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP)));

        effects.add((y, vh) -> {
            float p = ease(enter(section, y, vh, 0.25f));
            word.setAlpha(p);
            word.setScaleX(1.35f - 0.35f * p);
            word.setScaleY(1.35f - 0.35f * p);
            word.setTranslationY((1 - p) * dp(120));
            word.setLetterSpacing(0.25f - 0.29f * p);
            car.setTranslationY((1 - p) * dp(240));
            car.setScaleX(0.7f + 0.3f * p);
            car.setScaleY(0.7f + 0.3f * p);
            car.setAlpha(clamp(p * 1.5f));
            shadow.setAlpha(p);
            shadow.setScaleX(0.6f + 0.4f * p);
        });
    }

    // ---------- 03 · Performance chapter ----------

    private void setupPerformance(View root) {
        View section = root.findViewById(R.id.perf_section);
        View car = root.findViewById(R.id.perf_car);
        GridLayout grid = root.findViewById(R.id.gauges);
        Vehicle v = Inventory.byId("vanquish-2025");
        Object[][] data = {
                {(float) v.powerHp, 0, "HP", "POWER", 0.92f},
                {(float) v.torqueNm, 0, "NM", "TORQUE", 0.86f},
                {Float.parseFloat(v.zeroTo100), 1, "SEC", "0–100 KM/H", 0.72f},
                {(float) v.topSpeedKmh, 0, "KM/H", "TOP SPEED", 0.96f},
        };
        for (int i = 0; i < data.length; i++) {
            HudGaugeView g = new HudGaugeView(requireContext(), null);
            g.setData((float) data[i][0], (int) data[i][1], (String) data[i][2], (String) data[i][3], (float) data[i][4]);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(i / 2), GridLayout.spec(i % 2, 1f));
            lp.width = 0;
            lp.height = (int) dp(164);
            grid.addView(g, lp);
            gauges.add(g);
        }
        effects.add((y, vh) -> {
            float p = enter(section, y, vh, 0.2f);
            car.setRotation(90 + (p - 0.5f) * 24);
            car.setTranslationY((0.5f - p) * dp(80));
            if (!gaugesPlayed && p > 0.45f) {
                gaugesPlayed = true;
                for (int i = 0; i < gauges.size(); i++) gauges.get(i).play(i * 180L);
            }
        });
    }

    // ---------- 04 · Explore the collection ----------

    private void buildCollection(LinearLayout list, MainActivity main) {
        Context c = requireContext();
        String[] angle = {"right", "left", "front", "right", "top"};
        int i = 0;
        for (ModelLine m : Inventory.modelLines()) {
            final int index = i++;
            final boolean fromLeft = index % 2 == 0;
            final String view = angle[index % angle.length];

            FrameLayout block = new FrameLayout(c);
            block.setClipChildren(false);
            block.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) dp(450)));

            TextView number = new TextView(c);
            number.setText(String.format(Locale.UK, "%02d", index + 1));
            number.setTypeface(UiKit.font(c, R.font.manrope_bold));
            number.setTextSize(11);
            number.setLetterSpacing(0.3f);
            number.setTextColor(UiKit.color(c, R.color.highlight));
            FrameLayout.LayoutParams np = new FrameLayout.LayoutParams(-2, -2, fromLeft ? Gravity.START : Gravity.END);
            np.setMargins((int) dp(24), (int) dp(36), (int) dp(24), 0);
            block.addView(number, np);

            // Outlined, oversized model name behind the car (layered typography).
            TextView name = new TextView(c);
            name.setText(m.name.toUpperCase(Locale.UK));
            name.setTypeface(UiKit.font(c, R.font.manrope_bold));
            name.setMaxLines(1);
            name.setGravity((fromLeft ? Gravity.START : Gravity.END) | Gravity.CENTER_VERTICAL);
            name.setTextColor(((fromLeft ? highlight() : UiKit.color(c, R.color.accent_text)) & 0x00FFFFFF) | 0x99000000);
            name.getPaint().setStyle(Paint.Style.STROKE);
            name.getPaint().setStrokeWidth(dp(1.4f));
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(name, 40, 140, 2, TypedValue.COMPLEX_UNIT_SP);
            FrameLayout.LayoutParams nameLp = new FrameLayout.LayoutParams(-1, (int) dp(140));
            nameLp.setMargins((int) dp(16), (int) dp(56), (int) dp(16), 0);
            block.addView(name, nameLp);

            ImageView car = new ImageView(c);
            car.setImageResource(Angles.drawable(c, Angles.keyForModel(m.name), view));
            car.setScaleType(ImageView.ScaleType.FIT_CENTER);
            car.setContentDescription("Aston Martin " + m.name);
            FrameLayout.LayoutParams carLp = new FrameLayout.LayoutParams(-1, (int) dp(160));
            carLp.topMargin = (int) dp(view.equals("front") ? 104 : 122);
            if (view.equals("top")) car.setRotation(-10);
            block.addView(car, carLp);

            LinearLayout info = new LinearLayout(c);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setGravity(fromLeft ? Gravity.START : Gravity.END);
            FrameLayout.LayoutParams infoLp = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM);
            infoLp.setMargins((int) dp(24), 0, (int) dp(24), (int) dp(28));
            TextView tag = new TextView(c, null, 0, R.style.Text_Kicker);
            tag.setText(m.tagline);
            tag.setTextColor(UiKit.color(c, R.color.accent_text));
            info.addView(tag);
            TextView title = new TextView(c);
            title.setText("Aston Martin " + m.name);
            title.setTypeface(UiKit.font(c, R.font.manrope_light));
            title.setTextSize(26);
            title.setTextColor(UiKit.color(c, R.color.text_primary));
            title.setPadding(0, (int) dp(6), 0, 0);
            info.addView(title);
            TextView meta = new TextView(c);
            meta.setText(m.power + "  ·  " + m.topSpeed + "  ·  " + (m.fromPrice.startsWith("₹") ? "From " + m.fromPrice : "Price on request"));
            meta.setTypeface(UiKit.font(c, R.font.manrope_medium));
            meta.setTextSize(12);
            meta.setTextColor(UiKit.color(c, R.color.text_muted));
            meta.setPadding(0, (int) dp(4), 0, 0);
            info.addView(meta);
            TextView link = new TextView(c, null, 0, R.style.Text_Kicker);
            link.setText("Discover  →");
            link.setTextColor(UiKit.color(c, R.color.highlight));
            link.setPadding(0, (int) dp(12), 0, (int) dp(4));
            info.addView(link);
            block.addView(info, infoLp);

            View divider = new View(c);
            divider.setBackgroundColor(UiKit.color(c, R.color.line));
            FrameLayout.LayoutParams dl = new FrameLayout.LayoutParams(-1, 1, Gravity.BOTTOM);
            dl.setMargins((int) dp(24), 0, (int) dp(24), 0);
            block.addView(divider, dl);

            block.setOnClickListener(v -> {
                if (Inventory.countForModel(m.name) == 1) {
                    for (Vehicle veh : Inventory.all()) if (veh.modelLine.equals(m.name)) main.openVehicle(veh);
                } else {
                    main.openExplore(null, m.name, false);
                }
            });
            list.addView(block);

            // Scroll-triggered reveal: the car drives into frame while its name slides the other way.
            float dir = fromLeft ? -1 : 1;
            effects.add((y, vh) -> {
                float p = ease(enter(block, y, vh, 0.35f));
                float w = block.getWidth();
                if (view.equals("front")) {
                    car.setScaleX(0.6f + 0.4f * p);
                    car.setScaleY(0.6f + 0.4f * p);
                } else {
                    car.setTranslationX(dir * (1 - p) * w * 0.9f);
                }
                car.setAlpha(clamp(p * 1.4f));
                name.setTranslationX(-dir * (1 - p) * dp(120));
                name.setAlpha(p);
                info.setAlpha(clamp((p - 0.4f) / 0.6f));
                info.setTranslationY((1 - p) * dp(30));
            });
        }
    }

    // ---------- 05 · Exclusive releases ----------

    private void buildStories(LinearLayout row, MainActivity main) {
        Context c = requireContext();
        Object[][] stories = {
                {"LIMITED EDITION", "Valhalla", "Only 999 will ever be built. One is waiting at Aston Martin New Delhi.", R.drawable.story_valhalla, "valhalla"},
                {"NEW RELEASE", "The V12 returns", "The Vanquish brings back the twelve-cylinder flagship with 835 PS.", R.drawable.story_v12, "vanquish-2025"},
                {"STORY", "Night run to Lonavala", "We took the DB12 up the Mumbai–Pune Expressway after dark.", R.drawable.story_headlight, "db12-launch"},
                {"CRAFT", "Inside the DB12", "Hand-stitched leather, real metal switches and a cabin built around the driver.", R.drawable.story_cabin, "db12-launch"},
                {"UP CLOSE", "Vantage in Ultra Yellow", "Forged wheels and carbon-ceramic brakes on the 665 PS Vantage.", R.drawable.story_wheel, "vantage-2024"},
                {"FINAL EDITION", "DBS 770 Ultimate", "The last and most powerful DBS has arrived in Mumbai.", 0, "dbs-770"},
        };
        for (Object[] s : stories) {
            Vehicle v = Inventory.byId((String) s[4]);
            FrameLayout card = new FrameLayout(c);
            card.setBackgroundResource(R.drawable.bg_card_dark);
            card.setClipToOutline(true);
            card.setForeground(c.getDrawable(R.drawable.bg_card_dark_stroke));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) dp(264), (int) dp(380));
            lp.setMarginEnd((int) dp(12));
            card.setLayoutParams(lp);

            ImageView img = new ImageView(c);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int res = (int) s[3];
            if (res != 0) img.setImageResource(res);
            else UiKit.loadImage(img, v.imageUrl);
            if (res != 0 && c.getResources().getResourceEntryName(res).startsWith("car_")) {
                // Background-free cutout: sit the car on the dark card instead of cropping it.
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                img.setPadding((int) dp(16), (int) dp(40), (int) dp(16), (int) dp(190));
            }
            card.addView(img, new FrameLayout.LayoutParams(-1, -1));

            View scrim = new View(c);
            scrim.setBackgroundResource(R.drawable.scrim_story);
            card.addView(scrim, new FrameLayout.LayoutParams(-1, -1));

            LinearLayout text = new LinearLayout(c);
            text.setOrientation(LinearLayout.VERTICAL);
            text.setPadding((int) dp(20), 0, (int) dp(20), (int) dp(22));
            TextView tag = new TextView(c, null, 0, R.style.Text_Kicker);
            tag.setText((String) s[0]);
            tag.setTextSize(9);
            tag.setTextColor(UiKit.color(c, R.color.media_accent));
            tag.setBackgroundResource(R.drawable.bg_tag_media);
            tag.setPadding((int) dp(10), (int) dp(4), (int) dp(10), (int) dp(4));
            text.addView(tag, new LinearLayout.LayoutParams(-2, -2));
            TextView title = new TextView(c);
            title.setText((String) s[1]);
            title.setTypeface(UiKit.font(c, R.font.manrope_medium));
            title.setTextSize(22);
            title.setTextColor(0xFFFFFFFF);
            title.setPadding(0, (int) dp(12), 0, 0);
            text.addView(title);
            TextView body = new TextView(c);
            body.setText((String) s[2]);
            body.setTypeface(UiKit.font(c, R.font.manrope_regular));
            body.setTextSize(13);
            body.setLineSpacing(dp(3), 1f);
            body.setTextColor(UiKit.color(c, R.color.media_text_soft));
            body.setPadding(0, (int) dp(6), 0, 0);
            text.addView(body);
            TextView more = new TextView(c, null, 0, R.style.Text_Kicker);
            more.setText("View car  →");
            more.setTextColor(UiKit.color(c, R.color.media_accent));
            more.setPadding(0, (int) dp(14), 0, 0);
            text.addView(more);
            card.addView(text, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));

            card.setOnClickListener(x -> main.openVehicle(v));
            row.addView(card);
        }
    }

    private void buildArrivals(LinearLayout list, MainActivity main) {
        Context c = requireContext();
        List<Vehicle> latest = new ArrayList<>();
        for (Vehicle v : Inventory.all()) if (v.newDelivery && Angles.keyFor(v) != null) latest.add(v);
        latest.sort((a, b) -> b.year - a.year);
        for (int i = 0; i < Math.min(3, latest.size()); i++) {
            Vehicle v = latest.get(i);
            LinearLayout row = new LinearLayout(c);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.bg_card_dark);
            row.setPadding((int) dp(14), (int) dp(14), (int) dp(16), (int) dp(14));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = (int) dp(10);
            row.setLayoutParams(lp);

            ImageView thumb = new ImageView(c);
            thumb.setImageResource(Angles.drawable(c, Angles.keyFor(v), "right"));
            thumb.setScaleType(ImageView.ScaleType.FIT_CENTER);
            row.addView(thumb, new LinearLayout.LayoutParams((int) dp(104), (int) dp(52)));

            LinearLayout mid = new LinearLayout(c);
            mid.setOrientation(LinearLayout.VERTICAL);
            mid.setPadding((int) dp(14), 0, (int) dp(8), 0);
            TextView name = new TextView(c);
            name.setText(v.name + " · " + v.edition);
            name.setTypeface(UiKit.font(c, R.font.manrope_semibold));
            name.setTextSize(14);
            name.setMaxLines(1);
            name.setTextColor(UiKit.color(c, R.color.text_primary));
            mid.addView(name);
            TextView where = new TextView(c);
            where.setText(v.location);
            where.setTypeface(UiKit.font(c, R.font.manrope_regular));
            where.setTextSize(12);
            where.setMaxLines(1);
            where.setTextColor(UiKit.color(c, R.color.text_muted));
            mid.addView(where);
            TextView price = new TextView(c);
            price.setText(v.priceLabel());
            price.setTypeface(UiKit.font(c, R.font.manrope_semibold));
            price.setTextSize(13);
            price.setTextColor(UiKit.color(c, R.color.highlight));
            price.setPadding(0, (int) dp(4), 0, 0);
            mid.addView(price);
            row.addView(mid, new LinearLayout.LayoutParams(0, -2, 1f));

            ImageView chevron = new ImageView(c);
            chevron.setImageResource(R.drawable.ic_arrow_forward);
            chevron.setImageTintList(ColorStateList.valueOf(UiKit.color(c, R.color.accent_text)));
            row.addView(chevron, new LinearLayout.LayoutParams((int) dp(18), (int) dp(18)));

            row.setOnClickListener(x -> main.openVehicle(v));
            list.addView(row);
        }
    }

    private int highlight() {
        return UiKit.color(requireContext(), R.color.highlight);
    }

    private int highlightSoft() {
        return UiKit.color(requireContext(), R.color.highlight_soft);
    }

    /**
     * The header floats over the photography in white, then settles onto the page colour as you scroll.
     * In light mode its icons, wordmark and logo switch to the dark-on-cream treatment once it is solid.
     */
    private void setupHeader(View root) {
        View header = root.findViewById(R.id.home_header);
        TextView mark = root.findViewById(R.id.header_mark);
        ImageView logo = root.findViewById(R.id.header_logo);
        ImageView search = root.findViewById(R.id.btn_home_search);
        ImageView fav = root.findViewById(R.id.btn_home_fav);
        Context c = requireContext();
        int page = UiKit.color(c, R.color.bg);
        int ink = UiKit.color(c, R.color.text_primary);
        ArgbEvaluator mix = new ArgbEvaluator();
        final boolean[] solid = {false};
        effects.add((y, vh) -> {
            float a = clamp(y / (heroSection.getHeight() * 0.6f));
            header.setBackgroundColor(((int) (a * 0xF2) << 24) | (page & 0x00FFFFFF));
            int fg = (int) mix.evaluate(a, 0xFFFFFFFF, ink);
            mark.setTextColor(fg);
            search.setImageTintList(ColorStateList.valueOf(fg));
            fav.setImageTintList(ColorStateList.valueOf(fg));
            boolean nowSolid = a > 0.6f;
            if (nowSolid != solid[0]) {
                solid[0] = nowSolid;
                logo.setImageResource(nowSolid ? R.drawable.ic_logo : R.drawable.ic_logo_media);
            }
        });
    }

    private void goldText(TextView tv) {
        tv.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> tv.getPaint().setShader(
                new LinearGradient(0, 0, r - l, 0, new int[]{highlightSoft(), highlight(), highlightSoft()},
                        null, Shader.TileMode.CLAMP)));
    }
}
