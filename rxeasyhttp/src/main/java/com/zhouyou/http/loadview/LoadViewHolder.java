package com.zhouyou.http.loadview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zhouyou.http.R;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.BaseSubscriber;
import com.zhouyou.http.subsciber.CallBackSubsciber;
import com.zhouyou.http.utils.ClickTracker;

import io.reactivex.annotations.NonNull;



/**
* 配合网络库实现加载动画，及加载后结果展示，{@link CallBackSubsciber}CallBackSubsciber中绑定逻辑
* @author zhengy
* create at 2018/9/11 下午2:32
**/
public class LoadViewHolder implements View.OnClickListener, ILoad {

    // 根布局
    private View rootView;
    private FrameLayout fitSysHelper;

    // 失败view
    private ViewStub failedStub;
    private View failedView;

    // 网络错误
    private ViewStub networkStub;
    private View networkView;

    // 加载中view
    private View loadView;

    // 页面被替换view
    private View pageView;

    private ViewGroup pageViewGroup;


    CallBackSubsciber callBackSubsciber;

    private int pageViewIndex = -1;

    /**
     * 构造函数
     *
     * @param pageViewGroup   需要替换成加载动画的ViewGroup
     * @param pageParent 需要追加加载动画的父容器（前提时pageView没有）
     */
    public LoadViewHolder(@NonNull View pageViewGroup, ViewGroup pageParent) {
        this.pageView = pageView;
        if (pageView.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) pageView.getParent();
            rootView = inflate(R.layout.module_core_layout_global_load, parent, false);
            initView();

            // 替换成加载布局
            pageViewIndex = parent.indexOfChild(pageView);
            parent.removeView(pageView);

            // 为了适配fitSystemWindow=true属性
            fitSysHelper.addView(pageView);

            parent.addView(rootView, pageViewIndex, pageView.getLayoutParams());
        } else if (pageParent != null) {
            rootView = inflate(R.layout.module_core_layout_global_load, pageParent, false);
            initView();

            // 为了适配fitSystemWindow=true属性
            fitSysHelper.addView(pageView);

            pageView.setVisibility(View.GONE);
            pageParent.addView(rootView);


        }

        if (rootView != null) {
//            TextView tvName = (TextView) rootView.findViewById(R.id.tv_app_name);
//            String app_name = UIUtils.getString(R.string.app_name);
//            tvName.setText(app_name.replaceAll("(.{1})", "$1 ").trim());
        }

    }

    public LoadViewHolder(@NonNull ViewGroup pageView, ViewGroup pageParent) {
        this.pageViewGroup = pageView;
        if (pageView.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) pageView.getParent();
            rootView = inflate(R.layout.module_core_layout_global_load, parent, false);
            initView();

            // 替换成加载布局
            pageViewIndex = parent.indexOfChild(pageView);
            parent.removeView(pageView);

            // 为了适配fitSystemWindow=true属性
            fitSysHelper.addView(pageView);

            parent.addView(rootView, pageViewIndex, pageView.getLayoutParams());
        } else if (pageParent != null) {
            rootView = inflate(R.layout.module_core_layout_global_load, pageParent, false);
            initView();

            // 为了适配fitSystemWindow=true属性
            fitSysHelper.addView(pageView);

            pageView.setVisibility(View.GONE);
            pageParent.addView(rootView);


        }

        if (rootView != null) {
//            TextView tvName = (TextView) rootView.findViewById(R.id.tv_app_name);
//            String app_name = UIUtils.getString(R.string.app_name);
//            tvName.setText(app_name.replaceAll("(.{1})", "$1 ").trim());
        }

    }
    private void initView() {
        loadView = rootView.findViewById(R.id.layout_loading);
        failedStub = (ViewStub) rootView.findViewById(R.id.view_stub_failed);
        networkStub = (ViewStub) rootView.findViewById(R.id.view_stub_network_error);
        fitSysHelper = (FrameLayout) rootView.findViewById(R.id.fit_sys_helper);
    }

    // 显示失败页
    @Override
    public void showFailed(int errCode) {
        loadView.setVisibility(View.GONE);
        switch (errCode) {

            case ApiException.ERROR.NETWORD_ERROR: // 网络连接异常
                if (networkView == null) {
                    networkView = networkStub.inflate();
                    networkView.findViewById(R.id.layout_network_error).setOnClickListener(this);
                } else {
                    networkView.setVisibility(View.VISIBLE);
                }
                break;
            default: // 普通失败
                if (failedView == null) {
                    failedView = failedStub.inflate();
                    failedView.findViewById(R.id.layout_failed).setOnClickListener(this);
                } else {
                    failedView.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public void setSubscriber(BaseSubscriber baseSubscriber) {
        callBackSubsciber = (CallBackSubsciber) baseSubscriber;
    }


    // 显示加载中
    public void showLoading() {
        failedStub.setVisibility(View.GONE);
        networkStub.setVisibility(View.GONE);
        loadView.setVisibility(View.VISIBLE);
    }

    // 关闭Load页面
    @Override
    public void finishLoad() {
        if (rootView == null) return;

        final ViewGroup parent = (ViewGroup) rootView.getParent();
        if (pageView.getParent() != null && pageView.getParent() instanceof ViewGroup) {
            ((ViewGroup) pageView.getParent()).removeView(pageView);
        }
        if (pageView.getVisibility() != View.VISIBLE) {
            pageView.setVisibility(View.VISIBLE);
        }
        fitSysHelper.removeAllViews();
        if (parent != null) {
            parent.addView(pageView, pageViewIndex);
            rootView.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (rootView != null) {
                        rootView.setVisibility(View.INVISIBLE);
                        if (parent != null) {
                            try {
                                parent.removeView(rootView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (ClickTracker.isDoubleClick()) return;
        if (v.getId() == R.id.layout_failed || v.getId() == R.id.layout_network_error) {
            showLoading();
            if (callBackSubsciber != null) {
                callBackSubsciber.retry();
            }
        }
    }



    private static View inflate(int resource, ViewGroup parent, boolean attachToRoot) {
        return LayoutInflater.from(parent.getContext()).inflate(resource, parent, attachToRoot);
    }

    public LoadViewHolder setOption(Option option) {
        if (option != null && rootView != null) {
            rootView.setBackgroundColor(option.backgroundColor);
        }
        return this;
    }

    public static class Option {
        public int backgroundColor = -1;
        public int errImage = -1;
    }



}
