package cn.zgy.autoview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动变换 text size
 *
 * @author a_liYa
 * @date 2017/11/4 11:33.
 */
public class AutoSizeTextView extends AppCompatTextView {

    private List<SizeRange> mSizeRanges;

    private static final String SEPARATOR_GROUP = ";";
    private static final String SEPARATOR = ",";

    public AutoSizeTextView(Context context) {
        this(context, null);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) return;

        Context context = getContext();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoSizeText);
        if (ta.hasValue(R.styleable.AutoSizeText_sizeRange)) {
            String sizeRangeStr = ta.getString(R.styleable.AutoSizeText_sizeRange);
            if (!TextUtils.isEmpty(sizeRangeStr)) {
                String[] splits = sizeRangeStr.split(SEPARATOR_GROUP);
                if (splits != null && splits.length > 0) {
                    mSizeRanges = new ArrayList<>();
                    for (String split : splits) {
                        try {
                            SizeRange sizeRange = parseSizeRange(split);
                            if (sizeRange != null) {
                                mSizeRanges.add(sizeRange);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private SizeRange parseSizeRange(String str) {
        SizeRange sizeRange = null;
        if (!TextUtils.isEmpty(str)) {
            String[] split = str.split(SEPARATOR);
            if (split != null && split.length == 3) {
                sizeRange = new SizeRange();
                String tss = split[0];
                if (!TextUtils.isEmpty(tss)) {
                    tss = tss.toLowerCase();
                    if (tss.endsWith("sp")) {
                        sizeRange.textSize = Integer.valueOf(tss.substring(0, tss.length() - 2));
                        sizeRange.unit = TypedValue.COMPLEX_UNIT_SP;
                    } else if (tss.endsWith("dp")) {
                        sizeRange.textSize = Integer.valueOf(tss.substring(0, tss.length() - 2));
                        sizeRange.unit = TypedValue.COMPLEX_UNIT_DIP;
                    } else if (tss.endsWith("dip")) {
                        sizeRange.textSize = Integer.valueOf(tss.substring(0, tss.length() - 3));
                        sizeRange.unit = TypedValue.COMPLEX_UNIT_DIP;
                    } else if (tss.endsWith("px")) {
                        sizeRange.textSize = Integer.valueOf(tss.substring(0, tss.length() - 2));
                        sizeRange.unit = TypedValue.COMPLEX_UNIT_PX;
                    }
                }
                if (!TextUtils.isEmpty(split[1])) {
                    sizeRange.start = TextUtils.equals(split[1], "+") ? Integer.MAX_VALUE :
                            Integer.valueOf(split[1]);
                }
                if (!TextUtils.isEmpty(split[2])) {
                    sizeRange.end = TextUtils.equals(split[2], "+") ? Integer.MAX_VALUE :
                            Integer.valueOf(split[2]);
                }
            }
        }
        return sizeRange;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mSizeRanges != null && !TextUtils.isEmpty(text)) {
            for (SizeRange sizeRange : mSizeRanges) {
                if (sizeRange.effective()) {
                    if (sizeRange.start <= text.length() && sizeRange.end >= text.length()) {
                        setTextSize(sizeRange.unit, sizeRange.textSize);
                        break;
                    }
                }
            }
        }
        super.setText(text, type);
    }

    private static class SizeRange {

        public int start = -1;
        public int end = -1;

        public int textSize = -1;
        public int unit;

        public boolean effective() {
            return start != -1 && end != -1 && textSize != -1;
        }
    }

}
