package cn.zgy.base.permission;

import java.util.List;

/**
 * 单个权限申请CallBack
 *
 * @author a_liYa
 * @date 2016/9/19 15:20.
 * @see IPermissionCallBack
 */
public abstract class AbsPermSingleCallBack implements IPermissionCallBack {

    @Override
    public void onElse(List<String> deniedPerms, List<String> neverAskPerms) {

    }
}
