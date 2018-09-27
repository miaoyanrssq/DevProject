package cn.zgy.user;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.zgy.autoview.SwitchView;
import cn.zgy.autoview.circleView.CircleImageView;
import cn.zgy.base.BaseActivity;
import cn.zgy.base.utils.StatusBarUtil;
import cn.zgy.blurview.RealtimeBlurView;

public class UserActivity extends BaseActivity implements SwitchView.SlideListener{

    CircleImageView icon;
    Toolbar toolbar;

    SwitchView switchView1, switchView2;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        //状态栏透明和间距处理
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurView));
    }

    private void initView() {
        toolbar = findById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        icon = findById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putInt("max_num", 1);
                toPath("/MediaSelectActivity", 0, bundle);
            }
        });

        switchView1 = findById(R.id.switch1);
        switchView1.setSlideListener(this);
        switchView2 = findById(R.id.switch2);
    }

    @Override
    public void open() {
        Toast.makeText(this, "tab2可以切换", Toast.LENGTH_SHORT).show();
        switchView2.setSlideable(true);
    }

    @Override
    public void close() {
        switchView2.setSlideable(false);
        Toast.makeText(this, "tab2禁止切换", Toast.LENGTH_SHORT).show();
    }
}
