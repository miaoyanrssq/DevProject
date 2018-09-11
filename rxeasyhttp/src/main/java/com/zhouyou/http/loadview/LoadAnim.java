package com.zhouyou.http.loadview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.zhouyou.http.R;


/**
 * 自定义过场闪烁动画
 *
 * @author a_liYa
 * @date 2017/9/5 11:30.
 */
public class LoadAnim extends ImageView implements Animatable {

    // 只在图片有颜色的像素加半透明黑色遮罩
    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private Paint mPaint;
    private Path mPath;

    private ValueAnimator mAnimator;

    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private float mFlashWidth;

    public static final float TILT_ANGLE = (float) (45f / 180f * Math.PI);
    private static final long DURATION = 1500;
    private static int[] attrIds = {android.R.attr.src};

    public LoadAnim(Context context) {
        super(context);
        init();
    }

    public LoadAnim(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadAnim(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPath = new Path();
        mPaint.setXfermode(mMode);

        mGradientMatrix = new Matrix();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        start(); // 开始动画
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (VISIBLE == visibility) {
            start();
        } else {
            stop();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mFlashWidth = (right - left) / 4;
            float x1 = (float) (mFlashWidth * Math.sin(TILT_ANGLE) * Math.sin(TILT_ANGLE));
            float y1 = (float) (mFlashWidth * Math.sin(TILT_ANGLE) * Math.cos(TILT_ANGLE));
            mLinearGradient = new LinearGradient(0, 0, x1, y1,
                    new int[]{getZheColor(), getZheFlashColor(), getZheColor()},
                    null, Shader.TileMode.CLAMP);
            mPaint.setShader(mLinearGradient);
        }
    }

    // 变量定义到全局，防止重复创建变量
    @Override
    protected void onDraw(Canvas canvas) {
        int layer = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null,
                Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        calcPath();
        canvas.drawPath(mPath, mPaint);

        canvas.restoreToCount(layer);
    }

    /**
     * 计算 Path
     */
    private void calcPath() {
        mPath.reset();

        float x1 = (mFlashWidth + getWidth() + getHeight() / (float) Math.tan(TILT_ANGLE)) *
                mProgress - mFlashWidth;
        mPath.moveTo(x1, 0);
        mPath.lineTo(x1 + mFlashWidth, 0);
        mPath.lineTo(x1 + mFlashWidth - getHeight() / (float) Math.tan(TILT_ANGLE), getHeight());
        mPath.lineTo(x1 - getHeight() / (float) Math.tan(TILT_ANGLE), getHeight());

        mPath.close();

        mGradientMatrix.setTranslate(x1, 0);
        mLinearGradient.setLocalMatrix(mGradientMatrix);
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    private float mProgress;

    @Override
    public void start() {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofFloat(0, 1.5f);
            mAnimator.setDuration(DURATION);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (float) animation.getAnimatedValue();
                    if (mProgress <= 1) {
                        postInvalidate();
                    } else if (mProgress > 1) {
                        mProgress = 1;
                    }
                }
            });
        }
        mAnimator.cancel();
        mAnimator.start();
    }

    @Override
    public void stop() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    @Override
    public boolean isRunning() {
        if (mAnimator != null) {
            return mAnimator.isRunning();
        }
        return false;
    }


    private static TypedValue sOutValue = new TypedValue();

    private int getZheColor() {
        return resolveAttribute(R.color.color_zhe);
    }

    private int getZheFlashColor() {
        return resolveAttribute(R.color.color_zhe_flash);
    }

    private int resolveAttribute(int resId) {
        Resources.Theme theme = getContext().getTheme();
        if (theme.resolveAttribute(resId, sOutValue, true)) {
            switch (sOutValue.type) {
                case TypedValue.TYPE_INT_COLOR_ARGB4:
                case TypedValue.TYPE_INT_COLOR_ARGB8:
                case TypedValue.TYPE_INT_COLOR_RGB4:
                case TypedValue.TYPE_INT_COLOR_RGB8:
                    return sOutValue.data;
            }
        }
        return Color.TRANSPARENT;
    }
}
