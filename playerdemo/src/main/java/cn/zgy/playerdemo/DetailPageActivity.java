package cn.zgy.playerdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;

import cn.zgy.playerdemo.bean.VideoBean;
import cn.zgy.playerdemo.cover.GestureCover;
import cn.zgy.playerdemo.play.AssistPlayer;
import cn.zgy.playerdemo.play.DataInter;
import cn.zgy.playerdemo.utils.OrientationHelper;

/**
 * Created by Taurus on 2018/4/18.
 */

public class DetailPageActivity extends AppCompatActivity implements OnReceiverEventListener {

    public static final String KEY_ITEM = "item_data";

    private RelativeLayout mLayoutContainer;

    private boolean isLandscape;
    private IReceiverGroup mReceiverGroup;

    private OrientationHelper mOrientationHelper;
    private boolean isOnBackPress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_page);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mOrientationHelper = new OrientationHelper(this);

        VideoBean item = (VideoBean) getIntent().getSerializableExtra(KEY_ITEM);

        mLayoutContainer = findViewById(R.id.layoutContainer);

        AssistPlayer.get(this).addOnReceiverEventListener(this);

        mReceiverGroup = AssistPlayer.get(this).getReceiverGroup();
        mReceiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new GestureCover(this));
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);

        DataSource intentDataSource = new DataSource(item.getPath());
        intentDataSource.setTitle(item.getDisplayName());
        DataSource dataSource = null;
        DataSource playData = AssistPlayer.get(this).getDataSource();
        boolean dataChange = playData!=null && !playData.getData().equals(intentDataSource.getData());
        if(!AssistPlayer.get(this).isInPlaybackState() || dataChange){
            dataSource = intentDataSource;
        }

        mReceiverGroup.getGroupValue().putObject(DataInter.Key.KEY_DATA_SOURCE, intentDataSource);

        AssistPlayer.get(this).play(mLayoutContainer, dataSource);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandscape = newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE;
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mOrientationHelper.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isOnBackPress)
            AssistPlayer.get(this).pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AssistPlayer.get(this).resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOrientationHelper.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AssistPlayer.get(this).removeReceiverEventListener(this);

    }

    @Override
    public void onBackPressed() {
        if(!mOrientationHelper.isPortrait()){
            mOrientationHelper.toggleScreen();
            return;
        }
        isOnBackPress = true;
        super.onBackPressed();
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                mOrientationHelper.toggleScreen();
                break;
            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                onBackPressed();
                break;
        }
    }
}
