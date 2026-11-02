package com.gearshift.app.ui.cinema;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.gearshift.app.R;

/** Futuristic HUD backdrop: fine grid, corner brackets and a slow scanning line. */
public class HudGridView extends View {

    private final Paint grid = new Paint();
    private final Paint bracket = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint scan = new Paint();
    private final long start = System.currentTimeMillis();
    private final int scanColor;

    public HudGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float d = getResources().getDisplayMetrics().density;
        grid.setColor(ContextCompat.getColor(context, R.color.hud_grid));
        scanColor = ContextCompat.getColor(context, R.color.accent) & 0x00FFFFFF;
        grid.setStrokeWidth(1);
        bracket.setColor((ContextCompat.getColor(context, R.color.highlight) & 0x00FFFFFF) | 0xAA000000);
        bracket.setStyle(Paint.Style.STROKE);
        bracket.setStrokeWidth(1.5f * d);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float d = getResources().getDisplayMetrics().density;
        int w = getWidth(), h = getHeight();
        float step = 28 * d;
        for (float x = 0; x < w; x += step) canvas.drawLine(x, 0, x, h, grid);
        for (float y = 0; y < h; y += step) canvas.drawLine(0, y, w, y, grid);

        float m = 16 * d, l = 22 * d;
        canvas.drawLine(m, m, m + l, m, bracket);
        canvas.drawLine(m, m, m, m + l, bracket);
        canvas.drawLine(w - m, m, w - m - l, m, bracket);
        canvas.drawLine(w - m, m, w - m, m + l, bracket);
        canvas.drawLine(m, h - m, m + l, h - m, bracket);
        canvas.drawLine(m, h - m, m, h - m - l, bracket);
        canvas.drawLine(w - m, h - m, w - m - l, h - m, bracket);
        canvas.drawLine(w - m, h - m, w - m, h - m - l, bracket);

        float t = ((System.currentTimeMillis() - start) % 4000) / 4000f;
        float y = t * h;
        scan.setShader(new LinearGradient(0, y - 60 * d, 0, y, scanColor, scanColor | 0x2E000000, Shader.TileMode.CLAMP));
        canvas.drawRect(0, y - 60 * d, w, y, scan);
        postInvalidateOnAnimation();
    }
}
