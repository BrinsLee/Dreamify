package com.brins.commom.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import com.kugou.common.permission.Action;
import com.kugou.common.permission.KGPermission;
import com.kugou.common.permission.Setting;
import java.util.List;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc
 */
public final class PermissionHandler {

    public static final String[] permissions = (Build.VERSION.SDK_INT >= 29) ? new String[]{
        Manifest.permission.WRITE_EXTERNAL_STORAGE} :
        new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
        };

    public static final String[] contactsPermissions = {
        Manifest.permission.READ_CONTACTS
    };

    public static final String[] storagePermissions = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean hasBasicPermission(Context context) {
        return KGPermission.uCantAskMePermissionState(context, permissions);
    }


    @Deprecated
    public static boolean hasStoragePermission(Context context) {
        return KGPermission.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean hasRecordPermission(Context context) {
        return KGPermission.hasPermissions(context, Manifest.permission.RECORD_AUDIO);
    }

    @Deprecated
    public static boolean hasReadPhoneStatePermission(Context context) {
        return KGPermission.hasPermissions(context, Manifest.permission.READ_PHONE_STATE);
    }

    //todo com.kugou.common.permission.PermissionHandler#showDeniedDialog(android.content.Context, java.lang.String, java.lang.String, java.lang.Runnable, java.lang.Runnable)

    private static void gotoPermissionSetting(Context context, Setting.Action comebackAction) {
        KGPermission
            .with(context)
            .runtime()
            .setting()
            .onComeback(comebackAction)
            .start();
    }

    public static void requestContactsPermission(final Context context, final Runnable onGrantedListener, final Runnable onDeniedListener) {
        KGPermission.with(context)
            .runtime()
            .permission(contactsPermissions)
            .onDenied(new Action<List<String>>() {

                @Override
                public void onAction(List<String> strings) {
                    if (hasReadContactsPermission(context)) {
                        onGrantedListener.run();
                    } else {
                        onDeniedListener.run();
                    }
                }
            })
            .onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> strings) {
                    onGrantedListener.run();
                }
            })
            .start();
    }

    // todo com.kugou.common.permission.PermissionHandler#requestCameraPermissionRationale(android.app.Activity, java.lang.String, int, java.lang.Runnable, java.lang.Runnable)


    /**
     * 业务请求权限工具方法
     * @param context
     * @param permissionString 权限名
     * @param deniedString 拒绝的时候提示文案 （注意：这个字段已经失效）
     * @param onGrantedListener 授权回调
     * @param onDeniedListener 拒绝回调
     */
    public static void requestPermission(final Context context,
        final String permissionString,
        final String deniedString,
        final Runnable onGrantedListener,
        final Runnable onDeniedListener) {
        KGPermission.with(context)
            .runtime()
            .permission(permissionString)
            .onGranted(new Action<List<String>>() {

                @Override
                public void onAction(List<String> strings) {
                    if (onGrantedListener != null) {
                        onGrantedListener.run();
                    }
                }
            })
            .onDenied(new Action<List<String>>() {

                @Override
                public void onAction(List<String> strings) {
                    if (onDeniedListener != null) {
                        onDeniedListener.run();
                    }
                }
            }).start();
    }

    public static boolean hasReadContactsPermission(Context context) {
        return KGPermission.hasPermissions(context, contactsPermissions[0]);
    }

}
