package cn.zgy.multitype;

import android.support.annotation.NonNull;

import java.util.List;

/**
* json解析反序列化接口，具体使用参考{@link TypeDeserializer}在multilist包中
* @author zhengy
* create at 2018/9/19 上午10:23
**/
public interface Parser<T> {

    List<T> fromJson(@NonNull String json);
}
