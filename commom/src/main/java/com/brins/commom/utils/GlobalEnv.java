package com.brins.commom.utils;

import android.os.Environment;
import android.text.TextUtils;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.constant.GlobalStorageManager;
import java.io.File;

/**
 * @author lipeilin
 * @date 2022/10/15
 * @desc
 */
public final class GlobalEnv {
    private final static String TAG ="GlobalEnv";
    public static final String EXTERNAL_ROOT_DIR;
    private static CharSequence secondDirPath;

    public static final File EXTERNAL_DIR = DRCommonApplication.getContext().getExternalFilesDir(null);
    public static final File INNER_FILE_DIR = DRCommonApplication.getContext().getFilesDir();
    public static final String ROOT_DIR = Environment
        .getExternalStorageDirectory().toString();
    static {
        EXTERNAL_ROOT_DIR = GlobalStorageManager.getExternalStorageRootDir(true);
        Exception nullPointerException = new NullPointerException("EXTERNAL_ROOT_DIR is null!!!");
        updateGlobalValue();
    }

    /**
     * 其他图片缓存目录
     */
    public static final String IMAGE_OTHER_FOLDER = getValidPath(EXTERNAL_ROOT_DIR
        + "/dreamify/.images/.other/");
    /**
     * 精品推荐图片缓存目录
     */
    public static final String IMAGE_THIRD_APPS_FOLDER = EXTERNAL_ROOT_DIR
        + "/dreamify/.images/.apps/";

    /**
     * 6.0自定义(下载)背景图文件夹
     */
    public static final String IMAGE_CUSTOM_BG_FOLDER_V6 = EXTERNAL_ROOT_DIR
        + "/dreamify/6.0/.images/.custombg/";

    public static void updateGlobalValue() {

    }

    public static String getValidPath(String originPath) {
        if (TextUtils.isEmpty(secondDirPath)) {
            final File rootDir;
            if (EXTERNAL_DIR != null) {
                rootDir = EXTERNAL_DIR;
            } else if (INNER_FILE_DIR != null) {
                rootDir = INNER_FILE_DIR;
            } else {
                return originPath;
            }

            if (!rootDir.exists() || !rootDir.isDirectory()) {
                if (rootDir.mkdir()) {
                    secondDirPath = rootDir.getAbsolutePath();
                }
                if (TextUtils.isEmpty(secondDirPath)) {
                    return originPath;
                }
            }

            if (!TextUtils.isEmpty(originPath)) {
                if (originPath.startsWith(EXTERNAL_ROOT_DIR)) {
                    return originPath;
                } else if (originPath.startsWith(ROOT_DIR)) {
                    return new File(EXTERNAL_ROOT_DIR, originPath.substring(ROOT_DIR.length())).getAbsolutePath();
                }
            }
        }
        return originPath;
    }
}
