package cn.zgy.base.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.zgy.base.UIUtils;
import cn.zgy.base.db.SPHelper;

/**
 * 动态权限申请工具类
 *
 * @author a_liYa
 * @date 16/7/21.
 */
public class PermissionManager {
    /**
     * 被拒绝权限（不再询问）
     */
    public static final String PERMISSION_NEVER_ASK = "permission_never_ask_sets";

    private volatile static PermissionManager mInstance;

    /**
     * 获取实例
     * <p/>
     * 单例 - 懒汉式
     *
     * @return PermissionManager
     */
    public static PermissionManager get() {
        if (mInstance == null) {
            synchronized (PermissionManager.class) {
                if (mInstance == null) {
                    mInstance = new PermissionManager();
                }
            }
        }
        return mInstance;
    }

    private final SparseArray<OpEntity> mRequestCaches;
    private final Set<String> mManifestPerms;

    // 私有构造方法
    private PermissionManager() {
        mRequestCaches = new SparseArray<>();
        mManifestPerms = getManifestPermissions();
    }


    /**
     * 动态申请权限
     * <p/>
     * 注：所申请权限必须在Manifest中静态注册，否则可能崩溃
     *
     * @param permissionOp
     * @param callback     回调
     * @param permissions  权限数组
     * @return true：默认之前已经全部授权
     */
    public synchronized boolean request(IPermissionOperate permissionOp,
                                        IPermissionCallBack
                                                callback, Permission... permissions) {
        if (permissions == null) // 没有申请的权限 return true
            return true;

        if (permissionOp == null || callback == null) {
            return false;
        }

        OpEntity opEntity = new OpEntity(callback);
        // 6.0版本以上
        if (Build.VERSION.SDK_INT >= 23) {
            for (Permission permission : permissions) { // 权限分类：已授权、已拒绝（不再询问）、待申请
                //检查申请的权限是否在 AndroidManifest.xml 中
                if (mManifestPerms.contains(permission.getPermission())) {
                    //判断权限是否被授予
                    if (selfPermissionGranted(permission.getPermission())) {
                        opEntity.addGrantedPerm(permission.getPermission());
                    } else {
                        Set<String> neverAskPerms = SPHelper.get().get
                                (PERMISSION_NEVER_ASK, Collections.EMPTY_SET);
                        if (neverAskPerms.contains(permission.getPermission())) {
                            //  没有被用户禁止弹窗提示
                            opEntity.addNeverAskPerms(permission.getPermission());
                        } else {
                            opEntity.addWaitPerms(permission.getPermission());
                        }
                    }
                } else {
                    opEntity.addNeverAskPerms(permission.getPermission());
                }
            }

            // 处理分类权限
            if (opEntity.getGrantedPerms().size() == permissions.length) {
                callback.onGranted(true);
                return true;
            } else {
                if (opEntity.getWaitPerms().isEmpty()) {
                    if (opEntity.getGrantedPerms().isEmpty()) { // 全部是已拒绝（不再询问）
                        callback.onDenied(opEntity.getNeverAskPerms());
                    } else { // 其他情况：部分拒绝、部分已授权
                        callback.onElse(opEntity.getDeniedPerms(), opEntity.getNeverAskPerms());
                    }
                } else {
                    mRequestCaches.put(opEntity.getRequestCode(), opEntity);
                    permissionOp.exeRequestPermissions(opEntity.getWaitPermsArray(), opEntity
                            .getRequestCode());
                    opEntity.getWaitPerms().clear(); // 清空待申请权限
                }
            }
        } else {
            callback.onGranted(true);
            return true;
        }

        return false;
    }

