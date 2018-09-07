package cn.zgy.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.zgy.base.toolbar.ToolBarActivity;

/**
 * 生命周期可监听回调 - Activity
 *
 * @author a_liYa
 * @date 2017/12/2 13:11.
 */
public abstract class LifecycleActivity extends StatusBarActivity {

    private List<ActivityCallbacks> mActivityCallbacks;
    private List<ActivityCallbacks> mBackupCallbacks;

    public void registerActivityCallbacks(ActivityCallbacks callback) {
        if (callback != null) {
            ensureNunNull();
            mActivityCallbacks.add(callback);
        }
    }

    public void unregisterActivityCallbacks(ActivityCallbacks callback) {
        if (mActivityCallbacks != null) {
            mActivityCallbacks.remove(callback);
        }
    }


    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivityCreated(this, savedInstanceState);
            }
            mBackupCallbacks.clear();
        }
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivityStarted(this);
            }
            mBackupCallbacks.clear();
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivityResumed(this);
            }
            mBackupCallbacks.clear();
        }
    }

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivityPaused(this);
            }
            mBackupCallbacks.clear();
        }
    }

    @CallSuper
    @Override
    protected void onStop() {
        super.onStop();
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivityStopped(this);
            }
            mBackupCallbacks.clear();
        }
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivitySaveInstanceState(this, outState);
            }
            mBackupCallbacks.clear();
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivityDestroyed(this);
            }
            mBackupCallbacks.clear();
        }
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mActivityCallbacks != null) {
            ensureBackupNonNull();
            mBackupCallbacks.clear();
            mBackupCallbacks.addAll(mActivityCallbacks);
            for (ActivityCallbacks callback : mBackupCallbacks) {
                callback.onActivityResulted(this, requestCode, resultCode, data);
            }
            mBackupCallbacks.clear();
        }
    }

    /**
     * 确保 {@link #mActivityCallbacks} 不为null
     */
    private void ensureNunNull() {
        if (mActivityCallbacks == null) {
            synchronized (LifecycleActivity.class) {
                if (mActivityCallbacks == null) {
                    mActivityCallbacks =
                            Collections.synchronizedList(new ArrayList<ActivityCallbacks>());
                }
            }
        }
    }

    /**
     * 确保 {@link #mBackupCallbacks} 不为null
     */
    private void ensureBackupNonNull() {
        if (mBackupCallbacks == null) {
            synchronized (LifecycleActivity.class) {
                if (mBackupCallbacks == null) {
                    mBackupCallbacks = new ArrayList<>();
                }
            }
        }
    }

    /**
     * Activity 回调方法相关的接口
     *
     * @author a_liYa
     * @date 2017/12/2 下午1:33.
     */
    public interface ActivityCallbacks {

        void onActivityCreated(LifecycleActivity activity, Bundle savedInstanceState);

        void onActivityStarted(LifecycleActivity activity);

        void onActivityResumed(LifecycleActivity activity);

        void onActivityPaused(LifecycleActivity activity);

        void onActivityStopped(LifecycleActivity activity);

        void onActivitySaveInstanceState(LifecycleActivity activity, Bundle outState);

        void onActivityDestroyed(LifecycleActivity activity);

        void onActivityResulted(LifecycleActivity activity, int requestCode, int resultCode, Intent data);

    }

    // 方法的实现
    public static class AbsActivityCallbacks implements ActivityCallbacks {

        @Override
        public void onActivityCreated(LifecycleActivity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(LifecycleActivity activity) {

        }

        @Override
        public void onActivityResumed(LifecycleActivity activity) {

        }

        @Override
        public void onActivityPaused(LifecycleActivity activity) {

        }

        @Override
        public void onActivityStopped(LifecycleActivity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(LifecycleActivity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(LifecycleActivity activity) {

        }

        @Override
        public void onActivityResulted(LifecycleActivity activity, int requestCode, int resultCode, Intent data) {

        }

    }

}
