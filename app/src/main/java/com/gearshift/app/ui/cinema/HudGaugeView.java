package com.gearshift.app.ui.cinema;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.res.ResourcesCompat;

import androidx.core.content.ContextCompat;

import com.gearshift.app.R;

import java.util.Locale;

/** Animated HUD counter: a glowing arc fills while the number counts up to its target. */
public class HudGaugeView extends View {

    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arc = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ticks = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint value = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint unit = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private float target = 100, fill = 0.8f, progress;
    private int decimals;
    private String unitText = "", labelText = "";
    private boolean played;
    private final int accent, highlight, highlightSoft, accentDeep, labelColor;

    public HudGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float d = getResources().getDisplayMetrics().density;
        accent = ContextCompat.getColor(context, R.color.accent);
        accentDeep = ContextCompat.getColor(context, R.color.accent_deep);
        highlight = ContextCompat.getColor(context, R.color.highlight);
        highlightSoft = ContextCompat.getColor(context, R.color.highlight_soft);
        labelColor = ContextCompat.getColor(context, R.color.text_muted);
        track.setStyle(Paint.Style.STROKE);
        track.setStrokeWidth(2 * d);
        track.setColor(ContextCompat.getColor(context, R.color.hud_track));
        arc.setStyle(Paint.Style.STROKE);
        arc.setStrokeWidth(4 * d);
        arc.setStrokeCap(Paint.Cap.ROUND);
        arc.setShadowLayer(8 * d, 0, 0, (accent & 0x00FFFFFF) | 0xAA000000);
        ticks.setColor((highlight & 0x00FFFFFF) | 0x55000000);
        ticks.setStrokeWidth(1 * d);
        value.setColor(ContextCompat.getColor(context, R.color.text_primary));
        value.setTextAlign(Paint.Align.CENTER);
        value.setTypeface(ResourcesCompat.getFont(context, R.font.manrope_light));
        value.setTextSize(34 * d);
        unit.setColor(highlight);
        unit.setTextAlign(Paint.Align.CENTER);
        unit.setTypeface(ResourcesCompat.getFont(context, R.font.manrope_bold));
        unit.setTextSize(9 * d);
        unit.setLetterSpacing(0.25f);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    /** @param fillFraction how much of the arc the final value fills (0..1). */
    public void setData(float targetValue, int decimals, String unitText, String label, float fillFraction) {
        this.target = targetValue;
        this.decimals = decimals;
        this.unitText = unitText;
        this.labelText = label;
        this.fill = fillFraction;
        invalidate();
    }

    public void play(long delay) {
        if (played) return;
        played = true;
        ValueAnimator a = ValueAnimator.ofFloat(0f, 1f);
        a.setStartDelay(delay);
        a.setDuration(1800);
        a.setInterpolator(new DecelerateInterpolator(2f));
        a.addUpdateListener(v -> {
            progress = (float) v.getAnimatedValue();
            invalidate();
        });
        a.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float d = getResources().getDisplayMetrics().density;
        float size = Math.min(w, h) - 16 * d;
        oval.set((w - size) / 2, (h - size) / 2, (w + size) / 2, (h + size) / 2);
        SweepGradient g = new SweepGradient(w / 2f, h / 2f,
                new int[]{accentDeep, accent, highlightSoft, highlight}, new float[]{0f, 0.45f, 0.75f, 1f});
        android.graphics.Matrix m = new android.graphics.Matrix();
        m.setRotate(130, w / 2f, h / 2f);
        g.setLocalMatrix(m);
        arc.setShader(g);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float d = getResources().getDisplayMetrics().density;
        float cx = oval.centerX(), cy = oval.centerY(), r = oval.width() / 2;
        canvas.drawArc(oval, 135, 270, false, track);
        canvas.drawArc(oval, 135, 270 * fill * progress, false, arc);
        for (int i = 0; i <= 27; i++) {
            double a = Math.toRadians(135 + 270 * i / 27f);
            float r1 = r - 9 * d, r2 = r1 - (i % 3 == 0 ? 6 : 3) * d;
            canvas.drawLine(cx + (float) Math.cos(a) * r1, cy + (float) Math.sin(a) * r1,
                    cx + (float) Math.cos(a) * r2, cy + (float) Math.sin(a) * r2, ticks);
        }
        String text = String.format(Locale.UK, "%." + decimals + "f", target * progress);
        canvas.drawText(text, cx, cy + 10 * d, value);
        canvas.drawText(unitText, cx, cy + 28 * d, unit);
        unit.setColor(labelColor);
        canvas.drawText(labelText, cx, oval.bottom - 2 * d, unit);
        unit.setColor(highlight);
    }
}
