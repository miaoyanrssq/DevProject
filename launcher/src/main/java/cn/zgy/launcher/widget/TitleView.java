package cn.zgy.launcher.widget;

import android.content.Context;
import com.aliya.view.fitsys.FitWindowsFrameLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;
import cn.zgy.launcher.R;

public class TitleView extends FitWindowsFrameLayout {
    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        View inflate;
        inflate = LayoutInflater.from(ctx).inflate(R.layout.launcher_layout_main_bar, this);
        ButterKnife.bind(this, inflate);
    }
}