    /**
     * 权限申请结果处理
     *
     * @param requestCode  请求码
     * @param permissions  申请权限集合
     * @param grantResults 申请结果集合
     */
    public synchronized void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults, IPermissionOperate showRationale) {

        OpEntity opEntity = mRequestCaches.get(requestCode);
        if (opEntity != null) {
            mRequestCaches.remove(requestCode);

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {  //权限被授予
                    opEntity.addGrantedPerm(permissions[i]);
                } else {  //权限被拒绝 1、拒绝 2、拒绝并不再询问
                    if (!showRationale.exeShouldShowRequestPermissionRationale((permissions[i]))) {
                        SPHelper spHelper = SPHelper.get();
                        Set<String> neverAskPerms = spHelper.get(PERMISSION_NEVER_ASK,
                                new HashSet<String>());
                        neverAskPerms.add(permissions[i]);
                        spHelper.put(PERMISSION_NEVER_ASK, neverAskPerms);
                        opEntity.addNeverAskPerms(permissions[i]);
                    } else {
                        opEntity.addDeniedPerm(permissions[i]);
                    }
                }
            }

            IPermissionCallBack callBack = opEntity.getCallBack();
            if (callBack == null) return;

            if (opEntity.getDeniedPerms().isEmpty()) {
                callBack.onGranted(false);
            } else if (opEntity.getGrantedPerms().isEmpty()) {
                callBack.onDenied(opEntity.getNeverAskPerms());
            } else {
                callBack.onElse(opEntity.getDeniedPerms(), opEntity.getNeverAskPerms());
            }
        }
    }

    /**
     * 检查权限是否已经授权
     *
     * @param permission
     * @return true: 已经授权
     */
    private boolean selfPermissionGranted(String permission) {
        boolean result = true;
        int targetSdkVersion = 0;
        Context ctx = UIUtils.getContext();
        try {
            final PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                result = ContextCompat.checkSelfPermission(ctx, permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else { // 若targetSdkVersion<23且运行在M版本应用有上面代码检查会一直返回PERMISSION_GRANTED
                result = PermissionChecker.checkSelfPermission(ctx, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    /**
     * 手动开启权限
     *
     * @param context
     * @param message
     */
    public static void showAdvice(Context context, String message) {
        showDialog(context, message);
    }

    /**
     * 手动开启权限
     *
     * @param context
     */
    public static void showAdvice(Context context) {
        showDialog(context, "需要开启权限才能使用此功能");
    }

    private static void showDialog(final Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intentSetting.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intentSetting);
            }
        });
        builder.show();
    }

    /**
     * 获取Manifest静态注册的权限
     */
    private synchronized Set<String> getManifestPermissions() {

        Set<String> manifestPers = null;
        PackageInfo packageInfo = null;
        try {
            packageInfo = UIUtils.getContext().getPackageManager().getPackageInfo(
                    UIUtils.getContext().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null) {
                manifestPers = new HashSet<>(permissions.length);
                for (String perm : permissions) {
                    manifestPers.add(perm);
                }
            }
        }

        if (manifestPers == null)
            manifestPers = new HashSet<>(1);

        return manifestPers;
    }

    /**
     * 用来存储权限相关数据
     *
     * @author a_liYa
     * @date 2016/9/18 11:08.
     */
    private static class OpEntity implements Serializable {
        /**
         * 授权权限集合
         */
        private List<String> mGrantedPerms;
        /**
         * 拒绝权限集合 包括：不再询问权限
         */
        private List<String> mDeniedPerms;
        /**
         * 不再询问权限集合
         */
        private List<String> mNeverAskPerms;
        /**
         * 待申请权限集合
         */
        private List<String> mWaitPerms;

        private IPermissionCallBack mCallBack;

        private int requestCode;

        private static int count = 0;

        public OpEntity(IPermissionCallBack callBack) {
            mCallBack = callBack;
            mGrantedPerms = new ArrayList<>();
            mDeniedPerms = new ArrayList<>();
            mNeverAskPerms = new ArrayList<>();
            mWaitPerms = new ArrayList<>();
            requestCode = count++;
            if (count > 0x0000ffff) {
                count = 0;
            }
        }

        public IPermissionCallBack getCallBack() {
            return mCallBack;
        }

        public void setCallBack(IPermissionCallBack callBack) {
            mCallBack = callBack;
        }

        public List<String> getGrantedPerms() {
            return mGrantedPerms;
        }

        public void setGrantedPerms(List<String> grantedPerms) {
            mGrantedPerms = grantedPerms;
        }

        public void addGrantedPerm(String grantedPerm) {
            mGrantedPerms.add(grantedPerm);
        }

        public List<String> getDeniedPerms() {
            return mDeniedPerms;
        }

        public void addDeniedPerm(String deniedPerm) {
            mDeniedPerms.add(deniedPerm);
        }

        public void setDeniedPerms(List<String> deniedPerms) {
            mDeniedPerms = deniedPerms;
        }

        public List<String> getNeverAskPerms() {
            return mNeverAskPerms;
        }

        public void addNeverAskPerms(String neverAskPerm) {
            mNeverAskPerms.add(neverAskPerm);
            mDeniedPerms.add(neverAskPerm);
        }

        public List<String> getWaitPerms() {
            return mWaitPerms;
        }

        public void addWaitPerms(String requestPerm) {
            mWaitPerms.add(requestPerm);
        }

        public String[] getWaitPermsArray() {
            return mWaitPerms.toArray(new String[mWaitPerms.size()]);
        }

        public int getRequestCode() {
            return requestCode;
        }
    }
}
