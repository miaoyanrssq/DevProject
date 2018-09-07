package cn.zgy.base.toolbar;

import android.view.View;

/**
 * 点击左上角 back 回调接口
 *
 * @author a_liYa
 * @date 2017/7/25 19:15.
 */
public interface BackDownCallback {

    /**
     * 返回关闭之前的一个回调，给外部一个拦截机会，类似onKeyDown </br>
     *
     * @param v 被点击的View
     * @return true 表示外部拦截，内部不用处理事件；false 表示内部处理
     */
    boolean onBackDown(View v);

}
