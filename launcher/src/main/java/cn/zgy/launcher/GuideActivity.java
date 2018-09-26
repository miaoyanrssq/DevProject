package cn.zgy.launcher;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.zgy.banner.BannerView;
import cn.zgy.banner.transformer.TransitionEffect;
import cn.zgy.base.BaseActivity;
import cn.zgy.launcher.adapter.BannerAdapter;

public class GuideActivity extends BaseActivity {

    private BannerView bannerView;
    //本地图片
    private ArrayList<Integer> localImages = new ArrayList<Integer>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFullWindow();
        setSwipeBackEnable(true);
        addLocalImg();
        initGuide();
    }


    private void addLocalImg() {
        localImages.add(R.drawable.img1);
        localImages.add(R.drawable.img2);
        localImages.add(R.drawable.img3);
        localImages.add(R.drawable.img4);
        localImages.add(R.drawable.img5);
    }

    private void initGuide() {
        bannerView = (BannerView) findViewById(R.id.banners);
        bannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        bannerView.isGuide(true)
                .setAutoPlay(false)
                .setCanLoop(false)
                .setScrollDurtion(800)
                .setIndicatorBottomPadding(30)
                .setIndicatorWidth(10)
                .setHoriZontalTransitionEffect(TransitionEffect.Cube)
                .setIndicatorPosition(BannerView.IndicaTorPosition.BOTTOM_MID)
                .setOnStartListener(R.drawable.button_shape, 0XFFAACCBB, new BannerView.onStartListener() {
                    @Override
                    public void startOpen() {
                        //回调跳转的逻辑
                        toPath("/HomeActivity");
                    }
                });
        bannerView.setAdapter(new BannerAdapter(GuideActivity.this), localImages);
    }


    /**
     * 配置窗口属性 设置全屏
     */
    private void initFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
