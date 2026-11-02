package com.gearshift.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.gearshift.app.R;

/** The racing-green torque band drawn on the Vehicle Details screen. */
public class TorqueCurveView extends View {

    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotRing = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path line = new Path();
    private final Path area = new Path();

    public TorqueCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int green = UiKit.color(context, R.color.racing_green);
        stroke.setColor(green);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(UiKit.dp(context, 2.5f));
        stroke.setStrokeCap(Paint.Cap.ROUND);
        dot.setColor(green);
        dotRing.setColor(0xFFFFFFFF);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Curve drawn on a 320 x 80 grid, scaled to the view.
        float sx = w / 320f, sy = h / 80f;
        line.reset();
        line.moveTo(0, 75 * sy);
        line.cubicTo(40 * sx, 70 * sy, 70 * sx, 35 * sy, 110 * sx, 20 * sy);
        line.cubicTo(160 * sx, 10 * sy, 220 * sx, 10 * sy, 260 * sx, 25 * sy);
        line.cubicTo(290 * sx, 38 * sy, 310 * sx, 50 * sy, 320 * sx, 55 * sy);
        area.set(line);
        area.lineTo(w, h);
        area.lineTo(0, h);
        area.close();
        fill.setShader(new LinearGradient(0, 0, 0, h, (UiKit.color(getContext(), R.color.accent) & 0x00FFFFFF) | 0x40000000, UiKit.color(getContext(), R.color.accent) & 0x00FFFFFF, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(area, fill);
        canvas.drawPath(line, stroke);
        float cx = getWidth() * 210 / 320f, cy = getHeight() * 11 / 80f;
        canvas.drawCircle(cx, cy, UiKit.dp(getContext(), 6), dotRing);
        canvas.drawCircle(cx, cy, UiKit.dp(getContext(), 4), dot);
    }
}
