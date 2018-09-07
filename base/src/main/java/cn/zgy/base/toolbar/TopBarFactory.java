package cn.zgy.base.toolbar;

import android.app.Activity;
import android.view.ViewGroup;

import cn.zgy.base.toolbar.holder.DefaultTopBarHolder;


/**
 * TopBarFactory(生产TopBarHolder的工厂)
 *
 * @author a_liYa
 * @date 16/8/14 11:01.
 */
public final class TopBarFactory {

    /**
     * @param view
     * @param act
     * @param title
     * @return 通用顶部栏
     */
    public static DefaultTopBarHolder createDefault(ViewGroup view, Activity act, String title) {
        return new DefaultTopBarHolder(view, act, title);
    }




}
