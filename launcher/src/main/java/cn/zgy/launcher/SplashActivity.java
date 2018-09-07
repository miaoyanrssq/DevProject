package cn.zgy.launcher;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.vise.log.ViseLog;

import java.util.List;

import cn.zgy.base.BaseActivity;
import cn.zgy.base.permission.AbsPermSingleCallBack;
import cn.zgy.base.permission.Permission;
import cn.zgy.base.permission.PermissionManager;
import cn.zgy.launcher.fragment.AdFragment;
import cn.zgy.rxtool.RxSPTool;
/**
* 开屏页：权限请求，全屏， 第一次跳转引导页，广告读秒
* @author zhengy
* create at 2018/8/28 上午9:18
**/
public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFullWindow();
        permissionRequest();

    }

    private void permissionRequest() {
        if(RxSPTool.getBoolean(this, "isFirstStart", true)){
            PermissionManager.get().request(this, new AbsPermSingleCallBack() {
                @Override
                public void onGranted(boolean isAlreadyDef) {
                    ViseLog.d("onGranted");
                    goGuide();
                }

                @Override
                public void onDenied(List<String> neverAskPerms) {
                    ViseLog.d("onDenied");
                    goGuide();
                }
            }, Permission.LOCATION_COARSE, Permission.PHONE_READ_PHONE_STATE);
        }else{
            goGuide();
        }
    }


    private void goGuide(){
        if(RxSPTool.getBoolean(this, "isFirstStart", true)){
            toPath("/GuideActivity");
            RxSPTool.putBoolean(this, "isFirstStart", false);
            finish();
            return;
        }

        AdFragment adFragment = new AdFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.splash_ad_container, adFragment).commit();



        RxSPTool.putBoolean(this, "isFirstStart", false);

    }


    /**
     * 配置窗口属性 设置全屏
     */
    private void initFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}