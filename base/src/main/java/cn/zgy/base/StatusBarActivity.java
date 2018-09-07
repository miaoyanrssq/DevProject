package cn.zgy.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.zgy.base.swipeback.app.SwipeBackActivity;
/**
*
 *沉浸式状态栏
 * @author zhengy
* create at 2018/9/3 上午10:26
**/
public abstract class StatusBarActivity extends SwipeBackActivity {

    private View rootView;
    private ViewGroup container;
    /**
     * 获取根布局的资源ID
     *
     * @return
     */
    protected abstract int getLayoutId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(this).inflate(getLayoutId(), null);
        setContentView(rootView);
        container = rootView.findViewById(R.id.container);
        initStatusBar();
    }


    /**
     * 沉浸式状态栏
     */
    protected void initStatusBar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            //低于API19的情况设置非偏移高度，不支持沉浸式状态栏
            if (container != null) {
                ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
                layoutParams.height = UIUtils.dip2px(50);
                container.setLayoutParams(layoutParams);
            }
        }
    }

}
