package cn.zgy.multilist.bean;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public abstract class TypeItem {

    @SerializedName("type")
    public final @NonNull String type;

    protected TypeItem(@NonNull String type) {
        this.type = type;
    }
}
