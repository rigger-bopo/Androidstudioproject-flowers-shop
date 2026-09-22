package com.example.xianhuashangdian.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class LuckyWheelView extends View {
    public interface Listener {
        void onSpinEnd(int sectorIndex);
    }

    private static final String[] LABELS = {
            "20元券", "谢谢参与", "15元券", "5元券", "20元券",
            "谢谢参与", "15元券", "5元券", "谢谢参与", "5元券"
    };
    private static final int[] COLORS = {
            0xFFF7D991, 0xFFFFF8EA, 0xFFF3B9C8, 0xFFE7F4ED, 0xFFF7D991,
            0xFFFFF8EA, 0xFFF3B9C8, 0xFFE7F4ED, 0xFFFFF8EA, 0xFFE7F4ED
    };

    private final Paint sectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint separatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF wheelBounds = new RectF();
    private float currentRotation;
    private boolean spinning;

    public LuckyWheelView(Context context) {
        this(context, null);
    }

    public LuckyWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        separatorPaint.setColor(Color.WHITE);
        separatorPaint.setStrokeWidth(dp(2));
        textPaint.setColor(0xFF5A303A);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp(12));
        textPaint.setFakeBoldText(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dp(8));
        borderPaint.setColor(0xFFD89532);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) dp(292);
        int width = resolveSize(size, widthMeasureSpec);
        int height = resolveSize(size, heightMeasureSpec);
        int actual = Math.min(width, height);
        setMeasuredDimension(actual, actual);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radius = Math.min(width, height) / 2f - dp(9);
        wheelBounds.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);

        float sweep = 360f / LABELS.length;
        for (int i = 0; i < LABELS.length; i++) {
            float start = -90f + i * sweep;
            sectorPaint.setColor(COLORS[i]);
            canvas.drawArc(wheelBounds, start, sweep, true, sectorPaint);
            canvas.drawLine(
                    centerX,
                    centerY,
                    (float) (centerX + radius * Math.cos(Math.toRadians(start))),
                    (float) (centerY + radius * Math.sin(Math.toRadians(start))),
                    separatorPaint);

            canvas.save();
            float textAngle = start + sweep / 2f;
            canvas.rotate(textAngle, centerX, centerY);
            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            float baseline = centerY - (metrics.ascent + metrics.descent) / 2f;
            canvas.drawText(LABELS[i], centerX + radius * 0.62f, baseline, textPaint);
            canvas.restore();
        }
        canvas.drawCircle(centerX, centerY, radius, borderPaint);
        canvas.drawCircle(centerX, centerY, dp(48), borderPaint);
    }

    public void spinToIndex(int sectorIndex, Listener listener) {
        if (spinning || sectorIndex < 0 || sectorIndex >= LABELS.length) {
            return;
        }
        spinning = true;
        float sweep = 360f / LABELS.length;
        float targetModulo = (360f - (sectorIndex + 0.5f) * sweep) % 360f;
        float currentModulo = (currentRotation % 360f + 360f) % 360f;
        float delta = (targetModulo - currentModulo + 360f) % 360f;
        float targetRotation = currentRotation + 6f * 360f + delta;

        ValueAnimator animator = ValueAnimator.ofFloat(currentRotation, targetRotation);
        animator.setDuration(3600L);
        animator.setInterpolator(new DecelerateInterpolator(2.4f));
        animator.addUpdateListener(animation -> {
            currentRotation = (float) animation.getAnimatedValue();
            setRotation(currentRotation);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                spinning = false;
                currentRotation = targetRotation % 360f;
                setRotation(currentRotation);
                listener.onSpinEnd(sectorIndex);
            }
        });
        animator.start();
    }

    public boolean isSpinning() {
        return spinning;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
