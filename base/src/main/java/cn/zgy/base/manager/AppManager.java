package cn.zgy.base.manager;

import android.app.Activity;

import java.util.Stack;

/**
 * 类描述：App的Activity管理类：用于Activity管理和应用程序退出
 * 作者：a_liYa created at 2015/10/4 01:06.
 * <p>
 * 一、方法简介：
 * 1、添加
 * 2、获取当前
 * 3、结束当前
 * 4、结束指定：指定对象或指定类名
 * 5、结束所有
 * 6、退出应用
 * <p>
 * 二、使用描述：
 * 在基类BaseActivity中添加以下代码
 * protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * AppManager.get().addActivity(this);
 * }
 * protected void onDestroy() {
 * super.onDestroy();
 * AppManager.get().finishActivity(this);
 * }
 */
public class AppManager {

    private static Stack<Activity> mActivityStack;
    private volatile static AppManager instance;

    private AppManager() {
    }

    // 单例  懒汉式
    public static AppManager get() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public synchronized void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.push(activity);
    }


    /**
     * 删除Activity从堆栈
     */
    public synchronized void removeActivity(Activity activity) {
        if (activity != null && mActivityStack != null) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 获取栈中所有Activity
     */
    public Stack<Activity> getAllActivity() {
        return mActivityStack;
    }

    public boolean contains(Class<? extends Activity> cls) {
        if (mActivityStack != null)
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    return true;
                }
            }
        return false;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (mActivityStack == null || mActivityStack.isEmpty()) return null;
        return mActivityStack.peek();
    }


    /**
     * 获取指定的Activity
     */
    public Activity getActivity(Class<? extends Activity> cls) {
        if (mActivityStack != null)
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public Activity finishCurrActivity() {
        Activity activity = mActivityStack.pop();
        activity.finish();
        return activity;
    }

    /**
     * 结束指定的Activity
     * 如果存在多个，只删除距离栈底最近的一个元素
     *
     * @param activity 指定Activity实例
     * @return {@code true} 若找到了指定的对象, {@code false} 否则.
     */
    public synchronized boolean finishActivity(Activity activity) {

        if (activity != null) {
            if (mActivityStack.remove(activity)) { // 如果找到并删除，才finish()
                activity.finish();
                return true;
            }
        }
        return false;
    }

    /**
     * 获取栈大小
     *
     * @return
     */
    public synchronized int getCount() {
        if (mActivityStack != null) {
            return mActivityStack.size();
        }
        return 0;
    }

    /**
     * finish all Activity 除了指定的activity
     *
     * @param activity 排除的Activity
     */
    public synchronized void finishActivityExclude(Activity activity) {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i) && mActivityStack.get(i) != activity) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
        mActivityStack.add(activity);
    }

    /**
     * 结束指定类名的Activity
     * 如果存在多个，删除全部
     */
    public void finishActivity(Class<?> cls) {
        // 克隆副本
        Stack<Activity> clone = (Stack<Activity>) mActivityStack.clone();
        for (Activity activity : clone) { // 遍历副本，在主本中删除
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
        clone.clear(); // 销毁副本
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();


            //  以下代码不建议使用,会把后台服务、推送一并杀死;
//            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            activityMgr.killBackgroundProcesses(context.getPackageName());
//            System.exit(0);
//            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
