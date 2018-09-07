package cn.zgy.base.toolbar.holder;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.zgy.base.R;


/**
 * 默认TopBar：左边返回 - 中间文字
 *
 * @author a_liYa
 * @date 2017/7/25 17:15.
 */
public class DefaultTopBarHolder extends TopBarViewHolder {

    TextView tvTitle;

    public DefaultTopBarHolder(ViewGroup view, Activity activity, String title) {
        super(view, activity);
        tvTitle = findViewById(R.id.tv_top_bar_title);
        tvTitle.setText(title);
        setBackOnClickListener(R.id.iv_top_bar_back);
    }

    /**
     * 动态设置title
     *
     * @param title
     */
    public void setTopBarText(String title) {
        tvTitle.setText(title);
    }

    public TextView getTitleView() {
        return tvTitle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.default_topbar;
    }

}
