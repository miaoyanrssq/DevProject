package cn.zgy.nav;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixinke on 2017/8/9.
 */
public class Nav {
    private Context mContext;
    private Intent mIntent;

    private Fragment mFragment;
    private Bundle mBundle;
    private boolean isNeedLogin = false;
    private String mAction;
    private List<String> mCategories;
    private static List<Interceptor> sInterceptors = new ArrayList<>();

    private Nav(Context context) {
        mContext = context;
        mIntent = new Intent();
        mIntent.setAction(Intent.ACTION_VIEW);
    }

    private Nav(Fragment fragment) {
        this(fragment.getContext());
        mFragment = fragment;
    }

    public static Nav with(Context context) {
        return new Nav(context);
    }

    public static Nav with(Fragment fragment) {
        return new Nav(fragment);
    }

    public static void addInterceptor(Interceptor interceptor) {
        sInterceptors.add(interceptor);
    }

    public boolean to(String url) {
        return to(url, -1);
    }

    public boolean to(String url, int requestCode) {
        if (TextUtils.isEmpty(url)) {
            if (BuildConfig.DEBUG) {
                throw new NullPointerException("url");
            }
            return false;
        }
        return to(Uri.parse(url), requestCode);
    }

    public boolean toPath(String path) {
        return to(handlePath(path));
    }

    public boolean toPath(String path, int requestCode) {
        return to(handlePath(path), requestCode);
    }

    @Nullable
    private String handlePath(String path) {
        if (!TextUtils.isEmpty(path) && path.startsWith("/")) {
            path = mContext.getString(R.string.data_scheme)
                    + "://"
                    + mContext.getString(R.string.data_host)
                    + path;
        }
        return path;
    }

    public Nav setExtras(Bundle bundle) {
        mBundle = bundle;
        return this;
    }

    public Nav needLogin(boolean isNeedLogin) {
        this.isNeedLogin = isNeedLogin;
        return this;
    }

    public Nav setAction(String action) {
        mAction = action;
        return this;
    }

    public Nav addCategory(String category) {
        if (mCategories == null) {
            mCategories = new ArrayList<>();
        }
        mCategories.add(category);
        return this;
    }

    public Nav removeCategory(String category) {
        if (mCategories != null && mCategories.size() > 0) {
            mCategories.remove(category);
        }
        return this;
    }

    public boolean to(Uri uri, int requestCode) {

        if (uri == null) {
            if (BuildConfig.DEBUG) {
                throw new NullPointerException("uri");
            }
            return false;
        }

        if (!TextUtils.isEmpty(mAction)) {
            mIntent.setAction(mAction);
        }

        if (mCategories != null && mCategories.size() > 0) {
            for (int i = 0; i < mCategories.size(); i++) {
                mIntent.addCategory(mCategories.get(i));
            }
        }

        if (mBundle != null) {
            mIntent.putExtras(mBundle);
        }

        if (sInterceptors != null) {
            for (int i = 0; i < sInterceptors.size(); i++) {
                uri = sInterceptors.get(i).before(uri);
            }
        }

        if (isNeedLogin) {
            String reset = uri.toString();
            /**
            try {
                uri = Uri.parse("http://www.8531.cn/com.login.html").buildUpon()
                        .appendQueryParameter
                                ("reset", reset).build();
            } catch (Exception e) {
                return false;
            }
             */
        }

        mIntent.setData(uri.normalizeScheme());

        LinkControl linkControl = new LinkControl(uri);
        if (!linkControl.hasParams()) {
            return parseIntent(requestCode);
        } else {
            if (linkControl.isOuterBrowserOpen()) { // 外部浏览器打开
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startIntent(intent, requestCode);
                return true;
            } else { // 内部开启
                if (linkControl.isOpenAlert()) { // 弹框提醒
                    showDialog(requestCode);
                    return false;
                } else {
                    return parseIntent(requestCode);
                }
            }
        }
    }

    private void showDialog(final int requestCode) {
        CommentDialog zbDiaog = new CommentDialog(mContext);
        zbDiaog.setBuilder(new CommentDialog.Builder()
                .setTitle("跳转提醒")
                .setMessage("将要跳转到第三方应用站点")
                .setRightText("跳转")
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.btn_right) {
                           parseIntent(requestCode);
                        }
                    }
                }));
        zbDiaog.show();
    }

    private boolean parseIntent(int requestCode) {
        List<ResolveInfo> resolveInfos = mContext.getPackageManager().queryIntentActivities
                (mIntent, PackageManager.MATCH_ALL);
        try {
            if (resolveInfos == null || resolveInfos.size() == 0) {
                throw new ActivityNotFoundException("Not match any Activity:" + mIntent.toString());
            } else {
                ActivityInfo activityInfo = resolveInfos.get(0).activityInfo;
                //优先匹配应用自己的Activity
                for (int i = 0; i < resolveInfos.size(); i++) {
                    if (resolveInfos.get(i).activityInfo.packageName.equals(mContext.getPackageName())) {
                        activityInfo = resolveInfos.get(i).activityInfo;
                        break;
                    }
                }
                mIntent.setClassName(activityInfo.packageName, activityInfo.name);
            }
            startIntent(mIntent, requestCode);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startIntent(Intent intent, int requestCode) {
        if (mFragment != null) {
            mFragment.startActivityForResult(intent, requestCode);
        } else if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent, requestCode);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    private boolean isLogin(Context context) {
        return false;
    }

    public interface Interceptor {
        Uri before(Uri uri);
    }

}
