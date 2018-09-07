package cn.zgy.multilist.json;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.zgy.multilist.bean.TypeItem;

public class TypeParser {

    public TypeParser() {
    }

    static final Gson GSON = new GsonBuilder().registerTypeAdapter(TypeItem.class, new TypeDeserializer()).create();

    public static List<TypeItem> fromJson(@NonNull String json){
        return GSON.fromJson(json, new TypeToken<ArrayList<TypeItem>>(){}.getType());
    }
}
