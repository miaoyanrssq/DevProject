package cn.zgy.base.permission;

import java.util.List;

/**
 * IPermissionCallBack的实现
 *
 * @author a_liYa
 * @date 2016/9/19 15:20.
 * @see IPermissionCallBack
 */
public abstract class AbsPermCallBack implements IPermissionCallBack {

    @Override
    public void onGranted(boolean isAlreadyDef) {

    }

    @Override
    public void onDenied(List<String> neverAskPerms) {

    }

    @Override
    public void onElse(List<String> deniedPerms, List<String> neverAskPerms) {

    }
}
