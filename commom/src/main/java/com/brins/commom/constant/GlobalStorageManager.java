package com.brins.commom.constant;

import android.Manifest;
import android.os.Environment;
import androidx.annotation.Nullable;
import com.brins.commom.app.DRCommonApplication;
import com.kugou.common.permission.KGPermission;
import java.io.File;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc
 */
public class GlobalStorageManager {
    private static final String TAG = "GlobalStorageManager";

    private static volatile Boolean hasStoragePermission = null;

    private static volatile Boolean isForceUserNewStorage = null;

    private static boolean isUseNewStorage(boolean simple) {
        return true;
    }

    /**
     * 获取应用专属外部存储空间的持久性文件目录路径
     * 传入短路径，给出全路径，注意此方法只负责返回路径，mkdirs 需要自己处理
     * 无法保证可以访问这些目录中的文件，例如从设备中取出可移除的 SD 卡后，就无法访问其中的文件。
     * 请使用{@link #isExternalStorageWritable()}判断外置存储是否可用，有可能存在外置存储未挂载
     *
     * @param shortDir
     * @return 可空，请注意判空
     */
    @Nullable
    public static String getExternalStoragePath(String shortDir) {
        return getExternalStoragePath(shortDir, false);
    }


    /**
     * 获取应用专属外部存储空间的持久性文件目录路径
     * 传入短路径，给出全路径，注意此方法只负责返回路径，mkdirs 需要自己处理
     * 无法保证可以访问这些目录中的文件，例如从设备中取出可移除的 SD 卡后，就无法访问其中的文件。
     * 请使用{@link #isExternalStorageWritable()}判断外置存储是否可用，有可能存在外置存储未挂载
     *
     * @param shortDir
     * @param isSample 是否参与抽样,不参与直接使用新存储,抽中的使用新存储，否则使用旧版存储
     * @return 可空，请注意判空
     */
    @Nullable
    public static String getExternalStoragePath(String shortDir, boolean isSample) {
        File extDir;
        if (isUseNewStorage(isSample)) {
            extDir = DRCommonApplication.getContext().getExternalFilesDir("");
        } else {
            extDir = Environment.getExternalStorageDirectory();
        }
        if (extDir == null) {
            return getInternalStoragePath(shortDir);
        }
        return new File(extDir, shortDir).getAbsolutePath();
    }

    /**
     * 获取应用专属外部存储空间的持久性文件根目录
     * 无法保证可以访问这些目录中的文件，例如从设备中取出可移除的 SD 卡后，就无法访问其中的文件。
     * 请使用{@link #isExternalStorageWritable()}判断外置存储是否可用，有可能存在外置存储未挂载
     *
     * @return 可空，请注意判空
     */
    @Nullable
    public static String getExternalStorageRootDir() {
        return getExternalStorageRootDir(false);
    }

    /**
     * 获取应用专属外部存储空间的持久性文件根目录
     * 无法保证可以访问这些目录中的文件，例如从设备中取出可移除的 SD 卡后，就无法访问其中的文件。
     * 请使用{@link #isExternalStorageWritable()}判断外置存储是否可用，有可能存在外置存储未挂载
     *
     * @param isSample 是否参与抽样,不参与直接使用新存储,抽中的使用新存储，否则使用旧版存储
     * @return 可空，请注意判空
     */
    @Nullable
    public static String getExternalStorageRootDir(boolean isSample) {
        File extDir;
        if (isUseNewStorage(isSample)) {
            extDir = DRCommonApplication.getContext().getExternalFilesDir("");
        } else {
            extDir = Environment.getExternalStorageDirectory();
        }
        if (extDir == null) {
            return getInternalStorageRootDir();
        }
        return extDir.getAbsolutePath();
    }

    /**
     * 外置存储是否可用
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        try {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取应用专属内部私有存储空间的持久性文件目录路径
     * 传入短路径，给出全路径，注意此方法只负责返回路径，mkdirs 需要自己处理
     * 该目录适合存储只有应用本身才能访问的敏感数据。
     * 如果应用的功能取决于某些文件，应改为将这些文件存储在内部存储空间中。
     *
     * @param shortDir
     * @return
     */
    public static String getInternalStoragePath(String shortDir) {
        return new File(DRCommonApplication.getContext().getFilesDir(), shortDir).getAbsolutePath();
    }


    /**
     * 获取应用专属内部私有存储空间的持久性文件根目录路径
     * 该目录适合存储只有应用本身才能访问的敏感数据。
     * 如果应用的功能取决于某些文件，应改为将这些文件存储在内部存储空间中。
     *
     * @return
     */
    public static String getInternalStorageRootDir() {
        return DRCommonApplication.getContext().getFilesDir().getAbsolutePath();
    }

    /**
     * 获取应用专属内部存储空间的缓存文件目录
     * 传入短路径，给出全路径，注意此方法只负责返回路径，mkdirs 需要自己处理
     * 注意：当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。因此，请在读取前检查缓存文件是否存在。
     *
     * @param shortDir
     * @return
     */
    public static String getInternalCachePath(String shortDir) {
        return new File(DRCommonApplication.getContext().getCacheDir(), shortDir).getAbsolutePath();
    }

    /**
     * 获取应用专属内部存储空间的缓存文件根目录路径
     * 注意：当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。因此，请在读取前检查缓存文件是否存在。
     *
     * @return
     */
    public static String getInternalCachePath() {
        return DRCommonApplication.getContext().getCacheDir().getAbsolutePath();
    }





}
