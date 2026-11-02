package com.gearshift.app.ui.cinema;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.gearshift.app.R;

/** Full-screen automatic slideshow of still Aston Martin photographs for the top of the homepage. */
public class HeroSlideshowView extends FrameLayout {

    private static final long INTERVAL_MS = 4500;
    private static final long FADE_MS = 900;

    private static final int[] PHOTOS = {
            R.drawable.slide_vantage, R.drawable.slide_db12, R.drawable.slide_vanquish,
            R.drawable.slide_dbx, R.drawable.slide_valhalla, R.drawable.slide_trio
    };
    private static final String[] CAPTIONS = {
            "VANTAGE · COAST ROAD", "DB12 · MOUNTAIN PASS", "VANQUISH · STUDIO", "DBX707 · CITY NIGHTS", "VALHALLA · STUDIO", "THE COLLECTION"
    };

    private final ImageView[] frames = new ImageView[2];
    private final LinearLayout dots;
    private final TextView caption;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int front;
    private int index = -1;
    private boolean running;
    private final int mediaAccent;

    private final Runnable advance = new Runnable() {
        @Override
        public void run() {
            show((index + 1) % PHOTOS.length);
            if (running) handler.postDelayed(this, INTERVAL_MS);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public HeroSlideshowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0xFF000000);
        mediaAccent = ContextCompat.getColor(context, R.color.media_accent);
        float d = getResources().getDisplayMetrics().density;

        for (int i = 0; i < 2; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setAlpha(0f);
            frames[i] = iv;
            addView(iv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        // Dark grade top and bottom so the logo and headline always read.
        View grade = new View(context);
        grade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xCC000000, 0x22000000, 0x11000000, 0x88000000, 0xF5050506}));
        addView(grade, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        Typeface bold = ResourcesCompat.getFont(context, R.font.manrope_bold);
        caption = new TextView(context);
        caption.setTypeface(bold);
        caption.setTextSize(10);
        caption.setLetterSpacing(0.25f);
        caption.setTextColor(mediaAccent);
        LayoutParams cp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.START);
        cp.topMargin = (int) (80 * d);
        cp.leftMargin = (int) (24 * d);
        addView(caption, cp);

        dots = new LinearLayout(context);
        dots.setGravity(Gravity.CENTER_VERTICAL);
        for (int i = 0; i < PHOTOS.length; i++) {
            View dot = new View(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (14 * d), (int) (2 * d));
            lp.setMarginEnd((int) (5 * d));
            dot.setBackgroundColor(0x55FFFFFF);
            dots.addView(dot, lp);
        }
        LayoutParams dp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.END);
        dp.topMargin = (int) (86 * d);
        dp.rightMargin = (int) (19 * d);
        addView(dots, dp);

        // Swipe left or right to move through the photos by hand.
        GestureDetector swipe = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float vx, float vy) {
                if (Math.abs(vx) < Math.abs(vy)) return false;
                restartTimer();
                show((index + (vx < 0 ? 1 : -1) + PHOTOS.length) % PHOTOS.length);
                return true;
            }
        });
        setOnTouchListener((v, e) -> swipe.onTouchEvent(e));

        show(0);
    }

    public void start() {
        if (running) return;
        running = true;
        handler.postDelayed(advance, INTERVAL_MS);
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(advance);
    }

    private void restartTimer() {
        handler.removeCallbacks(advance);
        if (running) handler.postDelayed(advance, INTERVAL_MS);
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    private void show(int i) {
        if (i == index) return;
        index = i;
        ImageView next = frames[1 - front];
        ImageView prev = frames[front];
        front = 1 - front;
        next.setImageResource(PHOTOS[i]);
        ObjectAnimator.ofFloat(next, View.ALPHA, next.getAlpha(), 1f).setDuration(FADE_MS).start();
        ObjectAnimator.ofFloat(prev, View.ALPHA, prev.getAlpha(), 0f).setDuration(FADE_MS).start();

        caption.setText(String.format("%02d / %02d   ·   %s", i + 1, PHOTOS.length, CAPTIONS[i]));
        for (int d = 0; d < dots.getChildCount(); d++) {
            dots.getChildAt(d).setBackgroundColor(d == i ? mediaAccent : 0x55FFFFFF);
        }
    }
}
