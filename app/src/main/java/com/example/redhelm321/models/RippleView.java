package com.example.redhelm321.models;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class RippleView extends View {
    private Paint ripplePaint;
    private float rippleRadius = 0f;
    private float maxRippleRadius;
    private boolean isAnimating = false;
    private ValueAnimator rippleAnimator;

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ripplePaint = new Paint();
        ripplePaint.setColor(Color.parseColor("#40FF0000")); // Semi-transparent red
        ripplePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isAnimating) {
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            canvas.drawCircle(cx, cy, rippleRadius, ripplePaint);
        }
    }

    public void startRippleEffect(float buttonWidth) {
        if (isAnimating) return;

        maxRippleRadius = buttonWidth * 1.5f;
        isAnimating = true;

        rippleAnimator = ValueAnimator.ofFloat(0, maxRippleRadius);
        rippleAnimator.setDuration(1000);
        rippleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rippleAnimator.setRepeatMode(ValueAnimator.RESTART);
        rippleAnimator.addUpdateListener(animation -> {
            rippleRadius = (float) animation.getAnimatedValue();
            float alpha = 1f - (rippleRadius / maxRippleRadius);
            ripplePaint.setAlpha((int) (alpha * 255));
            invalidate();
        });

        rippleAnimator.start();
        setVisibility(View.VISIBLE);
    }

    public void stopRippleEffect() {
        if (rippleAnimator != null && rippleAnimator.isRunning()) {
            rippleAnimator.cancel();
        }
        isAnimating = false;
        rippleRadius = 0;
        setVisibility(View.GONE);
        invalidate();
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}