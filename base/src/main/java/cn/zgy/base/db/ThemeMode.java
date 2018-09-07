package cn.zgy.base.db;

import android.app.Activity;
import android.support.annotation.StyleRes;

import com.aliya.uimode.UiModeManager;

/**
 * 夜间模式 对应主题 配置
 *
 * @author a_liYa
 * @date 2017/6/29 09:28.
 */
public class ThemeMode {

    private boolean isNightMode;

    private int mDayTheme = NO_THEME;
    private int mNightTheme = NO_THEME;

    private static final int NO_THEME = -1;
    private static int default_theme = NO_THEME;
    public static final String KEY_UI_MODE = "ui_mode";

    private static ThemeMode sInstance;

    private ThemeMode() {
//        Context context = UIUtils.getContext();
//        if (context != null) {
//            PackageManager manager = context.getPackageManager();
//            try {
//                ApplicationInfo appInfo = manager.getApplicationInfo(context.getPackageName(),
//                        PackageManager.GET_META_DATA);
//                default_theme = appInfo.theme;
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        isNightMode = SPHelper.get().get(KEY_UI_MODE, false);
    }

    public static final void initTheme(@StyleRes int dayTheme, @StyleRes int nightTheme) {
        _get().setDayTheme(dayTheme).setNightTheme(nightTheme);
    }

    private static ThemeMode _get() {
        if (sInstance == null) {
            synchronized (ThemeMode.class) {
                if (sInstance == null) {
                    sInstance = new ThemeMode();
                }
            }
        }
        return sInstance;
    }

    public int getDayTheme() {
        return mDayTheme;
    }

    public ThemeMode setDayTheme(int dayTheme) {
        mDayTheme = dayTheme;
        return this;
    }

    public int getNightTheme() {
        return mNightTheme;
    }

    public ThemeMode setNightTheme(int nightTheme) {
        mNightTheme = nightTheme;
        return this;
    }

    private final int getCurrentTheme() {
        return isNightMode ? mNightTheme : mDayTheme;
    }

    public static final void setUiMode(boolean isNightMode) {
        if (_get().isNightMode != isNightMode) {
            _get().isNightMode = isNightMode;
            SPHelper.get().put(KEY_UI_MODE, isNightMode).commit();
            int theme = isNightMode ? _get().getNightTheme() : _get().getDayTheme();
            if (NO_THEME != theme) {
                UiModeManager.setTheme(theme);
            }
        }
    }

    public static final boolean isNightMode() {
        return _get().isNightMode;
    }

    public static final void fitActivityTheme(Activity activity) {
        if (activity == null) return;

        int currTheme = _get().getCurrentTheme();
        if (NO_THEME != currTheme && default_theme != currTheme) {
            activity.setTheme(currTheme);
        }
    }
}
