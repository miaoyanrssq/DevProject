package cn.zgy.news.adapter.cache;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import cn.zgy.multilist.NewsFragment;


/**
* ViewPager.Adapter - 相关缓存处理类，对fragment进行缓存，配合fragment页面做的缓存，实现tab页面的缓存功能{@link NewsFragment#onCreateView}}
* @author zhengy
* create at 2018/9/10 上午11:12
**/
public class PagerCache<T> {

    protected Map<String, SoftReference<Fragment>> fCaches;

    /**
     * 缓存 tab custom views
     */
    protected SparseArray<SoftReference<View>> tabViewCaches;

    private FactoryPolicy<T> mPolicy;

    private View.OnAttachStateChangeListener mTabViewAttachListener = new View
            .OnAttachStateChangeListener() {

        @Override
        public void onViewAttachedToWindow(View v) {
            tabViewCaches.remove(v.hashCode());
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            tabViewCaches.put(v.hashCode(), new SoftReference<>(v));
        }
    };

    public PagerCache(FactoryPolicy<T> policy) {
        this.mPolicy = policy;
        fCaches = new HashMap<>();
        tabViewCaches = new SparseArray<>();
    }


    public Fragment getFragment(T data) {
        String key = mPolicy.toKey(data);
        Fragment f = getCacheFragment(key);

        if (f == null) {
            putCacheFragment(key, f = mPolicy.newFragment(data));
        }
        return f;
    }

    /**
     * 获取缓存Fragment
     *
     * @param key a key
     * @return Fragment
     */
    protected Fragment getCacheFragment(String key) {
        SoftReference<Fragment> soft = fCaches.get(key);
        return soft != null ? soft.get() : null;
    }

    /**
     * 缓存Fragment
     *
     * @param key
     * @param fragment
     */
    protected void putCacheFragment(String key, Fragment fragment) {
        fCaches.put(key, new SoftReference<>(fragment));
    }

    public View obtainTabViewCache() {
        View v = null;
        while (tabViewCaches.size() > 0) {
            int key = tabViewCaches.keyAt(0);
            SoftReference<View> soft = tabViewCaches.get(key);
            tabViewCaches.remove(key);
            if (soft != null && (v = soft.get()) != null) {
                if (mTabViewAttachListener != null)
                    v.removeOnAttachStateChangeListener(mTabViewAttachListener);
                return v;
            }
        }
        return v;
    }

    public void bindAttach(@NonNull View v) {
        v.addOnAttachStateChangeListener(mTabViewAttachListener);
    }

    /**
     * 创建实例 - 策略模式
     *
     * @author a_liYa
     * @date 2017/10/13 下午2:48.
     */
    public interface FactoryPolicy<T> {

        /**
         * 创建Fragment
         *
         * @param t data
         * @return Fragment
         */
        Fragment newFragment(T t);

        /**
         * key used to save fragment
         *
         * @param t data
         * @return key string
         */
        String toKey(T t);

    }

}
