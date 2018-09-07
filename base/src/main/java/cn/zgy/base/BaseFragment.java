package cn.zgy.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;


import java.lang.reflect.Field;

import cn.zgy.base.permission.IPermissionOperate;
import cn.zgy.base.permission.PermissionManager;
import cn.zgy.nav.Nav;

/**
 * Fragment基类
 *
 * @author a_liYa
 * @date 2016-3-27 下午8:43:53
 */
public class BaseFragment extends Fragment implements IPermissionOperate {

    /**
     * Fragment所依附的父容器
     */
    protected ViewGroup container;

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            view.setTag(R.id.tag_fragment, this);
            if (view.getParent() instanceof ViewGroup) {
                this.container = (ViewGroup) view.getParent();
            }
        }
    }

//    /**
//     * 替换加载View
//     */
//    public LoadViewHolder replaceLoad() {
//        return new LoadViewHolder(getView(), container);
//    }
//
//    /**
//     * 替换加载View
//     *
//     * @param pageView 需要替换成加载View的该页面View
//     */
//    public LoadViewHolder replaceLoad(View pageView) {
//        return new LoadViewHolder(pageView, container);
//    }
//
//    /**
//     * 替换加载View
//     *
//     * @param id 需要替换成加载View的该页面View的id
//     */
//    public LoadViewHolder replaceLoad(@IdRes int id) {
//        return replaceLoad(findViewById(id));
//    }

    /**
     * 返回Fragment自身<br/>
     * 匿名内部类中使用
     *
     * @return this
     */
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isAdded()) {
            try {
                Fragment fragment = getChildFragmentManager().findFragmentByTag("video.manager");
                if (fragment != null) {
                    fragment.onHiddenChanged(hidden);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getView() != null) {
            getView().setTag(R.id.tag_fragment, null);
        }
    }

    /**
     * findViewById
     */
    public <T extends View> T findViewById(int id) {
        if (getView() != null)
            return (T) getView().findViewById(id);
        return null;
    }

    // http://stackoverflow.com/questions/15207305/getting-the-error-java-lang
    // -illegalstateexception-activity-has-been-destroyed
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            // throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.get().onRequestPermissionsResult(requestCode, permissions,
                grantResults, this);
    }

    @Override
    public void exeRequestPermissions(@NonNull String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    @Override
    public boolean exeShouldShowRequestPermissionRationale(@NonNull String permission) {
        return shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    /**
     * 查找View所依附的Fragment
     *
     * @param v view
     * @return Fragment
     */
    public static Fragment findAttachFragmentByView(View v) {
        while (v != null) {
            Object tag = v.getTag(R.id.tag_fragment);
            if (tag instanceof Fragment) {
                return (Fragment) tag;
            }
            if (v.getParent() instanceof View) {
                v = (View) v.getParent();
            } else {
                v = null;
            }
        }
        return null;
    }


    /**
     * 跳转
     *
     * @param url 完整url
     */
    public void to(String url) {
        Nav.with(this).to(url);
    }

    /**
     * 跳转
     *
     * @param path
     */
    public void toPath(String path) {
        Nav.with(this).toPath(path);
    }
}
