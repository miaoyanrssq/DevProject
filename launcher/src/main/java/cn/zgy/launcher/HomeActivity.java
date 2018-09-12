package cn.zgy.launcher;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zgy.base.BaseActivity;
import cn.zgy.base.widget.GuideView;
import cn.zgy.launcher.manager.TabManager;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class HomeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String FIRST_TAB_TAG = "home";

    @BindView(R2.id.launcher_bottom)
    RadioGroup launcherBottom;
    @BindView(R2.id.launcher_home)
    RadioButton launcherHome;
    @BindView(R2.id.connect_title)
    TextView connectTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.launcher_activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        TabManager.getInstance().switchTab(getSupportFragmentManager(), R.id.launcher_content, FIRST_TAB_TAG);
        launcherHome.setChecked(true);
        launcherBottom.setOnCheckedChangeListener(this);

        setupNewFunctionGuide();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        try {
            String tag = group.findViewById(checkedId).getTag().toString();
            TabManager.getInstance().switchTab(getSupportFragmentManager(), R.id.launcher_content, tag);

            if (checkedId == R.id.launcher_home) {

            } else if (checkedId == R.id.launcher_local) {

            } else if (checkedId == R.id.launcher_video) {

            } else if (checkedId == R.id.launcher_subscription) {


            } else if (checkedId == R.id.launcher_discovery) {


            }

        } catch (Exception e) {
        }
    }


    /**
     * 新功能引导页
     */
    private void setupNewFunctionGuide() {
        GuideView.Builder step2 = new GuideView.Builder(this)
                .setGravity(Gravity.BOTTOM)
                .setAnchorView(findViewById(R.id.launcher_video))
                .setGuideResource(R.drawable.launcher_bottom_guide)
                .setTag("launcherBottom");

        final GuideView.Builder step1 = new GuideView.Builder(this)
                .setGravity(Gravity.RIGHT)
                .setGuideResource(R.drawable.user_center_guide)
                .setAnchorView(findViewById(R.id.launcher_user_center_view))
                .setNext(step2)
                .setTag("userCenter");


        launcherHome.postDelayed(new Runnable() {
            @Override
            public void run() {
                step1.build();
            }
        }, 1000);
    }


    int mClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mClickCount == 0) {
            mClickCount++;
            Toast.makeText(this, R.string.exit_application_tip, Toast.LENGTH_SHORT).show();
            Observable.timer(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    mClickCount = 0;
                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R2.id.connect_title)
    public void onViewClicked() {
        toPath("/ConnectActivity");
    }
}
