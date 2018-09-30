package cn.zgy.multilist.bean;

import android.support.annotation.NonNull;

public class Data {

    public static final String TYPE_1 = "text";
    public static final String TYPE_2 = "image";
    public static final String TYPE_3 = "rich";

    @NonNull
    String type;
    String name;
    int resId;

    public Data(@NonNull String type, String name, int resId) {
        this.type = type;
        this.name = name;
        this.resId = resId;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
