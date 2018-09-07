package cn.zgy.base.permission;

import java.util.Arrays;

public class PermissionGroup {
    private static String[] CONTACTS = {"android.permission.WRITE_CONTACTS", "android.permission.GET_ACCOUNTS", "android.permission.READ_CONTACTS"};
    private static String[] PHONE = {"android.permission.READ_CALL_LOG", "android.permission.READ_PHONE_STATE", "android.permission.CALL_PHONE", "android.permission.WRITE_CALL_LOG", "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS", "com.android.voicemail.permission.ADD_VOICEMAIL"};
    private static String[] CALENDAR = {"android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR"};
    private static String[] CAMERA = {"android.permission.CAMERA"};
    private static String[] SENSORS = {"android.permission.BODY_SENSORS"};
    private static String[] LOCATION = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    private static String[] STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static String[] MICROPHONE = {"android.permission.RECORD_AUDIO"};
    private static String[] SMS = {"android.permission.READ_SMS", "android.permission.RECEIVE_WAP_PUSH", "android.permission.RECEIVE_MMS", "android.permission.RECEIVE_SMS", "android.permission.SEND_SMS", "android.permission.READ_CELL_BROADCASTS"};

    public static String getGroupName(String permission) {
        if (Arrays.asList(CONTACTS).contains(permission)) {
            return "通讯录权限";
        } else if (Arrays.asList(PHONE).contains(permission)) {
            return "电话权限";
        } else if (Arrays.asList(CALENDAR).contains(permission)) {
            return "日历权限";
        } else if (Arrays.asList(CAMERA).contains(permission)) {
            return "相机权限";
        } else if (Arrays.asList(SENSORS).contains(permission)) {
            return "传感器权限";
        } else if (Arrays.asList(LOCATION).contains(permission)) {
            return "位置信息权限";
        } else if (Arrays.asList(STORAGE).contains(permission)) {
            return "存储空间权限";
        } else if (Arrays.asList(MICROPHONE).contains(permission)) {
            return "麦克风权限";
        } else if (Arrays.asList(SMS).contains(permission)) {
            return "短信权限";
        }
        return null;
    }

}
