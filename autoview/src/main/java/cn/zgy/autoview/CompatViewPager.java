package cn.zgy.autoview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ViewPager的兼容类
 * <p>
 * 1、禁止默认的{@link ViewPager#setCurrentItem(int)} smoothly scroll
 * 2、try catch IllegalArgumentException: pointerIndex out of range pointerIndex=-1 pointerCount=1
 *
 * @author a_liYa
 * @date 2017/8/30 17:40.
 */
public class CompatViewPager extends ViewPager {

    public CompatViewPager(Context context) {
        super(context);
    }

    public CompatViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException: pointerIndex out of range pointerIndex=-1 pointerCount=1
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * @see ViewPager#onTouchEvent(MotionEvent) : pointerIndex == -1 代码块
         */
        try {
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException: pointerIndex out of range pointerIndex=-1 pointerCount=1
            return false;
        }
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }

}
