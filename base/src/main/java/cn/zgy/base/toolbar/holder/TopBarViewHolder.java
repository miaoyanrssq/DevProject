package cn.zgy.base.toolbar.holder;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.zgy.base.toolbar.BackDownCallback;


/**
 * topBar处理基类
 *
 * @author a_liYa
 * @date 2017/7/25 19:25.
 */
public abstract class TopBarViewHolder {

    View rootView;

    Activity activity;

    public TopBarViewHolder(ViewGroup view, Activity activity) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(getLayoutId(), view, false);
    }

    protected abstract int getLayoutId();

    public <T extends View> T findViewById(@IdRes int id) {
        return (T) rootView.findViewById(id);
    }

    public View getView() {
        return rootView;
    }

    protected void setBackOnClickListener(@IdRes int id) {
        setBackOnClickListener(findViewById(id));
    }

    protected void setBackOnClickListener(@NonNull View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean callback = false;
                if (activity instanceof BackDownCallback) {
                    callback = ((BackDownCallback) activity).onBackDown(v);
                }
                if (!callback && activity != null) {
                    try {
                        activity.onBackPressed();
                    } catch (Exception e) {
                        // @see android.app.FragmentManagerImpl#checkStateLoss()
                    }
                }
            }
        });
    }

}
