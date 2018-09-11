package com.zhouyou.http.loadview;


import com.zhouyou.http.subsciber.BaseSubscriber;

/**
 * Created by lixinke on 2017/11/8.
 */

public interface ILoad {
    void finishLoad();

    void showFailed(int errCode);

    void setSubscriber(BaseSubscriber baseSubscriber);
}
