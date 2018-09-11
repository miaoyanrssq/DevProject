package com.zhouyou.http.utils;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LruCache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * 点击跟踪器，记录点击时间、位置信息，以及相关计算
 *
 * @author a_liYa
 * @date 2017/5/18 17:40.
 */
public class ClickTracker {

    private static final int CACHE_MAX_SIZE = 15;
    /**
     * 上一次点击的时间
     */
    private static long lastClickTime = 0L;
    /**
     * 防止连续点击的时间间隔
     */
    private static final long INTERVAL = 500L;

    private long timeMillis;

    private static final LruCache<String, SoftReference<ClickTracker>>
            sClickCache = new LruCache<>(CACHE_MAX_SIZE);

    private static ReferenceQueue<ClickTracker> sReferenceQueue = new ReferenceQueue<>();

    protected ClickTracker(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    private static ClickTracker getLastClickTracker(String key) {
        if (key == null)
            return null;

        SoftReference<ClickTracker> lastSoft = sClickCache.get(key);
        ClickTracker clickTracker = null;
        if (lastSoft != null && lastSoft.get() != null) {
            clickTracker = lastSoft.get();
        }
        return clickTracker;
    }

    private static SoftReference<ClickTracker> obtainSoft(ClickTracker clickTracker,
                                                          long timeMillis) {
        if (clickTracker == null) {
            Reference<? extends ClickTracker> poll = sReferenceQueue.poll();
            if (poll != null) {
                clickTracker = poll.get();
            }
        }
        if (clickTracker == null) {
            clickTracker = new ClickTracker(timeMillis);
        } else {
            clickTracker.setTimeMillis(timeMillis);
        }
        return new SoftReference<>(clickTracker, sReferenceQueue);
    }

    /**
     * 判断按钮是否双击
     *
     * @return true : 表示双击
     */
    public static boolean isDoubleClick() {
        return isDoubleClick(INTERVAL);
    }

    /**
     *
     * @param interval
     * @return
     */
    public static boolean isDoubleClick(long interval) {
        long time = SystemClock.uptimeMillis();
        String currClassName = ClickTracker.class.getName();
        String key = null;

        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean matched = false;
            for (StackTraceElement element : stackTrace) {
                if (matched) {
                    if (!currClassName.equals(element.getClassName())) {
                        key = element.getClassName() + element.getLineNumber();
                        break;
                    }
                } else if (currClassName.equals(element.getClassName())) {
                    matched = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(key)) {
            if ((time - lastClickTime) < interval) {
                return true;
            }
            lastClickTime = time;
        } else {
            ClickTracker clickTracker = getLastClickTracker(key);
            if (clickTracker != null) {
                if ((time - clickTracker.getTimeMillis()) < interval) {
                    return true;
                }
            }
            sClickCache.put(key, obtainSoft(clickTracker, time));
        }

        return false;
    }
}
