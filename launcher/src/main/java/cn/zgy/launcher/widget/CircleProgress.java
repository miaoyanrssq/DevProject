package cn.zgy.launcher.widget;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cn.zgy.launcher.R;


public class CircleProgress extends View implements Animator.AnimatorListener, View.OnClickListener {
    private Paint finishedPaint;
    private Paint unfinishedPaint;
    private Paint innerCirclePaint;

    protected Paint textPaint;

    private RectF finishedOuterRect = new RectF();
    private RectF unfinishedOuterRect = new RectF();

    private int duration;

    private int attributeResourceId = 0;
    private boolean showText;
    private float textPadding;
    private float textSize;
    private int textColor;
    private float progress = 0;
    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private int startingDegree;
    private float finishedStrokeWidth;
    private float unfinishedStrokeWidth;
    private int innerBackgroundColor;
    private String text = null;

    private final float default_stroke_width;
    private final int default_finished_color = Color.rgb(66, 145, 241);
    private final int default_unfinished_color = Color.rgb(204, 204, 204);
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int default_inner_background_color = Color.TRANSPARENT;
    private final int default_max = 100;
    private final int default_startingDegree = 0;
    private int default_duration = 1000;
    private final float default_text_size;
    private final int min_size;


    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_TEXT = "text";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width";
    private static final String INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width";
    private static final String INSTANCE_BACKGROUND_COLOR = "inner_background_color";
    private static final String INSTANCE_STARTING_DEGREE = "starting_degree";
    private static final String INSTANCE_INNER_DRAWABLE = "inner_drawable";


    private OnCircleProgressListener mOnCircleProgressListener;
    private CountDownTimer timer;

    public void setOnCircleProgressListener(OnCircleProgressListener onCircleProgressListener) {
        mOnCircleProgressListener = onCircleProgressListener;
    }

    public void cancel() {
        mOnCircleProgressListener = null;
        if (timer != null) {
            timer.onFinish();
            timer.cancel();
        }
    }

    public void finish() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public interface OnCircleProgressListener {
        void onFinish();

        void onClick(View view);
    }

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = sp2px(getResources(), 18);
        min_size = (int) dp2px(getResources(), 20);
        default_stroke_width = dp2px(getResources(), 10);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();

        setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void start() {
        timer = new CountDownTimer(duration,15) {
            @Override
            public void onTick(long millisUntilFinished) {
                progress = (duration-Float.valueOf(millisUntilFinished))*100f/duration;
                invalidate();
            }

            @Override
            public void onFinish() {
                if (mOnCircleProgressListener != null) {
                    mOnCircleProgressListener.onFinish();
                }
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        if (timer != null) {
            timer.onFinish();
            timer.cancel();
        }
        if (mOnCircleProgressListener != null) {
            mOnCircleProgressListener.onClick(this);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (mOnCircleProgressListener != null) {
            mOnCircleProgressListener.onFinish();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void initPainters() {
        if (showText) {
            textPaint = new TextPaint();
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            textPaint.setAntiAlias(true);
        }

        finishedPaint = new Paint();
        finishedPaint.setColor(finishedStrokeColor);
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(finishedStrokeWidth);

        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(innerBackgroundColor);
        innerCirclePaint.setAntiAlias(true);
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.CircleProgress_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.CircleProgress_unfinished_color, default_unfinished_color);
        showText = attributes.getBoolean(R.styleable.CircleProgress_show_text, true);
        attributeResourceId = attributes.getResourceId(R.styleable.CircleProgress_inner_drawable, 0);

        setMax(attributes.getInt(R.styleable.CircleProgress_max, default_max));
        setProgress(attributes.getFloat(R.styleable.CircleProgress_progress, 0));
        finishedStrokeWidth = attributes.getDimension(R.styleable.CircleProgress_finished_stroke_width, default_stroke_width);
        unfinishedStrokeWidth = attributes.getDimension(R.styleable.CircleProgress_unfinished_stroke_width, default_stroke_width);

        if (showText) {
            if (attributes.getString(R.styleable.CircleProgress_text) != null) {
                text = attributes.getString(R.styleable.CircleProgress_text);
            }

            textPadding = attributes.getDimension(R.styleable.CircleProgress_text_padding, 0);

            textColor = attributes.getColor(R.styleable.CircleProgress_text_color, default_text_color);
            textSize = attributes.getDimension(R.styleable.CircleProgress_text_size, default_text_size);
        }

        startingDegree = attributes.getInt(R.styleable.CircleProgress_circle_starting_degree, default_startingDegree);
        innerBackgroundColor = attributes.getColor(R.styleable.CircleProgress_background_color, default_inner_background_color);
        duration = attributes.getInt(R.styleable.CircleProgress_duration, default_duration);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public float getFinishedStrokeWidth() {
        return finishedStrokeWidth;
    }

    public void setFinishedStrokeWidth(float finishedStrokeWidth) {
        this.finishedStrokeWidth = finishedStrokeWidth;
        this.invalidate();
    }

    public float getUnfinishedStrokeWidth() {
        return unfinishedStrokeWidth;
    }

    public void setUnfinishedStrokeWidth(float unfinishedStrokeWidth) {
        this.unfinishedStrokeWidth = unfinishedStrokeWidth;
        this.invalidate();
    }

    private float getProgressAngle() {
        return getProgress() / (float) max * 360f;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.invalidate();
    }


    public int getInnerBackgroundColor() {
        return innerBackgroundColor;
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        this.innerBackgroundColor = innerBackgroundColor;
        this.invalidate();
    }


    public int getStartingDegree() {
        return startingDegree;
    }

    public void setStartingDegree(int startingDegree) {
        this.startingDegree = startingDegree;
        this.invalidate();
    }

    public int getAttributeResourceId() {
        return attributeResourceId;
    }

    public void setAttributeResourceId(int attributeResourceId) {
        this.attributeResourceId = attributeResourceId;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (!TextUtils.isEmpty(text)) {
                int width = (int) (textPaint.measureText(text) + 2 * (Math.max(finishedStrokeWidth, unfinishedStrokeWidth) + textPadding));
                result = Math.max(width, result);
            }

            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth);
        finishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);
        unfinishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);

        float innerCircleRadius = (getWidth() - Math.min(finishedStrokeWidth, unfinishedStrokeWidth) + Math.abs(finishedStrokeWidth - unfinishedStrokeWidth)) / 2f;
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, innerCircleRadius, innerCirclePaint);
        canvas.drawArc(finishedOuterRect, getStartingDegree(), getProgressAngle(), false, finishedPaint);
        canvas.drawArc(unfinishedOuterRect, getStartingDegree() + getProgressAngle(), 360 - getProgressAngle(), false, unfinishedPaint);

        if (showText) {
            String text = this.text != null ? this.text : "";
            if (!TextUtils.isEmpty(text)) {
                float textHeight = textPaint.descent() + textPaint.ascent();
                canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
            }
        }

        if (attributeResourceId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), attributeResourceId);
            canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2.0f, (getHeight() - bitmap.getHeight()) / 2.0f, null);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_STARTING_DEGREE, getStartingDegree());
        bundle.putFloat(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_TEXT, getText());
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth());
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth());
        bundle.putInt(INSTANCE_BACKGROUND_COLOR, getInnerBackgroundColor());
        bundle.putInt(INSTANCE_INNER_DRAWABLE, getAttributeResourceId());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            finishedStrokeWidth = bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH);
            unfinishedStrokeWidth = bundle.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH);
            innerBackgroundColor = bundle.getInt(INSTANCE_BACKGROUND_COLOR);
            attributeResourceId = bundle.getInt(INSTANCE_INNER_DRAWABLE);
            initPainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setStartingDegree(bundle.getInt(INSTANCE_STARTING_DEGREE));
            setProgress(bundle.getFloat(INSTANCE_PROGRESS));
            text = bundle.getString(INSTANCE_TEXT);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void setCircleProgress(String percent) {
        if (!TextUtils.isEmpty(percent)) {
            setProgress(Integer.parseInt(percent));
        }
    }

    private float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
