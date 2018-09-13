package cn.zgy.base.db;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.ref.WeakReference;
import java.util.Set;

import cn.zgy.base.utils.UIUtils;


/**
 * 轻量级数据存储助手
 *
 * @author a_liYa
 * @date 2016-3-28 下午9:06:16
 */
public class SPHelper {

    private static WeakReference<SPHelper> mSpHelperWeak;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SPHelper() {
        if (UIUtils.getContext() != null) {
            sharedPreferences = UIUtils.getContext().getSharedPreferences(
                    UIUtils.getAppName(), Activity.MODE_PRIVATE);
        }

    }

    private SPHelper(String spName) {
        if (UIUtils.getContext() != null) {
            sharedPreferences = UIUtils.getContext().getSharedPreferences(spName,
                    Activity.MODE_PRIVATE);
        }

    }

    /**
     * 获取无参实例
     */
    public static SPHelper get() {
        SPHelper spHelper;
        if (mSpHelperWeak != null) {
            spHelper = mSpHelperWeak.get();
            if (spHelper != null) {
                return spHelper;
            }
        }
        spHelper = new SPHelper();
        mSpHelperWeak = new WeakReference<>(spHelper);
        return spHelper;
    }

    /**
     * 获取实例 SharedPreferences的name为spName
     */
    public static SPHelper get(String spName) {
        return new SPHelper(spName);
    }

    public SharedPreferences.Editor getSharedPreferencesEditor() {
        return sharedPreferences.edit();
    }

    public boolean hasKey(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.contains(key);
        }
        return false;
    }

    /**
     * 保存数据 需要手动commit
     *
     * @param key   关键字
     * @param value 值
     * @param <T>   泛型可为：int、float、boolean、String、long、Set<String>
     */
    public <T> SPHelper put(String key, T value) {
        if (editor == null) {
            editor = getSharedPreferencesEditor();
        }
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);

        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);

        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);

        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);

        } else if (value instanceof String) {
            editor.putString(key, (String) value);

        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);

        } else if (value instanceof Serializable) {
            editor.putString(key, writeObjectToString(value));
        }

        return this;
    }

    /**
     * 提交 异步
     */
    public SPHelper commit() {
        commit(true);
        return this;
    }

    /**
     * 提交
     *
     * @param isAsync true 表示异步， false 表示同步
     * @return true 成功， false 失败， 异步永远返回false
     */
    public boolean commit(boolean isAsync) {
        boolean result = false;
        if (editor != null) {
            if (isAsync) {
                editor.apply();
            } else {
                result = editor.commit();
            }
            editor = null;
        }
        return result;
    }

    public String get(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public long get(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public int get(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public float get(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public Set<String> get(String key, Set<String> defValue) {
        return sharedPreferences.getStringSet(key, defValue);
    }

    /**
     * 获取保存的bean对象
     *
     * @param key key
     * @param <T> 获取对象的类型
     * @return 返回对应的对象，不存在或出错时返回
     */
    public <T> T getObject(String key) {
        try {
            if (sharedPreferences.contains(key)) {
                String string = sharedPreferences.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    byte[] stringToBytes = stringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    return (T) is.readObject();
                }
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    /**
     * 清空数据 Key
     */
    public boolean clear() {
        if (editor == null) {
            editor = getSharedPreferencesEditor();
        }
        return editor.clear().commit();
    }

    /**
     * 返回: 对象字节流
     *
     * @param data 保存的对象string
     * @return modified:
     */
    private byte[] stringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;
            char hex_char1 = hexString.charAt(i);
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i);
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48);
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55;
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;
        }
        return retData;
    }


    /**
     * 返回:将序列化对象保存到sharepreferener
     *
     * @param obj
     * @return modified:
     */
    private String writeObjectToString(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(obj);
            return (bytesToHexString(bos.toByteArray()).equals("") ||
                    bytesToHexString(bos.toByteArray()) == null) ? null : bytesToHexString(bos
                    .toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    private String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 清除存储的KEY
     *
     * @param key value对应的key
     */
    public void remove(String key) {
        sharedPreferences.edit().remove(key).commit();
    }

    public interface Key {
        String INITIALIZATION_RESOURCES = "initialization_resources";
    }

}
