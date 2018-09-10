package android.support.design.widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

/**
* 针对 {@link TabLayout} 做优化：结合ViewPager使用, 选择不相邻tab时，ViewPager和TabLayout跳动剧烈
* @author zhengy
* create at 2018/9/10 上午9:44
**/
public class CompatTabLayout extends TabLayout {

    public CompatTabLayout(Context context) {
        super(context);
    }

    public CompatTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompatTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        if (positionOffset != 0) {
            super.setScrollPosition(position, positionOffset, updateSelectedText);
        }
    }
}
