package cn.zgy.nav;

import android.net.Uri;
import android.text.TextUtils;


/**
 * link 参数控制定义解析
 *
 * @author a_liYa
 * @date 2017/11/7 15:42.
 */
public class LinkControl {

    private String mParameter;

    public LinkControl(Uri uri) {
        if (uri != null) {
            try {
                mParameter = uri.getQueryParameter(IKey.LINK_CONTROL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否有需要的参数
     *
     * @return true 有
     */
    public boolean hasParams() {
        return !TextUtils.isEmpty(mParameter);
    }

    /**
     * 是否使用外部浏览器打开
     *
     * @return true，是
     */
    public boolean isOuterBrowserOpen() {
        return isZeroCharAt(0, '1');
    }

    /**
     * 是否允许分享
     *
     * @return true，是
     */
    public boolean isAllowShare() {
        return !isZeroCharAt(1, '1');
    }

    /**
     * 是否打开前提醒
     *
     * @return true，是
     */
    public boolean isOpenAlert() {
        return !isZeroCharAt(2, '0');
    }

    /**
     * 是否导入本地js
     *
     * @return true
     */
    public boolean isImportNativeJs() {
        return !isZeroCharAt(3, '0');
    }

    /**
     * 指定字符是否为 '0'
     *
     * @param index       index
     * @param defaultChar default char
     * @return true, 是
     */
    private boolean isZeroCharAt(int index, char defaultChar) {
        return ((TextUtils.isEmpty(mParameter) || index >= mParameter.length())
                ? defaultChar
                : mParameter.charAt(index))
                == '0';
    }

}
