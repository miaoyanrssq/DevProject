package cn.zgy.base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.zgy.base.manager.AppManager;

/**
 * UI工具类
 *
 * @author a_liYa
 * @date 2016-3-1 下午4:16:10
 */
public class UIUtils {

    private UIUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static boolean debuggable = false;

    private static Application sApp;
    /**
     * 获取到主线程的handler
     */
    private static Handler sMainHandler;

    /**
     * JS调用的缓存变量
     */
    private static HashMap<String, String> mapForJs;

    /**
     * 获取Application
     *
     * @return
     */
    public static Application getApp() {
        return sApp;
    }

    /**
     * 必须要在application中初始化
     *
     * @param app 这个方法只能在MainBuild里面调用  重要的事情只说一遍
     */
    public static void init(Application app) {
        sApp = app;
        if (sMainHandler == null) {
            sMainHandler = new Handler();
        }

        try {
            debuggable = (app.getPackageManager()
                    .getPackageInfo(app.getPackageName(), PackageManager.GET_ACTIVITIES)
                    .applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            debuggable = false;
        }
    }

    public static boolean isDebuggable() {
        return debuggable;
    }

    /**
     * JS存储数据
     *
     * @return
     */
    public static HashMap<String, String> getMapForJs() {
        if (null == mapForJs) {
            mapForJs = new HashMap<>();
        }
        return mapForJs;
    }

    public static Context getContext() {
        return sApp;
    }

    public static Activity getActivity() {
        return AppManager.get().currentActivity();
    }

    /**
     * 获取主线程Id
     */
    public static long getMainThreadId() {
        return sApp.getMainLooper().getThread().getId();
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getHandler() {
        return sMainHandler;
    }

    /**
     * 在主线程执行runnable
     * 确定当前为子线程时调用此方法
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /**
     * 从主线程looper里面移除runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    public static int getScreenW() {
        WindowManager windowManager = (WindowManager)
                getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        return Math.min(point.x, point.y);
    }

    public static int getScreenH() {
        WindowManager windowManager = (WindowManager)
                getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        return Math.max(point.x, point.y);
    }

    /**
     * dip转换px
     */
    public static int dip2px(float dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换dip
     */
    public static float px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return px / scale;
    }

    /**
     * sp转px
     */
    public static int sp2px(float spVal) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getContext().getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     * px转sp
     */
    public static float px2sp(float pxVal) {
        return (pxVal / getContext().getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 获取屏幕的宽度：单位px
     */
    public static int getScreenWidthPixels(Activity activity) {

        DisplayMetrics metric = new DisplayMetrics(); // 定义DisplayMetrics 对象
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric); // 取得窗口属性
        return metric.widthPixels;
    }

    /**
     * 获取屏幕的高度：单位px
     */
    public static int getScreenHeightPixels(Activity aty) {

        DisplayMetrics metric = new DisplayMetrics(); // 定义DisplayMetrics 对象
        aty.getWindowManager().getDefaultDisplay().getMetrics(metric); // 取得窗口属性
        return metric.heightPixels;
    }

    /**
     * 获取指定id的View
     *
     * @param view 父View
     * @param id   id
     * @param <T>  返回view的类型
     * @return
     */
    public static <T extends View> T findById(View view, int id) {
        return (T) view.findViewById(id);
    }


    /**
     * 解析resId为视图 (过滤了resId视图自带layoutparam参数)
     *
     * @param resId 资源Id
     * @return 返回解析后的视图
     */
    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null);
    }

    /**
     * 解析resId至视图(保持resid自带的layoutparam参数)
     *
     * @param resId        资源Id
     * @param root         根目录（已经依附在window上的父视图）
     * @param attachToRoot 是否追加到root视图上
     * @return 返回解析后的视图
     */
    public static View inflate(int resId, ViewGroup root, boolean attachToRoot) {
        if (root != null && root.getContext() != null) {
            return LayoutInflater.from(root.getContext()).inflate(resId, root, attachToRoot);
        }
        return LayoutInflater.from(getContext()).inflate(resId, root, attachToRoot);
    }

    /**
     * 获取资源
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取文字
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取App名称
     */
    public static String getAppName() {
        PackageManager pm;
        String appName = "";
        if (getContext() != null) {
            pm = getContext().getPackageManager();
            appName = getContext().getApplicationInfo().loadLabel(pm)
                    .toString();
        }

        return appName;
    }

    /**
     * 判断当前的线程是不是在主线程
     */
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    /**
     * 在主线程中执行runnable
     * 不确定当前线程是子线程还是主线程时调用
     * 若当前线程为子线程 see (@post(Runnable runnable))
     */
    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    /**
     * 切换软键盘(若正在显示, 则隐藏, 否则反之)
     */
    public static void toggleSoftInput() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示软键盘(强制)
     * 注:此时软件盘不能自动隐藏了,hideSoftInput(view)也不行
     *
     * @param view 目前聚焦的视图，它希望接收软键盘输入
     */
    public static void showSoftInput(View view) {
        view.requestFocus(); // 获取焦点
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘(强制)
     *
     * @param view 与获取焦点控件在同一个视图上的任意控件;eg:同一个Activity、同一个Dialog、同一个PopWin
     */
    public static void hideSoftInput(View view) {
        view.clearFocus(); // 清除焦点
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
    }

    /**
     * TextView设置不同样式
     *
     * @param context   上下文
     * @param tv        TectView
     * @param text      文本
     * @param style1    样式1
     * @param style2    样式2
     * @param diffStart 样式2的开始位置
     */
    public static void setTextDiffStyle(Context context, TextView tv, String text, int style1,
                                        int style2, int diffStart) {

        SpannableString styledText = new SpannableString(text);

        styledText.setSpan(new TextAppearanceSpan(getContext(), style1), 0, diffStart,

                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(context, style2), diffStart, styledText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(styledText, TextView.BufferType.SPANNABLE);
    }

    /**
     * 设置View以及子view的状态（可用/不可用）<br/>
     * 广度遍历
     *
     * @param view
     * @param enabled
     */
    public static void setViewEnabled(View view, boolean enabled) {
        if (view == null) {
            return;
        }
        Queue<View> queue = new LinkedList<View>();
        queue.add(view);
        while (true) {
            if (!queue.isEmpty()) {
                View poll = queue.poll();
                if (poll != null) {
                    poll.setEnabled(enabled);
                    if (poll instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) poll;
                        for (int i = 0; i < vg.getChildCount(); i++) {
                            View child = vg.getChildAt(i);
                            queue.add(child);
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean setFlymeStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null); // 获取的静态字段
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean setMIUIStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    static String sRatio = null;

    public static String getAspectRatio() {
        if (sRatio != null) {
            return sRatio;
        }
        int height = UIUtils.getScreenH();
        int width = UIUtils.getScreenW();
        int ratio = divisor(height, width);
        sRatio = height / ratio + "," + width / ratio;
        return sRatio;
    }

    /**
     * 求最大公约数
     *
     * @param m
     * @param n
     * @return
     */
    static int divisor(int m, int n) {
        if (m % n == 0) {
            return n;
        } else {
            return divisor(n, m % n);
        }
    }

    /**
     * drawable转birmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static boolean isTopActivity(Activity activity){
        return activity!=null && isTopActivity(activity, activity.getClass().getName());
    }

    public static boolean isTopActivity(Context context, String activityName){
        return isForeground(context, activityName);
    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
