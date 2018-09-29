package cn.zgy.playerdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.kk.taurus.playerbase.assist.AssistPlay;
import com.kk.taurus.playerbase.assist.OnAssistPlayEventHandler;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.window.FloatWindow;
import com.kk.taurus.playerbase.window.FloatWindowParams;

import java.util.ArrayList;
import java.util.List;

import cn.zgy.base.BaseFragment;
import cn.zgy.multitype.MultiTypeAdapter;
import cn.zgy.playerdemo.bean.VideoBean;
import cn.zgy.playerdemo.binder.VideoBinder;
import cn.zgy.playerdemo.cover.CloseCover;
import cn.zgy.playerdemo.cover.CompleteCover;
import cn.zgy.playerdemo.cover.ControllerCover;
import cn.zgy.playerdemo.cover.ErrorCover;
import cn.zgy.playerdemo.cover.LoadingCover;
import cn.zgy.playerdemo.play.AssistPlayer;
import cn.zgy.playerdemo.play.DataInter;
import cn.zgy.playerdemo.utils.DataUtils;
import cn.zgy.playerdemo.utils.WindowPermissionCheck;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static cn.zgy.playerdemo.play.DataInter.ReceiverKey.KEY_COMPLETE_COVER;
import static cn.zgy.playerdemo.play.DataInter.ReceiverKey.KEY_CONTROLLER_COVER;
import static cn.zgy.playerdemo.play.DataInter.ReceiverKey.KEY_ERROR_COVER;
import static cn.zgy.playerdemo.play.DataInter.ReceiverKey.KEY_LOADING_COVER;

/**
 * 视频列表展示， 小窗口展示，页面跳转及全屏
 *
 * @author zhengy
 * create at 2018/9/29 下午3:07
 **/
public class PlayerListFragment extends BaseFragment implements VideoBinder.OnListListener, VideoBinder.OnSwitchWindowListener,
        OnReceiverEventListener, OnPlayerEventListener {

    Context context;
    private List<VideoBean> mItems = new ArrayList<>();
    private RecyclerView mRecycler;

    MultiTypeAdapter adapter;
    VideoBinder videoBinder;

    private ReceiverGroup mReceiverGroup;

    private FrameLayout mWindowVideoContainer;
    private FloatWindow mFloatWindow;

    boolean isWindow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playerlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        initView();
    }

    private void initView() {
        initRecycleView();

        initReceiveGroup();

        initFloatWindow();
    }


    private void initRecycleView() {
        mRecycler = findViewById(R.id.recycler);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(context));
        mRecycler.addItemDecoration(new DividerItemDecoration(context, VERTICAL));
        adapter = new MultiTypeAdapter();
        videoBinder = new VideoBinder(context, mRecycler);
        videoBinder.setOnListListener(this);
        videoBinder.setOnSwitchWindowListener(this);
        adapter.register(VideoBean.class, videoBinder);
        mRecycler.setAdapter(adapter);
        mItems = DataUtils.getVideoList();
        adapter.setItems(mItems);
        adapter.notifyDataSetChanged();
    }

    /**
     * cover等配置
     */
    private void initReceiveGroup() {
        mReceiverGroup = new ReceiverGroup();
        mReceiverGroup.addReceiver(KEY_LOADING_COVER, new LoadingCover(context));
        mReceiverGroup.addReceiver(KEY_CONTROLLER_COVER, new ControllerCover(context));
        mReceiverGroup.addReceiver(KEY_COMPLETE_COVER, new CompleteCover(context));
        mReceiverGroup.addReceiver(KEY_ERROR_COVER, new ErrorCover(context));

        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
        AssistPlayer.get(context).setReceiverGroup(mReceiverGroup);

    }

    /**
     * 浮窗配置
     */
    private void initFloatWindow() {
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0+
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int width = (int) (widthPixels * 0.8f);
        mWindowVideoContainer = new FrameLayout(context);
        mFloatWindow = new FloatWindow(context, mWindowVideoContainer,
                new FloatWindowParams()
                        .setWindowType(type)
                        .setX((int) (widthPixels * 0.2f))
                        .setY(getResources().getDisplayMetrics().heightPixels - width * 9 / 16 - 400)
                        .setWidth(width)
                        .setHeight(width * 9 / 16));
        mFloatWindow.setBackgroundColor(Color.BLACK);

    }


    private void attachList() {
        if (adapter != null) {
            mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_CLOSE_COVER);
            mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
            mRecycler.post(new Runnable() {
                @Override
                public void run() {
                    VideoBinder.VideoHolder itemHolder = videoBinder.getItemHolder();
                    if (itemHolder != null) {
                        AssistPlayer.get(context).play(itemHolder.layoutContainer, null);
                    }
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AssistPlayer.get(context).addOnReceiverEventListener(this);
        AssistPlayer.get(context).addOnPlayerEventListener(this);
        mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
        AssistPlayer.get(context).setReceiverGroup(mReceiverGroup);
        AssistPlayer.get(context).setEventAssistHandler(eventHandler);
        if (isWindow) {
            windowPlay();
        } else {
            attachList();
        }
        int state = AssistPlayer.get(context).getState();
        if (state != IPlayer.STATE_PLAYBACK_COMPLETE) {
            AssistPlayer.get(context).resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AssistPlayer.get(context).pause();
        AssistPlayer.get(context).removeReceiverEventListener(this);
        AssistPlayer.get(context).removePlayerEventListener(this);
        AssistPlayer.get(context).setEventAssistHandler(null);
        isWindow = mFloatWindow.isWindowShow();
        closeWindow();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            closeWindow();
            videoBinder.updatePlayPosition(-1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AssistPlayer.get(context).destroy();
        closeWindow();
    }

    @Override
    public void onTitleClick(VideoBean item, int position) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailPageActivity.KEY_ITEM, item);
        toPath("/DetailPageActivity", 0, bundle);
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
//        switch (eventCode) {
            //此处和eventHandler中重复，只做一次处理
//            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
//                break;
//            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
//                isWindow = false;
//                toPath("/FullScreenActivity");
//                break;
//        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                AssistPlayer.get(context).stop();
                break;
            default:
                break;

        }
    }

    private OnAssistPlayEventHandler eventHandler = new OnAssistPlayEventHandler() {
        @Override
        public void onAssistHandle(AssistPlay assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode) {
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    AssistPlayer.get(context).stop();
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_CLOSE_COVER);
                    toPath("/FullScreenActivity");
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_CLOSE:
                    closeWindow();
                    AssistPlayer.get(context).stop();
                    videoBinder.updatePlayPosition(-1);
                    break;
                default:
                    break;
            }
        }
    };

    public void switchWindowPlay(boolean isWindow) {
        if (isWindow && !mFloatWindow.isWindowShow()) {
            if (WindowPermissionCheck.checkPermission(getActivity())) {
                windowPlay();
            }
        } else {
            attachList();
            closeWindow();
        }
    }

    private void windowPlay() {
        if (!mFloatWindow.isWindowShow()) {
            mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
            mReceiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CLOSE_COVER, new CloseCover(context));
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
            mFloatWindow.setElevationShadow(20);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFloatWindow.setRoundRectShape(50);
            }
            mFloatWindow.show();
            AssistPlayer.get(context).play(mWindowVideoContainer, null);
        }
    }

    private void closeWindow() {
        if (mFloatWindow.isWindowShow()) {
            mFloatWindow.close();
        }
    }

    @Override
    public void onSwitchWindow(boolean isWndow) {
        switchWindowPlay(isWndow);
    }
}
