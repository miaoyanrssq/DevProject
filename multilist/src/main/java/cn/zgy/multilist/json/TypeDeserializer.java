package cn.zgy.multilist.json;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import cn.zgy.multilist.bean.ImageItem;
import cn.zgy.multilist.bean.RichItem;
import cn.zgy.multilist.bean.TextItem;
import cn.zgy.multilist.bean.TypeItem;

/**
* 自定义json解析反序列化类
* @author zhengy
* create at 2018/9/7 下午4:04
**/
public class TypeDeserializer implements JsonDeserializer<TypeItem>{
    @Override
    public TypeItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = TypeParser.GSON;
        JsonObject jsonObject = (JsonObject) json;
        final String type = stringOrEmpty(jsonObject.get("type"));
        TypeItem typeItem = null;

        if(TextItem.TYPE.equals(type)){
            typeItem = gson.fromJson(json, TextItem.class);
        }else if(ImageItem.TYPE.equals(type)){
            typeItem = gson.fromJson(json, ImageItem.class);
        }else if(RichItem.TYPE.equals(type)){
            typeItem = gson.fromJson(json, RichItem.class);
        }
        return typeItem;
    }

    private @NonNull
    String stringOrEmpty(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}
