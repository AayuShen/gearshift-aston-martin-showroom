package com.gearshift.app.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.gearshift.app.R;
import com.gearshift.app.data.Favourites;
import com.gearshift.app.data.Vehicle;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/** Small shared helpers for building the editorial UI in code. */
public final class UiKit {

    private UiKit() {
    }

    public static int dp(Context c, float value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                c.getResources().getDisplayMetrics()));
    }

    public static int color(Context c, int res) {
        return ContextCompat.getColor(c, res);
    }

    public static Typeface font(Context c, int res) {
        return ResourcesCompat.getFont(c, res);
    }

    public static void loadImage(ImageView view, String url) {
        Glide.with(view)
                .load(url)
                .placeholder(R.color.surface_container)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(view);
    }

    /**
     * Shows the car as a background-free cutout on a soft studio backdrop when we have one for its model,
     * otherwise falls back to the original photo. Returns true when a cutout was used.
     */
    public static boolean showCar(ImageView view, String modelLine, String photoUrl, String angle,
                                  int padTopDp, int padBottomDp) {
        Context c = view.getContext();
        int res = com.gearshift.app.data.Angles.drawable(c, com.gearshift.app.data.Angles.keyForModel(modelLine), angle);
        if (res == 0) {
            view.setBackgroundColor(color(c, R.color.surface_container));
            view.setPadding(0, 0, 0, 0);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            loadImage(view, photoUrl);
            return false;
        }
        Glide.with(view).clear(view);
        view.setBackgroundResource(R.drawable.studio_backdrop);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int side = dp(c, 18);
        view.setPadding(side, dp(c, padTopDp), side, dp(c, padBottomDp));
        view.setImageResource(res);
        return true;
    }

    /** Shrinks every compound drawable inside the tree to an 18dp glyph so it sits nicely in pills. */
    public static void tidyIcons(View root) {
        if (root instanceof TextView) {
            TextView tv = (TextView) root;
            Drawable[] ds = tv.getCompoundDrawablesRelative();
            boolean any = false;
            int size = dp(tv.getContext(), 18);
            for (Drawable d : ds) {
                if (d != null) {
                    d.setBounds(0, 0, size, size);
                    any = true;
                }
            }
            if (any) tv.setCompoundDrawablesRelative(ds[0], ds[1], ds[2], ds[3]);
        }
        if (root instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) root;
            for (int i = 0; i < g.getChildCount(); i++) tidyIcons(g.getChildAt(i));
        }
    }

    /** Fills a horizontal container with label/value columns separated by hairlines. */
    public static void fillMetrics(LinearLayout container, String[][] metrics, boolean centered) {
        Context c = container.getContext();
        container.removeAllViews();
        for (int i = 0; i < metrics.length; i++) {
            if (i > 0) {
                View divider = new View(c);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(dp(c, 1), dp(c, 26));
                dlp.gravity = Gravity.CENTER_VERTICAL;
                divider.setLayoutParams(dlp);
                divider.setBackgroundColor(color(c, R.color.surface_highest));
                container.addView(divider);
            }
            LinearLayout col = new LinearLayout(c);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(centered ? Gravity.CENTER_HORIZONTAL : Gravity.START);
            col.setPadding(dp(c, 6), 0, dp(c, 6), 0);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView label = new TextView(c, null, 0, R.style.Text_Kicker_Small);
            label.setText(metrics[i][0]);
            label.setMaxLines(1);
            col.addView(label);

            TextView value = new TextView(c, null, 0, R.style.Text_Metric);
            value.setText(metrics[i][1]);
            value.setMaxLines(1);
            value.setPadding(0, dp(c, 4), 0, 0);
            col.addView(value);

            if (metrics[i].length > 2) {
                TextView sub = new TextView(c, null, 0, R.style.Text_BodySm);
                sub.setText(metrics[i][2]);
                sub.setTextSize(10);
                sub.setMaxLines(1);
                sub.setPadding(0, dp(c, 2), 0, 0);
                col.addView(sub);
            }
            container.addView(col);
        }
    }

    public static String[][] coreMetrics(Vehicle v) {
        return new String[][]{
                {"Power", v.powerHp + " HP"},
                {"0–100", v.zeroTo100 + " s"},
                {"Top Speed", v.topSpeedKmh + " km/h"}
        };
    }

    /** A small rounded chip, used for option tags and spec overlays. */
    public static TextView chip(Context c, String text, int bgColor, int textColor, float textSp) {
        TextView chip = new TextView(c, null, 0, R.style.Text_LabelCaps);
        chip.setText(text);
        chip.setTextSize(textSp);
        chip.setTextColor(textColor);
        chip.setMaxLines(1);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(c, 999));
        bg.setColor(bgColor);
        chip.setBackground(bg);
        chip.setPadding(dp(c, 9), dp(c, 4), dp(c, 9), dp(c, 4));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(c, 6));
        chip.setLayoutParams(lp);
        return chip;
    }

    /** Selectable pill used for category rails and segmented navigators. */
    public static TextView segmentPill(Context c, String text, boolean active) {
        TextView pill = new TextView(c, null, 0, R.style.Text_LabelCaps);
        pill.setText(text);
        pill.setGravity(Gravity.CENTER);
        pill.setMaxLines(1);
        pill.setPadding(dp(c, 16), 0, dp(c, 16), 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(c, 36));
        lp.setMarginEnd(dp(c, 8));
        pill.setLayoutParams(lp);
        setSegmentActive(pill, active);
        return pill;
    }

    public static void setSegmentActive(TextView pill, boolean active) {
        Context c = pill.getContext();
        pill.setBackgroundResource(active ? R.drawable.bg_pill_green : R.drawable.bg_pill_low);
        pill.setTextColor(color(c, active ? R.color.white : R.color.secondary));
        pill.setElevation(active ? dp(c, 1) : 0);
    }

    /** Toggleable refinement chip (carbon when active). */
    public static void setRefineActive(TextView chip, boolean active) {
        Context c = chip.getContext();
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(c, 999));
        bg.setColor(color(c, active ? R.color.carbon : R.color.white));
        if (!active) bg.setStroke(dp(c, 1), color(c, R.color.hairline));
        chip.setBackground(bg);
        chip.setTextColor(color(c, active ? R.color.white : R.color.on_surface_variant));
    }

    /** Binds a heart toggle to the favourites store. */
    public static void bindSave(ImageView button, Vehicle v) {
        Favourites favs = Favourites.get(button.getContext());
        renderSave(button, favs.contains(v.id));
        button.setOnClickListener(view -> {
            boolean saved = favs.toggle(v.id);
            renderSave(button, saved);
            button.animate().scaleX(0.85f).scaleY(0.85f).setDuration(90)
                    .withEndAction(() -> button.animate().scaleX(1f).scaleY(1f).setDuration(120).start())
                    .start();
        });
    }

    public static void renderSave(ImageView button, boolean saved) {
        Context c = button.getContext();
        button.setImageResource(saved ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        button.setImageTintList(ColorStateList.valueOf(color(c, saved ? R.color.racing_green : R.color.on_surface)));
        button.setContentDescription(saved ? "Remove from favourites" : "Save to favourites");
    }

    /** Confirmation sheet for contact actions. Gearshift is buyer-only, so every action is a request to the seller. */
    public static void showConcierge(Context c, String title, String message) {
        BottomSheetDialog dialog = new BottomSheetDialog(c);
        LinearLayout sheet = new LinearLayout(c);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setPadding(dp(c, 24), dp(c, 24), dp(c, 24), dp(c, 32));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color(c, R.color.white));
        float r = dp(c, 24);
        bg.setCornerRadii(new float[]{r, r, r, r, 0, 0, 0, 0});
        sheet.setBackground(bg);

        LinearLayout row = new LinearLayout(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        View dot = new View(c);
        dot.setBackgroundResource(R.drawable.bg_dot_green);
        row.addView(dot, new LinearLayout.LayoutParams(dp(c, 10), dp(c, 10)));
        TextView kicker = new TextView(c, null, 0, R.style.Text_Kicker_Green);
        kicker.setText("Gearshift Support");
        kicker.setPadding(dp(c, 8), 0, 0, 0);
        row.addView(kicker);
        sheet.addView(row);

        TextView heading = new TextView(c, null, 0, R.style.Text_HeadlineSm);
        heading.setText(title);
        heading.setPadding(0, dp(c, 12), 0, 0);
        sheet.addView(heading);

        TextView body = new TextView(c, null, 0, R.style.Text_Body);
        body.setText(message);
        body.setTextColor(color(c, R.color.on_surface_variant));
        body.setPadding(0, dp(c, 8), 0, 0);
        sheet.addView(body);

        TextView done = new TextView(c, null, 0, R.style.Pill);
        done.setText("OK");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(c, 48));
        lp.topMargin = dp(c, 20);
        sheet.addView(done, lp);
        done.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(sheet);
        View parent = (View) sheet.getParent();
        if (parent != null) parent.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        dialog.show();
    }
}
