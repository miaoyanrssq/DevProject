
package cn.zgy.base.swipeback.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.zgy.base.swipeback.SwipeBackLayout;
import cn.zgy.base.swipeback.Utils;


/**
 * 边缘手势 - 侧滑退出Activity
 *
 * @author a_liYa
 * @date 2016/9/30 18:38.
 */
public class SwipeBackActivity extends AppCompatActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;
    private boolean mNeverSwipeBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mNeverSwipeBack) {
            mHelper = new SwipeBackActivityHelper(this);
            mHelper.onActivityCreate();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!mNeverSwipeBack) {
            mHelper.onPostCreate();
        }
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    /**
     * 设置是否支持边缘手势
     *
     * @param enable
     */
    @Override
    public void setSwipeBackEnable(boolean enable) {
        if (!mNeverSwipeBack) {
            getSwipeBackLayout().setEnableGesture(enable);
        }
    }

    /**
     * 设置永不支持边缘手势
     */
    public void setNeverSwipeBack() {
        mNeverSwipeBack = true;
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    /**
     * 设置边缘滑动退出方式
     *
     * @param edgeFlags SwipeBackLayout.EDGE_LEFT 左边缘滑动退出
     *                  SwipeBackLayout.EDGE_RIGHT 右边缘滑动退出
     *                  SwipeBackLayout.EDGE_BOTTOM 下边缘滑动退出
     *                  SwipeBackLayout.EDGE_ALL 左、右、下边缘滑动退出
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        getSwipeBackLayout().setEdgeTrackingEnabled(edgeFlags);
    }

}
