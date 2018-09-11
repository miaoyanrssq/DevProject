package cn.zgy.launcher.manager;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import cn.zgy.launcher.fragment.AdFragment;
import cn.zgy.launcher.fragment.TestFragment;
import cn.zgy.multilist.Loading_NewsFragment;
import cn.zgy.multilist.NewsFragment;
import cn.zgy.multilist.OneToManyFragment;
import cn.zgy.news.HomeFragment;


/**
 * 主页面TAB切换管理工具类，全局唯一
 */

public class TabManager {
    private static final TabManager instance = new TabManager();

    public static TabManager getInstance() {
        return instance;
    }

    private Fragment mCurFragment;


    private TabManager() {
    }

    /**
     * 创建新的TAB页面
     *
     * @param tag 页面对应的TAG，在主页面唯一，用来查找已经创建的Fragment
     * @return 返回对应TAG的Fragment
     */
    private Fragment newTab(String tag) {
        Fragment fragment = null;
        switch (tag) {
            case "home":
                fragment = new HomeFragment();
                break;
            case "local":
                fragment=new OneToManyFragment();
                break;
            case "video":
                fragment=new Loading_NewsFragment();
                break;
            case "subscription":
                fragment = new TestFragment();
                break;
            case "discovery":
                fragment=new TestFragment();
                break;
        }
        return fragment;
    }

    /**
     * 切换Fragment
     *
     * @param fragmentManager fragmentManager
     * @param containerId     主页面中的容器ID，用来加载不同的Fragment
     * @param tag             需要切换的Fragment对应的TAG
     */
    public void switchTab(FragmentManager fragmentManager, @IdRes int containerId, String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = newTab(tag);
            if (fragment == null) {
                return;
            }
            fragmentManager.beginTransaction().add(containerId, fragment, tag).commit();
        } else if (mCurFragment != null) {
            fragmentManager.beginTransaction().show(fragment).commit();
        }

        if (mCurFragment != null && mCurFragment != fragment) {
            fragmentManager.beginTransaction().hide(mCurFragment).commit();
        }
        mCurFragment = fragment;
    }

}
