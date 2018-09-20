package cn.zgy.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.zgy.base.BaseAgentWebActivity;
/**
* 继承BaseAgentWebActivity
* @author zhengy
* create at 2018/9/20 下午4:18
**/
public class BrowserActivity2 extends BaseAgentWebActivity {

    LinearLayout contener;
    ImageView back;
    TextView mTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        back = findById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitle = findById(R.id.tv_title);
        contener = findById(R.id.container);

    }


    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return findById(R.id.container);
    }

    @Override
    protected void setTitle(WebView view, String title) {
        mTitle.setText(title);

    }

    @Override
    protected int getIndicatorColor() {
        return getResources().getColor(R.color.colorPrimary);
    }

    @Override
    protected int getIndicatorHeight() {
        return 3;
    }

    @Nullable
    @Override
    protected String getUrl() {
        return "https://m.jd.com/";
    }
}
