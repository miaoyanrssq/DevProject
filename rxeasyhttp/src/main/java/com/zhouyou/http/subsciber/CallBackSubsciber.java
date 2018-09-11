/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhouyou.http.subsciber;

import android.content.Context;

import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.callback.ProgressDialogCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.loadview.ILoad;
import com.zhouyou.http.loadview.LoadViewHolder;
import com.zhouyou.http.request.BaseRequest;
import com.zhouyou.http.request.PostRequest;

import io.reactivex.annotations.NonNull;


/**
 * <p>描述：带有callBack的回调</p>
 * 主要作用是不需要用户订阅，只要实现callback回调<br>
 * 作者： zhouyou<br>
 * 日期： 2016/12/28 17:10<br>
 * 版本： v2.0<br>
 */
public class CallBackSubsciber<T> extends BaseSubscriber<T> {
    public CallBack<T> mCallBack;
    public PostRequest postRequest;

    public LoadViewHolder loadViewHolder;

    public CallBackSubsciber(Context context, CallBack<T> callBack) {
        super(context);
        mCallBack = callBack;
        if (callBack instanceof ProgressDialogCallBack) {
            ((ProgressDialogCallBack) callBack).subscription(this);
        }
    }

    public CallBackSubsciber(Context context, CallBack<T> callBack, PostRequest postRequest, ILoad load) {
        super(context);
        mCallBack = callBack;
        this.postRequest = postRequest;
        this.loadViewHolder = (LoadViewHolder) load;
        loadViewHolder.setSubscriber(this);
        if (callBack instanceof ProgressDialogCallBack) {
            ((ProgressDialogCallBack) callBack).subscription(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCallBack != null) {
            mCallBack.onStart();
        }
    }
    
    @Override
    public void onError(ApiException e) {
        if (mCallBack != null) {
            mCallBack.onError(e);
            if(loadViewHolder != null) {
                loadViewHolder.showFailed(e.getCode());
            }
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        super.onNext(t);
        if (mCallBack != null) {
            mCallBack.onSuccess(t);
            if(loadViewHolder != null) {
                loadViewHolder.finishLoad();
            }
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if (mCallBack != null) {
            mCallBack.onCompleted();
            if(loadViewHolder != null) {
                loadViewHolder.finishLoad();
            }
        }
    }

    public void retry(){
        if(postRequest != null) {
            postRequest.execute(mCallBack, loadViewHolder);
        }
    }
}
