package cn.zgy.base.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.pm.PackageManager.GET_SIGNATURES;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/**
 * App工具类 ：安装、卸载、是否运行等
 *
 * @author a_liYa
 * @date 2016-3-30 下午11:51:09
 */
public class AppUtils {
    private static final String TAG = "AppUtils";

    // 渠道名称
    private static String sChannelName = "";

    /**
     * 该工具不能被创建实例
     */
    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static String getChannelName() {
        return sChannelName;
    }

    public static void setChannel(String name) {
        sChannelName = name;
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    public static boolean isInstall(String packageName) {

        if (!new File("/data/data/" + packageName).exists()) {
            return false;
        }

        PackageManager pm = UIUtils.getContext().getPackageManager();
        List<PackageInfo> list = pm
                .getInstalledPackages(PERMISSION_GRANTED);
        for (PackageInfo p : list) {
            if (packageName != null && packageName.equals(p.packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断应用是否正在运行  
     */
    public static boolean isAppAlive(String packageName) {
        ActivityManager am = (ActivityManager) UIUtils.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : list) {
            String processName = appProcess.processName;
            if (processName != null && processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取MetaData数据
     *
     * @param name key
     * @return value
     */
    public static String getMetaData(String name) {
        ApplicationInfo appInfo = null;
        try {
            Context ctx = UIUtils.getContext();
            appInfo = ctx.getPackageManager()
                    .getApplicationInfo(ctx.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return appInfo == null ? "" : appInfo.metaData.getString(name);
    }

    /**
     * 开启应用
     *
     * @param packageName
     */
    public static void startApp(String packageName) {
        Intent intent = UIUtils.getContext().getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (intent != null) {
            UIUtils.getContext().startActivity(intent);
        } else {
            // 应用没有安装
            Log.i(TAG, "应用没有安装");
        }
    }

    /**
     * 打开App指定的界面
     *
     * @param packageName 应用包名
     * @param clazzName   页面的类名
     */
    public static void startAppActivity(String packageName, String clazzName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(packageName, clazzName));
        UIUtils.getContext().startActivity(intent);
    }

//    /**
//     * 获取手机IMEI号
//     *
//     * @return
//     */
//    public static String getIMEI() {
//        return ((TelephonyManager) UIUtils.getContext().getSystemService(
//                Context.TELEPHONY_SERVICE)).getDeviceId();
//    }

    /**
     * 获取设备标示(会变)
     *
     * @param context
     * @return
     */
    public synchronized static String getTerminalID(Context context) {
        String sID = null;
        if (sID == null) {
            File installation = new File(context.getFilesDir(), "INSTALLATION");
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation)
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation)
            throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    /**
     * 获取应用信息:包名 + 版本名称 +版本号
     *
     * @return 包名 + 版本名称 +版本号
     */
    public static String getAppInfo() {
        try {
            String pkName = UIUtils.getContext().getPackageName();
            String versionName = UIUtils.getContext().getPackageManager()
                    .getPackageInfo(pkName, 0).versionName;
            int versionCode = UIUtils.getContext().getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    /**
     * 获取App版本名称
     *
     * @return
     */
    public static String getVersion() {
        try {
            Context ctx = UIUtils.getContext();
            return ctx.getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0.0";
    }

    /**
     * 获取App版本号
     *
     * @return 版本号
     */
    public static int getVersionCode() {
        int code = 1;
        try {
            PackageInfo packageInfo = UIUtils.getContext().getPackageManager().getPackageInfo(
                    UIUtils.getContext().getPackageName(), 0);
            code = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "获取App版本号失败！");
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取应用签名信息
     *
     * @return
     */
    public static String getSignInfo() {
        String packageName = UIUtils.getContext().getPackageName();
        try {
            PackageInfo packageInfo = UIUtils.getContext().getPackageManager()
                    .getPackageInfo(packageName, GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];

            return parseSignature(sign.toByteArray());

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String parseSignature(byte[] singature) {
        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(singature));
            byte[] buffer = cert.getEncoded();

            // String pubKey = cert.getPublicKey().toString();
            // String signNumber = cert.getSerialNumber().toString();
            // String sigAlgName = cert.getSigAlgName();
            // String subjectDN = cert.getSubjectDN().toString();

            return new String(buffer);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取签名信息 网上搜索
     *
     * @param context
     * @return
     */
    public static String getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm
                .getInstalledPackages(GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                return packageinfo.signatures[0].toCharsString();
            }
        }
        return null;
    }

    /**
     * 安装应用apk
     *
     * @param file
     */
    public static void installApk(File file) {
        Context context = UIUtils.getContext();
        if (!file.exists() || context == null) {
            return;
        }

        final String mime_type = "application/vnd.android.package-archive";
        // 官方提供的API:安装App
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 兼容7.0私有文件权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri, mime_type);
        } else {
            install.setDataAndType(Uri.fromFile(file), mime_type);
        }
        context.startActivity(install);

    }

    /**
     * 卸载应用Apk
     *
     * @param packageName 包名
     */
    public static void unInstallApk(String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + packageName));
        UIUtils.getContext().startActivity(intent);
    }

    /**
     * 检查App是否已安装
     *
     * @param packageName 包名
     * @return true:表示安装 false:表示为安装
     */
    public static boolean checkAppInstalled(String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            UIUtils.getContext()
                    .getPackageManager()
                    .getPackageInfo(packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取当前进程名字
     *
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) UIUtils.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 获取当前Android版本号
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        /*
         * Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH
         * 判断当前Android版本号与目标版本号大小 运用于版本兼容
         */
        int version = 0;
        try {
            version = Integer.valueOf(Build.VERSION.SDK_INT);
        } catch (NumberFormatException e) {
            Log.e("", e.toString());
            UIUtils.getContext().getPackageName();
        }
        return version;
    }

    /**
     * 获取需要保存路径的File
     *
     * @param relativePath 绝对路径
     * @param name
     * @return
     */
    public static File getSaveDir(String relativePath, String name) {

        File dir = Environment.getExternalStoragePublicDirectory(relativePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(dir, name);
    }

    /**
     * 获取CPU版本
     *
     * @return 结果:arm64-v8a | armeabi |　armeabi-v7a | mips | mips64 | x86 |
     * x86_64
     */
    public static String getCPUVersion() {
        try {
            Class<?> localClass = Class.forName("android.os.SystemProperties");
            Method method = localClass.getDeclaredMethod("get",
                    new Class[]{String.class});
            return (String) method.invoke(localClass,
                    new Object[]{"ro.product.cpu.abi"});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        // 详情请百度百科搜索：build.prop
        // # begin build properties （开始设置系统性能）
        // # autogenerated （通过设置形成系统信息）
        // ro.=GRI40 (版本ID)
        // ro.build.=GRJ22 （版本号）
        // ro.build.version.incremental=im_chat_to_bg_normal.buildbot.20110619.060228 （版本增量）
        // ro.build.version.sdk=10 （sdk版本）
        // ro.build.version.codename=REL （版本代号）
        // ro.build.version.release=2.3.4 （Android 2.3.4系统）
        // ro.build.date=Sun Jun 19 06:02:58 UTC 2011 （制作者及制作时间）
        // ro.build.date.utc=0
        // ro.build.type=user (编译模式,如user,userdebug,im_chat_to_bg_normal,test模式)
        // ro.build.user=buildbot (编译账户)
        // ro.build.host=bb1 (编译主机系统)
        // ro.build.tags=test-keys (编译标签)
        // ro.product.model=HTC Wildfire （HTC内部手机代号）
        // ro.product.brand=htc_wwe （手机品牌）
        // ro.product.name=htc_buzz （手机正式名称）
        // ro.product.device=buzz （采用的设备）
        // ro.product.board=buzz （采用的处理器）
        // ro.product.cpu.abi=armeabi-v6j （cpu的版本）
        // ro.product.cpu.abi2=armeabi （cpu的品牌）
        // ro.product.manufacturer=HTC （手机制造商）
        // ro.product.locale.language=zh （手机默认语言）
        // ro.product.locale.region=CN （地区语言）
        // ro.wifi.channels= （WIFI连接的渠道）
        // ro.board.platform=msm7k （主板平台）
    }

    /**
     * 获取系统分割符号
     *
     * @return 默认返回"\n"
     */
    public static String getLineSeparator() {
        return System.getProperty("line.separator", "\n");
    }

    /**
     * 获取CPU核心数
     *
     * @return
     */
    public static int getCpuCore() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 扫描文件或文件夹
     * (加入对sd卡操作的权限)
     *
     * @param filePath 指定文件的路径 为null扫描全部文件
     */
    public static void scanFile(String filePath) {

        Intent intent;
        if (TextUtils.isEmpty(filePath)) { // 4.4以后的系统不允许发送全盘扫描广播
            return;
//            intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
//            intent.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory()));
        } else {
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(filePath)));
        }
        UIUtils.getContext().sendBroadcast(intent);

    }

    /**
     * 获取Assets文件的base64编码
     *
     * @param path 文件路径
     * @return String文本内容
     */
    public static String getAssetsBase64(String path) {
        AssetManager a = UIUtils.getContext().getAssets();
        try {
            InputStream is = a.open(path);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return Base64.encodeToString(buffer, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取Assets文件的文本内容
     *
     * @param path 文件路径
     * @return String文本内容
     */
    public static String getAssetsText(String path) {

        AssetManager a = UIUtils.getContext().getAssets();
        try {
            StringBuffer buffer = new StringBuffer();
            InputStream is = a.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String lineStr;

            while ((lineStr = reader.readLine()) != null) {
                buffer.append(lineStr);
            }

            reader.close();
            is.close();

            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * 判定是否是纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * @param mobiles
     * @return 判断输入的是否为手机号
     */
    public static boolean isMobileNum(String mobiles) {
        //非空、纯数字、长度为11
        if (!TextUtils.isEmpty(mobiles) && isNumeric(mobiles) && mobiles.length() == 11) {
            Pattern p = Pattern.compile("[1]\\d{10}");
            Matcher m = p.matcher(mobiles);
            return m.matches();
        } else {
            return false;
        }

    }


    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx
                                .getPackageName(),
                        PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }


    /**
     * 不需要权限的唯一设备号
     * 返回 pseudo unique ID
     * 支持API 9 以上
     * 每秒1兆的值（一万亿），需要100亿年才有可能发生重复  覆盖率为98.4%，剩下的为在系统9以下
     *
     * @return ID
     */
    public static String getUniquePsuedoID() {
        String m_szDevIDShort = "24" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10)
                + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10)
                + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10)
                + (Build.PRODUCT.length() % 10);
        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception e) {
            serial = "serial"; // some value
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    //4.0版本设备号***************************************************************************

//    /**
//     * 获取设备唯一标识码
//     *
//     * @return
//     */
//    public static String getUniquePsuedoIDOld() {
//        String deviceId = readDeviceId();
//        String serialNo = readSerial();
//        if (!TextUtils.isEmpty(serialNo)) {
//            serialNo += "-SERO";
//        }
//        String uuid = deviceId + serialNo;
//        if (StringUtils.isEmpty(uuid)) {
//            uuid = readUuid() + "-UUID";
//        }
//        String devicesUuid = encodeMD5Hex(uuid);
//        if (isEmulator()) {
//            return "EMU-" + devicesUuid;
//        } else {
//            return "NOR-" + devicesUuid;
//        }
//    }
//
//    public static String encodeMD5Hex(String source) {
//        MessageDigest md;
//        byte[] bt = source.getBytes(Charset.forName("UTF-8"));
//        try {
//            md = MessageDigest.getInstance("MD5");
//            md.update(bt);
//            String s = new String(encodeHex(md.digest(), DIGITS_LOWER));
//            return s;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//
//    private static final char[] DIGITS_LOWER =
//            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
//
//    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
//        final int l = data.length;
//        final char[] out = new char[l << 1];
//        // two characters form the hex value.
//        for (int i = 0, j = 0; i < l; i++) {
//            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
//            out[j++] = toDigits[0x0F & data[i]];
//        }
//        return out;
//    }
//
//    /**
//     * 该唯一设备号方法只针对浙江新闻5.0以下的版本，用于用户兼容
//     *
//     * @return
//     */
//    public static String getUniquePsuedoIDOld1() {
//        String serial = null;
//
//        String m_szDevIDShort = "35" +
//                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
//
//                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
//
//                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
//
//                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
//
//                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
//
//                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
//
//                Build.USER.length() % 10; //13 位
//
//        try {
//            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
//            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
//        } catch (Exception e) {
//            String androidId = Settings.Secure.getString(UIUtils.getApp().getContentResolver(), Settings.Secure.ANDROID_ID);
//            serial = StringUtils.isEmpty(androidId) ? "serial" : androidId; // some value
//        }
//        //使用硬件信息拼凑出来的15位号码
//        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
//    }
//
//    public static String readUuid() {
//        String uuid = SPHelper.get("ZHEJIANG_DAILY_SP").get("UUID_KEY", "");
//        if (StringUtils.isEmpty(uuid)) {
//            uuid = UUID.randomUUID().toString();
//            SPHelper.get("ZHEJIANG_DAILY_SP").put("UUID_KEY", uuid).commit();
//        }
//        return uuid;
//    }
//
//    private static String readDeviceId() {
//        return readDeviceId(UIUtils.getApp());
//    }
//
//    private static boolean isEmulator() {
//        String android_id = Settings.Secure.getString(UIUtils.getApp().getContentResolver(), Settings.Secure.ANDROID_ID);
//        boolean emulator = TextUtils.isEmpty(android_id) || "google_sdk".equals(Build.PRODUCT)
//                || "sdk".equals(Build.PRODUCT);
//        return emulator;
//    }
//
//
//    public static String getDeviceId() {
//        String deviceID = SPHelper.get("ZHEJIANG_DAILY_SP").get("DEVICE_ID", "");
//        if (deviceID.isEmpty()) {
//
//            deviceID = getUniquePsuedoIDOld1();
//            SPHelper.get("ZHEJIANG_DAILY_SP").put("DEVICE_ID", deviceID).commit();
//
//        }
//        return deviceID;
//    }
//
//    // 读取手机的设备ID IMEI=DEVICE_ID
//    private static String readDeviceId(Context context) {
//        String deviceId = getDeviceId();
//        if (StringUtils.isEmpty(deviceId)) {
//            deviceId = readMacAddress(context);
//            if (!StringUtils.isEmpty(deviceId)) {
//                deviceId += "-MAC";
//            }
//        } else {
//            // 添加标记
//            deviceId += "-DEVI";
//        }
//        return deviceId;
//    }
//
//    // 读取手机的MAC地址
//    public static String readMacAddress(Context context) {
//        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        String macAddress = info.getMacAddress();
//        return macAddress;
//    }
//
//    public static String readSerial() {
//        return android.os.Build.SERIAL;
//    }
//
//    public static String readRoSerialno() {
//        String serialnum = "";
//        try {
//            Class<?> c = Class.forName("android.os.SystemProperties");
//            Method get = c.getMethod("get", String.class, String.class);
//            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
//        } catch (Exception ignored) {
//        }
//        return serialnum;
//    }
    //**************************************************************************

    /**
     * @param context
     * @return 判断应用是否在前台
     */
    public static boolean isBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<RunningAppProcessInfo> runningProcesses = am
                    .getRunningAppProcesses();
            for (RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == RunningAppProcessInfo
                        .IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    /**
     * 加亮字符串
     *
     * @param target
     * @param keywords
     * @return
     */
    public static SpannableString highlightString(String target, List<String> keywords) {
        SpannableString spannable = new SpannableString(target);
        if (keywords != null && keywords.size() > 0) {
            ForegroundColorSpan colorSpan = null;
            Pattern pattern;
            Matcher matcher;
            for (int i = 0; i < keywords.size(); i++) {
                pattern = Pattern.compile(keywords.get(i));
                matcher = pattern.matcher(spannable);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    spannable.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannable;
    }

    public static String getDeviceInfo() {
        Map<String, String> deviceInfo = new HashMap<>();
        deviceInfo.put("did", getUniquePsuedoID());
        deviceInfo.put("deviceType", Build.MANUFACTURER); // 手机制造商
        deviceInfo.put("os", "Android");
        deviceInfo.put("appVersion", "TwentyFourHours" + getVersion());
        deviceInfo.put("screenSize", UIUtils.getScreenW() + "*" + UIUtils.getScreenH());

        return JsonUtils.toJsonString(deviceInfo);
    }

    /**
     * 禁止EditText输入空格,密码最多30位
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText, final boolean isPassword) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (isPassword) {
                    if (dend > 30 || source.equals(" ")) {
                        return "";
                    } else {
                        return null;
                    }
                } else {
                    if (source.equals(" ")) {
                        return "";
                    } else {
                        return null;
                    }
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 获取处理器信息
     *
     * @return
     */
    public static String getCPUInfo() {
        try {
            FileReader fileReader = new FileReader("/proc/cpuinfo");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String info;
            while ((info = bufferedReader.readLine()) != null) {
                String[] array = info.split(":");
                if (array[0].trim().equals("Hardware")) {
                    return array[1];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 打开应用设置方法，可以设置权限相关
     *
     * @param context
     */
    public static void openSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

}