package cn.zgy.autoview;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;


/**
* 自定义适配TabLayout的TextView
* @author zhengy
* create at 2018/9/10 上午10:24
**/
public class TabText extends AppCompatTextView {

    public TabText(Context context) {
        super(context);
    }

    public TabText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, selected ? 18 : 16);
//        setScaleX(selected ? 18f / 16 : 1);
//        setScaleY(selected ? 18f / 16 : 1);
        super.setSelected(selected);
    }

}
