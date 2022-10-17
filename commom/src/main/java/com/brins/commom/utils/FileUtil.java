
package com.brins.commom.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.log.DrLog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.net.Proxy.Type.HTTP;

/**
 * 文件操作类
 *
 * @author Chenys
 */
public class FileUtil {
    private final static String TG = "vz-delete-FileUtil";

    /**
     * 创建文件的模式，已经存在的文件要覆盖
     */
    public final static int MODE_COVER = 1;

    /**
     * 创建文件的模式，文件已经存在则不做其它事
     */
    public final static int MODE_UNCOVER = 0;

    private static final String TAG_KGMP3HASH = "kgmp3hash";

    public static final int TAG_KGMP3HASH_LENGTH = TAG_KGMP3HASH.length() + 32;

    public static final int ENCRYPT_FILE_HEAD_SIZE = 1024;

    public static void createParentIfNecessary(String path) {
        File file = new DelFile(path);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
        }
    }

    /**
     * 如果file不存在那么创建新的文件
     *
     * @param filePath
     * @return
     */
    public static File createFileIfFileNoExits(String filePath) {
        File file = new DelFile(filePath);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    /**
     * 向文件的末尾添加数据
     *
     * @param path
     * @param data
     */
    public static boolean appendData(String path, byte[] data) {
        FileOutputStream fos = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                if (DrLog.DEBUG) DrLog.d("hch-file", "file.exists()  appendData path = " + path);
                createParentIfNecessary(path);
                fos = new FileOutputStream(file, true);
                fos.write(data);
                fos.flush();
            }
            if (DrLog.DEBUG) DrLog.d("hch-file", "appendData path = " + path);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                return true;
            } else {
                return false;
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 在文件末尾追加字符串，如果文件不存在，那么重新创建
     *
     * @param filePath
     * @param str
     * @return
     */
    public static boolean appendString(String filePath, String str) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = createFileIfFileNoExits(filePath);
        if (file != null) {
            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(file, true);
                bw = new BufferedWriter(fw);
                bw.write(str);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean appendString(String filePath, String str, boolean append) {
        boolean result = false;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(filePath)) {
            return result;
        }
        File file = createFileIfFileNoExits(filePath);
        if (file != null) {
            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(file, append);
                bw = new BufferedWriter(fw);
                bw.write(str);
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 向文件末尾添加数据
     *
     * @param path
     * @param is
     */
    public static void appendData(String path, InputStream is) {
        FileOutputStream fos = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                createParentIfNecessary(path);
                fos = new FileOutputStream(file, true);
                byte[] data = new byte[1024];
                int receive = 0;
                while ((receive = is.read(data)) != -1) {
                    fos.write(data, 0, receive);
                    fos.flush();
                }
            }
        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }




    /**
     * 复制文件，sourcePath，targetPath必须是文件路径，否则没效
     *
     * @param sourcePath 原文件路径
     * @param targetPath 目标文件路径
     * @throws IOException
     */
    public static int copyFile(String sourcePath, String targetPath) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        int resultType = 0;
        try {
            File sourceFile = new DelFile(sourcePath);
            File targetFile = new DelFile(targetPath);
            if (!sourceFile.exists() || sourceFile.isDirectory())
                return 1;
            if (sourceFile.isDirectory())
                return 2;
            if (!targetFile.exists()) {
                createParentIfNecessary(targetPath);
                targetFile.createNewFile();
            }
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } catch (Exception e) {
            resultType = 3;
            e.printStackTrace();
            DrLog.e("wwhSkin", e.getMessage());
        } finally {
            try {
                // 关闭流
                if (inBuff != null)
                    inBuff.close();
                if (outBuff != null)
                    outBuff.close();
            } catch (IOException e) {
                resultType = 4;
                e.printStackTrace();
            }
        }
        return resultType;
    }

    /**
     * 从InputStream中拷贝文件
     *
     * @param sourceIS   原文件InputStream
     * @param targetPath 目标文件路径
     */
    public static void copyFileFromIS(InputStream sourceIS, String targetPath) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            File targetFile = new DelFile(targetPath);
            if (sourceIS == null)
                return;
            if (!targetFile.exists()) {
                createParentIfNecessary(targetPath);
                targetFile.createNewFile();
            }
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(sourceIS);
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                if (inBuff != null)
                    inBuff.close();
                if (outBuff != null)
                    outBuff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建文件目录
     *
     * @param filePath
     * @return
     */
    public static boolean createDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }

        File file = new DelFile(filePath);

        if (file.exists()) {
            return true;
        }

        return file.mkdirs();
    }

    public static void closeIS(Closeable is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个空的文件(创建文件的模式，已经存在的是否要覆盖)
     *
     * @param path
     * @param mode
     */
    public static boolean createFile(String path, int mode) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                if (mode == FileUtil.MODE_COVER) {
                    deleteFile(file, DEL_FILE_BY_UNKNOW);//由于这个路径一定是文件路径，所以还是需要检查文件后缀来发送
                    file.createNewFile();
                }
            } else {
                // 如果路径不存在，先创建路径
                File mFile = file.getParentFile();
                if (!mFile.exists()) {
                    mFile.mkdirs();
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 创建一个空的文件夹(创建文件夹的模式，已经存在的是否要覆盖)
     *
     * @param path
     * @param mode
     */
    public static void createFolder(String path, int mode) {
        try {
            // LogUtil.debug(path);
            File file = new DelFile(path);
            if (file.exists()) {
                if (mode == FileUtil.MODE_COVER) {
                    //这个函数的逻辑有点奇怪，下面的删除只能删除空的文件夹或文件；MODE_COVER模式很奇怪
                    FileUtil.deleteFile(file, DEL_FILE_BY_CREATE_FOLDER);//A|B)
                    file.mkdirs();
                }
            } else {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除目录下文件，除了exceptFilePath文件
     *
     * @param dirPath
     * @param exceptFilePath 不删除的文件
     * @return
     */
    public static boolean deleteChilds(String dirPath, String exceptFilePath) {
        return deleteChilds(dirPath, exceptFilePath, null);
    }

    /**
     * 删除目录下文件，除了exceptFilePath文件
     *
     * @param dirPath
     * @param exceptFilePath 不删除的文件
     * @return
     */
    public static boolean deleteChilds(String dirPath, String exceptFilePath, FilenameFilter filter) {
        if (TextUtils.isEmpty(dirPath)) {
            return false;
        }

        File dirFile = new DelFile(dirPath);

        if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }

//        File[] list = filter == null ? dirFile.listFiles() : dirFile.listFiles(filter);//vz1234--需要过滤
        File[] list;
        if (filter == null) {
            list = DelFileChecker.getInstance().listFilesNonKugouFolderMusic(dirFile);
        } else {//如果已有过滤逻辑那么不改变他原有的逻辑
            list = dirFile.listFiles(filter);
        }

        if (list == null) {
            return false;
        }

        for (File file : list) {
            if (file.getAbsolutePath().equals(exceptFilePath)) {
                continue;
            }

            if (file.isDirectory()) {
                deleteDirectory(file.getAbsolutePath());
            } else {
                deleteFile(file, DEL_FILE_BY_UNKNOW);
            }
        }

        return true;
    }

    /**
     * 删除目录下所有文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteChilds(String filePath) {
        if (null == filePath) {
            if (DrLog.DEBUG) DrLog.e("param invalid, filePath: " + filePath);
            return false;
        }

        File file = new DelFile(filePath);

        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
//            File[] list = file.listFiles();//vz1234-需要过滤
            File[] list = DelFileChecker.getInstance().listFilesNonKugouFolderMusic(file);
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (DrLog.DEBUG) {
                        DrLog.d("delete filePath: " + list[i].getAbsolutePath());
                    }
                    if (list[i].isDirectory()) {
                        deleteDirectory(list[i].getAbsolutePath());
                    } else {
                        deleteFile(list[i], DEL_FILE_BY_UNKNOW);
                    }
                }
            }
        }

        return true;
    }

    /**
     * 删除目录 及其 子目录
     *
     * @param filePath
     * @return
     */
    public static boolean deleteDirectory(String filePath) {
        if (null == filePath) {
            if (DrLog.DEBUG) DrLog.e("param invalid, filePath: " + filePath);
            return false;
        }

        File file = new DelFile(filePath);
        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
//            File[] list = file.listFiles();//vz1234--需要过滤
            File[] list = DelFileChecker.getInstance().listFilesNonKugouFolderMusic(file);
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (DrLog.DEBUG) DrLog.d("delete filePath: " + list[i].getAbsolutePath());

                    if (list[i].isDirectory()) {
                        deleteDirectory(list[i].getAbsolutePath());
                    } else {
                        deleteFile(list[i], DEL_FILE_BY_UNKNOW);
                    }
                }
            }
        }

        if (DrLog.DEBUG) DrLog.e("delete filePath: " + file.getAbsolutePath());

        deleteFile(file, DEL_FILE_BY_UNKNOW);
        return true;
    }

    /**
     * 删除文件或文件夹(包括目录下的文件)
     * 请尽量使用deleteFile(File) or deleteDirectory(String)
     */
    @Deprecated
    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File f = new DelFile(filePath);
            if (f.exists()) {
                if (f.isDirectory()) {
                    File[] delFiles = DelFileChecker.getInstance().listFilesNonKugouFolderMusic(f);
                    if (delFiles != null) {
                        for (int i = 0; i < delFiles.length; i++) {
                            deleteFile(delFiles[i].getAbsolutePath());
                        }
                    }
                }
                FileUtil.deleteFile(f);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @param deleteParent 是否删除父目录
     * @deprecated
     */
    @Deprecated
    public static void deleteFile(String filePath, boolean deleteParent) {
        if (filePath == null) {
            return;
        }
        try {
            File f = new DelFile(filePath);
            if (f.exists() && f.isDirectory()) {
//                File[] delFiles = f.listFiles();//vz1234--需要过滤
                File[] delFiles = DelFileChecker.getInstance().listFilesNonKugouFolderMusic(f);
                if (delFiles != null) {
                    for (int i = 0; i < delFiles.length; i++) {
                        deleteFile(delFiles[i].getAbsolutePath(), deleteParent);
                    }
                }
            }
            if (deleteParent) {
                deleteFile(f, DEL_FILE_BY_UNKNOW);
            } else if (f.isFile()) {
                deleteFile(f, DEL_FILE_BY_UNKNOW);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean fileIsExist(String filePath) {
        if (filePath == null || filePath.length() < 1) {
            if (DrLog.DEBUG) DrLog.e("param invalid, filePath: " + filePath);
            return false;
        }

        File f = new DelFile(filePath);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 调用File.delete()，不做任何其他判断
     * 目的只是统一删除文件的入口
     * 该方法需要提换
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        return deleteFile(file, DEL_FILE_BY_UNKNOW);//delete_file_check 需要统计
    }

    /**
     * 获取文件夹大小
     *
     * @param path
     * @return
     */
    public static long getAllSize(String path) {//only DebugActivity use
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        File file = new DelFile(path);
        if (!file.exists())
            return 0;

        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();//vz1234--safe only now
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getAllSize(child.getPath());
        return total;
    }

    public static long getDictoryFileSize(String path) {//only DebugActivity use
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        File file = new DelFile(path);
        if (!file.exists())
            return 0;

        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();//vz1234--safe only now
        long total = 0;
        if (children != null)
            for (final File child : children) {
                if (child != null && child.isFile()) {
                    total += child.length();
                }
            }
        return total;
    }

    public static boolean redirectDeleteChilds(String filePath) {
        if (null == filePath) {
            if (DrLog.DEBUG) DrLog.e("param invalid, filePath: " + filePath);
            return false;
        }

        File file = new DelFile(filePath);

        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (DrLog.DEBUG) {
                        DrLog.d("delete filePath: " + list[i].getAbsolutePath());
                    }
                    if (list[i].isDirectory()) {
                        deleteDirectory(list[i].getAbsolutePath());
                    } else {
                        deleteFile(list[i], DEL_FILE_BY_UNKNOW);
                    }
                }
            }
        }

        return true;
    }

    /**
     * @param path hash.ext.kgtmp
     * @return
     */
    public static String getAudioMimeType(String path) {
        boolean isM4A = path.toLowerCase().endsWith(".m4a");
        return isM4A ? "audio/mp4" : "audio/mpeg";
    }

    /**
     * 把一个
     *
     * @param filePath
     * @return
     */
    public static String getDuplicateFileName(String filePath, String fileName) {
        String newName = "";
        int count = 0;
        boolean isExist = isExist(filePath + "/" + newName);
        String[] name = StringUtil.getFileNameAndExtName(fileName);
        while (isExist && name != null) {
            newName = StringUtil.formatFilePath(name[0] + "(" + (++count) + ")") + "." + name[1];
            isExist = isExist(filePath + "/" + newName);
        }
        return newName;
    }

    /**
     * 获取指定路径的文件，若是用于jpgPng文件，允许后缀未指定。
     *
     * @param path 文件路径，允许png jpg后缀未指定
     * @return 如果文件存在，真实的文件。如果不存在 返回null.
     */
    public static File getExistJpgPngFile(String path) {
        File resultFile = null;

        File file = new DelFile(path);
        if (file.exists()) {
            resultFile = file;
        } else {
            String jpgPath = path.trim() + ".jpg";
            File jpgFile = new DelFile(jpgPath);
            if (jpgFile.exists()) {
                resultFile = jpgFile;
            } else {
                String pngPath = path.trim() + ".png";
                File pngFile = new DelFile(pngPath);
                if (pngFile.exists()) {
                    resultFile = pngFile;
                }
            }
        }

        return resultFile;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getExtName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return "";
        } else {
            return fileName.substring(index + 1, fileName.length());
        }
    }

    /**
     * 获取小写的文件扩展名
     *
     * @param fileName 文件名
     * @return 小写的扩展名
     */
    public static String getLowerCaseExtName(String fileName) {
        String ext = getExtName(fileName);
        return ext != null ? ext.toLowerCase() : null;
    }

    /**
     * 获取文件的数据
     *
     * @param path
     * @return
     */
    public static byte[] getFileData(String path) {
        return InfoUtils.getFileData(path);
    }

    /**
     * 将文件内容以字符串形式读出来
     *
     * @param filePath
     * @param charsetName The name of a supported
     *                    {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static String getFileData(String filePath, String charsetName) {
        File file = new DelFile(filePath);
        String fileContent = "";
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            createParentIfNecessary(filePath);
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.equals("")) {
                    fileContent += "\r\n";
                }
                fileContent += line;
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }


    /**
     * 获取文件的输入流
     *
     * @param path
     * @return
     */
    public static FileInputStream getFileInputStream(String path) {
        FileInputStream fis = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                fis = new FileInputStream(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fis;
    }

    /**
     * 获取文件最后修改时间
     *
     * @param filePath
     * @return
     */
    public static long getFileModifyTime(String filePath) {
        if (null == filePath) {
            if (DrLog.DEBUG) DrLog.e("Invalid param. filePath: " + filePath);
            return 0;
        }

        File file = new DelFile(filePath);
        if (file == null || !file.exists()) {
            return 0;
        }

        return file.lastModified();
    }

    /**
     * 解析文件全名,带扩展名
     *
     * @param filePath 文件路径
     * @return 文件全名
     */
    public static String getFileNameWithExt(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int last = filePath.lastIndexOf("/");
        int end = filePath.length() - 1;
        if (last == -1) {
            return filePath;
        } else if (last < end) {
            return filePath.substring(last + 1);
        } else {
            return filePath.substring(last);
        }
    }

    /**
     * 获取文件名，不带扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameWithoutExt(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int last = filePath.lastIndexOf("/");
        int index = filePath.lastIndexOf(".");
        if (last == -1 && index == -1) {
            return filePath;
        } else if (index > last) {
            return filePath.substring(last + 1, index);
        } else {
            return filePath.substring(last);
        }
    }

    /**
     * 获取文件的输出流
     *
     * @param path
     * @return
     */
    public static OutputStream getFileOutputStream(String path) {
        FileOutputStream fos = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                fos = new FileOutputStream(file);
            }
        } catch (Exception e) {
            return null;
        }
        return fos;
    }

    /**
     * 文件大小
     *
     * @param path
     * @return
     */
    public static int getFileSize(String path) {
        if (TextUtils.isEmpty(path))
            return -1;
        return (int) new DelFile(path).length();
    }

    public static long getmem_UNUSED(Context mContext) {
        long MEM_UNUSED;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        // 取得剩余的内存空间

        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }

    /**
     * 获取父目录
     *
     * @return
     */
    public static String getParentPath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int last = filePath.lastIndexOf("/");
        if (last == -1) {
            return null;
        }
        return filePath.substring(0, last + 1);
    }

    /**
     * 将文件内容以字符串形式读出来
     *
     * @param charsetName The name of a supported
     *                    {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static String getRawFileData(Context context, int resId, String charsetName) {

        String fileContent = "";
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(context.getResources().openRawResource(
                    resId), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent += "\r\n";
                }
                fileContent += line;
            }
            reader.close();
            return fileContent;
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        //某些机型会找不到此目录
        if (!path.isDirectory())
            return -1;
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        long size = 0;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                size = file.length();
            }
        } catch (Exception e) {
            return 0;
        }
        return size;
    }

    public static String getUnSameFileNameOfNum(String folder, String filename) {
        if (TextUtils.isEmpty(filename)) {
            return "";
        }
        if (filename.lastIndexOf(".") == -1) {
            return folder + StringUtil.formatFilePath(filename);
        }
        String title = filename.substring(0, filename.lastIndexOf("."));
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        int count = 0;
        String tempFilename = folder + StringUtil.formatFilePath(title) + "." + ext;
        boolean isExist = isExist(tempFilename);
        while (isExist) {
            tempFilename = folder + StringUtil.formatFilePath(title + "(" + (++count) + ")") + "."
                    + ext;
            isExist = isExist(tempFilename);
        }
        return tempFilename;
    }

    public static String getUnSameFileNameOfNum(String folder, String filename,
                                                String sourceFilePath) {
        if (TextUtils.isEmpty(filename)) {
            return "";
        }
        if (filename.lastIndexOf(".") == -1) {
            return folder + StringUtil.formatFilePath(filename);
        }
        String title = filename.substring(0, filename.lastIndexOf("."));
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        int count = 0;
        String tempFilename = folder + StringUtil.formatFilePath(title) + "." + ext;
        boolean isExist = !tempFilename.equals(sourceFilePath) && isExist(tempFilename);
        while (isExist) {
            tempFilename = folder + StringUtil.formatFilePath(title + "(" + (++count) + ")") + "."
                    + ext;
            isExist = isExist(tempFilename);
        }
        return tempFilename;
    }

    /*
    public static String getUnSameFileNameOfNumWithDb(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        String title = SystemUtils.getShortFileNameWithoutSuffix(filePath);
        String ext = SystemUtils.getFileSuffix(filePath);
        String folder = getParentPath(filePath);
        int count = 0;
        String tempFilePath = folder + title + ext;
        boolean isExistInFile = isExist(tempFilePath);
        boolean isExistInDb = LocalMusicDao.checkLocalMusicExitByPath(filePath);
        boolean isExist = isExistInFile && isExistInDb;
        while (isExist) {
            tempFilePath = folder + title + "(" + (++count) + ")" + ext;
            isExistInFile = isExist(tempFilePath);
            isExistInDb = LocalMusicDao.checkLocalMusicExitByPath(tempFilePath);
            isExist = isExistInFile && isExistInDb;
        }
        return tempFilePath;
    }
    */

    /**
     * 某个文件夹下是否文件
     */
    public static boolean hasfile(String filepath) {
        boolean returnValue = false;
        File file = new DelFile(filepath);
        if (!file.exists()) {
            returnValue = false;
        } else if (!file.isDirectory()) {
            if (file.length() > 0) {
                returnValue = true;
                return returnValue;
            }
        } else if (file.isDirectory()) {
            if (DrLog.DEBUG) DrLog.d("文件夹:" + file.getName());
            String[] filelist = file.list();
            if (filelist != null) {
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new DelFile(filepath + "/" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        if (readfile.length() > 0) {
                            returnValue = true;
                            return returnValue;
                        }
                    } else {
                        if (hasfile(filepath + "/" + filelist[i])) {
                            returnValue = true;
                            return true;
                        }
                    }
                }
            }
        }
        return returnValue;
    }

    public static byte[] InputStreamToByte(InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = iStrm.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    public static byte[] InputStreamToByteNew(InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        byte[] buf = new byte[1024];
        while ((ch = iStrm.read(buf)) != -1) {
            bytestream.write(buf, 0, ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    /**
     * 判断是不是文件夹
     *
     * @param path
     * @return true 是文件夹
     */
    public static boolean isDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new DelFile(path);
        return file.isDirectory();
    }

    /**
     * 是否是下载出错文件（下到错误页面的数据）
     *
     * @param filePath 文件路径
     * @return
     */
    public static boolean isErrorFile(final String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        FileInputStream stream = null;
        try {
            createParentIfNecessary(filePath);
            stream = new FileInputStream(new DelFile(filePath));
            byte[] buffer = new byte[16];
            if (stream.read(buffer) == 16) {
                return ((buffer[0] & 0xFF) == 0xFF && (buffer[1] & 0xFF) == 0xD8
                        && (buffer[2] & 0xFF) == 0xFF && (buffer[3] & 0xFF) == 0xE0
                        && (buffer[4] & 0xFF) == 0x00 && (buffer[5] & 0xFF) == 0x10
                        && (buffer[6] & 0xFF) == 0x4A && (buffer[7] & 0xFF) == 0x46
                        && (buffer[8] & 0xFF) == 0x49 && (buffer[9] & 0xFF) == 0x46
                        && (buffer[10] & 0xFF) == 0x00 && (buffer[11] & 0xFF) == 0x01
                        && (buffer[12] & 0xFF) == 0x02 && (buffer[13] & 0xFF) == 0x01
                        && (buffer[14] & 0xFF) == 0x00 && (buffer[15] & 0xFF) == 0x48);
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param path
     * @return true 文件存在
     */
    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean exist = false;
        try {
            File file = new DelFile(path);
            exist = file.exists();
        } catch (Exception e) {
            return false;
        }
        return exist;
    }

    public static boolean isExistAndCanRead(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean exist = false;
        try {
            File file = new DelFile(path);
            exist = file.exists() && file.canRead();
        } catch (Exception e) {
            return false;
        }
        return exist;
    }

    /**
     * 判断文件或文件夹是否存在
     * 如果exists方法判断异常,那么返回null
     *
     * @param path
     * @return true 文件存在
     */
    public static Boolean safeExistsOrNull(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        //test code
        /*if(DrLog.isLogFile()) {
            if (path.contains("GOT7")) {
                return null;
            }
        }*/
        try {
            File file = new DelFile(path);
            return file.exists();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断文件是否0字节 是的话删除
     *
     * @param path
     * @return
     */
    public static boolean isExistAndFileNotNull(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean exist = false;
        try {
            File file = new DelFile(path);
            exist = file.exists();
            if (exist && file.length() > 0) {
                return true;
            } else if (exist && file.length() == 0) {
                file.delete();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return exist;
    }

    /**
     * 判断文件存在，且不是“空文件或空的加密缓存文件”。
     * 此方法不删除文件。
     **/
    public static boolean isFileExistAndNotNullOfEncrypt(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        boolean exist = false;
        try {
            File file = new DelFile(path);
            if (file.isDirectory()) {
                return false;
            }

            exist = file.exists();
            if (exist) {
                long fileSize = file.length();
                if (fileSize == 0) {
                    exist = false;
                } else if (fileSize == ENCRYPT_FILE_HEAD_SIZE && isRealEncryptedFile(path)) {
                    exist = false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return exist;
    }
    //            对应
    //            GlobalEnv.BUFFER_EXT,
    //            GlobalEnv.SHORT_BUFFER_EXT,
    //            GlobalEnv.ENCRYPT_BUFFER_EXT,
    private final static String[] SUBFIX_TEMP_SONG = {
            ".kgtmp",
            ".kgt",
            ".kge",
    };

    public static boolean existsAndNonTmpSong(String path) {
        if (!TextUtils.isEmpty(path)) {
            return existsAndNonTmpSong(new DelFile(path));
        }
        return false;
    }

    /**
     * 存在并且不是临时的歌曲文件
     *
     * @param f
     * @return
     */
    public static boolean existsAndNonTmpSong(File f) {
        boolean ret = f.exists();
        if (ret) {//如果文件存在检查文件后缀
            String name = f.getName();//文件存在，文件名必然不为空
            for (String subfix : SUBFIX_TEMP_SONG) {
                if (name.endsWith(subfix)) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }
    

    public static final int FLAG_FILE_EXISTS = 1 << 0;//标识位，是否存在
    public static final int FLAG_FILE_MUSIC_TMP = 1 << 1;//标识位，是否存临时音频文件
    //标识，是否存在的临时音频文件
    public static final int FLAG_FILE_EXISTS_MUSIC_TMP = (FLAG_FILE_EXISTS | FLAG_FILE_MUSIC_TMP);

    /**
     * 文件是否存在，以及文件是否是临时的音频文件类型；
     * 该方法效率不及 FileUtil.existsAndNonTmpSong()
     *
     * @param f
     * @return
     */
    public static int existsFileType(File f) {
        boolean exists = f.exists();
        int ret = exists ? FLAG_FILE_EXISTS : 0;
        String name = f.getName();
        if (!TextUtils.isEmpty(name)) {
            for (String subfix : SUBFIX_TEMP_SONG) {
                if (name.endsWith(subfix)) {//音频文件的临时文件
                    ret |= FLAG_FILE_MUSIC_TMP;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 判断是不是文件
     *
     * @param path
     * @return true 是文件
     */
    public static boolean isFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean isFile = false;
        try {
            File file = new DelFile(path);
            isFile = file.isFile();
        } catch (Exception e) {
            return false;
        }
        return isFile;
    }

/*    public static boolean isTempCachePath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        if (path.endsWith(GlobalEnv.BUFFER_EXT) || path.endsWith(GlobalEnv.SHORT_BUFFER_EXT)
                || path.endsWith(GlobalEnv.ENCRYPT_BUFFER_EXT)) {
            return true;
        } else {
            return false;
        }
    }*/

/*    public static boolean isNotTempCachePath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        return !isTempCachePath(path);
    }*/

    static final byte[] ENCRYPT_HEAD = new byte[]{
            (byte) 0x7c, (byte) 0xd5, (byte) 0x32, (byte) 0xeb,
            (byte) 0x86, (byte) 0x02, (byte) 0x7f, (byte) 0x4b,
            (byte) 0xa8, (byte) 0xaf, (byte) 0xa6, (byte) 0x8e,
            (byte) 0x0f, (byte) 0xff, (byte) 0x99, (byte) 0x14,
    };

    static boolean bytesEquals(byte[] a, byte[] b) {
        int aLen = a == null ? 0 : a.length;
        int bLen = b == null ? 0 : b.length;
        if (aLen != bLen) {
            return false;
        } else if (aLen == 0 && bLen == 0) {
            return true;
        }

        for (int i = 0; i < aLen; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean isRealEncryptedFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new DelFile(path));
            byte[] buffer = new byte[16];
            int ret = stream.read(buffer);

            return ret == 16 && bytesEquals(buffer, ENCRYPT_HEAD);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 是否是m4a文件
     *
     * @param m4a m4a文件路径
     * @return
     */
    public static boolean isM4A(final String m4a) {
        if (TextUtils.isEmpty(m4a)) {
            return false;
        }
        FileInputStream stream = null;
        try {
            createParentIfNecessary(m4a);
            stream = new FileInputStream(new DelFile(m4a));
            byte[] buffer = new byte[8];
            if (stream.read(buffer) == 8) {
                return (buffer[4] == 'f' && buffer[5] == 't' && buffer[6] == 'y' && buffer[7] == 'p');
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 列出目录文件
     *
     * @return
     */
    public static File[] listFiles(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new DelFile(filePath);
            if (file.exists() && file.isDirectory()) {
                return file.listFiles();//vz1234--?
            }
        }
        return null;
    }

    /**
     * 列出目录文件
     *
     * @return
     */
    public static File[] listFiles(String filePath, FileFilter filter) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new DelFile(filePath);
            if (file.exists() && file.isDirectory()) {
                return file.listFiles(filter);
            }
        }
        return null;
    }

    /**
     * 列出目录文件
     *
     * @return
     */
    public static File[] listFiles(String filePath, FilenameFilter filter) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new DelFile(filePath);
            if (file.exists() && file.isDirectory()) {
                return file.listFiles(filter);
            }
        }
        return null;
    }

    /**
     * 从assset资源中读取Properties
     *
     * @param context
     * @param assetPath
     * @return
     */
    public static Properties loadPropertiesFromAsset(Context context, String assetPath) {
        Properties result = null;
        if (context != null && !TextUtils.isEmpty(assetPath)) {
            InputStream ins = null;
            try {
                ins = context.getAssets().open(assetPath);
                result = new Properties();
                result.load(ins);
            } catch (IOException e) {
                result = null;
                e.printStackTrace();
            } finally {
                try {
                    if (ins != null) {
                        ins.close();
                    }
                } catch (Exception e2) {
                }
            }
        }
        return result;
    }

    /**
     * 从raw资源中读取Properties
     *
     * @param context
     * @param rawId
     * @return
     */
    public static Properties loadPropertiesFromRaw(Context context, int rawId) {
        Properties result = null;
        if (context != null) {
            InputStream ins = null;
            try {
                ins = context.getResources().openRawResource(rawId);
                result = new Properties();
                result.load(ins);
            } catch (IOException e) {
                result = null;
                e.printStackTrace();
            } finally {
                try {
                    if (ins != null) {
                        ins.close();
                    }
                } catch (Exception e2) {
                }
            }
        }
        return result;
    }

    public static boolean movePicFile(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        boolean renameResult = srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
        String canonicalPath;
        try {
            canonicalPath = srcFile.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = srcFile.getAbsolutePath();
        }
        if (DrLog.DEBUG) DrLog.d("wu FileUtil", "path:" + canonicalPath);
        final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final int result = DRCommonApplication.getContext().getContentResolver().delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = srcFile.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                DRCommonApplication.getContext().getContentResolver().delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
        return renameResult;
    }


    /**
     * 不同机型相机所创建的相册目录不相同，目前就三种，Camera,100MEDIA,100ANDRO,按顺序查询，都没有的话就返回picture目录。
     *
     * @return
     */
    @Deprecated
    /**
     *不允许用绝对路径操作公有目录,请使用PublicDocument库
     */
    public static String getCurrentPath() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (path.exists()) {
            File cameraFile = new File(path + File.separator + "Camera");
            if (cameraFile.exists()) {
                path = cameraFile;
            } else {
                File mediaFile = new File(path + File.separator + "100MEDIA");
                if (mediaFile.exists()) {
                    path = mediaFile;
                } else {
                    File androFile = new File(path + File.separator + "100ANDRO");
                    if (androFile.exists()) {
                        path = androFile;
                    } else {
                        File picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        if (!picturePath.exists() || !picturePath.isDirectory()) {
                            picturePath.mkdirs();
                        }
                        path = picturePath;
                    }
                }
            }
        } else {
            File picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!picturePath.exists() || !picturePath.isDirectory()) {
                picturePath.mkdirs();
            }
            path = picturePath;
        }
        return path.getAbsolutePath();
    }

    /**
     * 移动文件
     *
     * @param oldFilePath 旧路径
     * @param newFilePath 新路径
     * @return
     */
    public static boolean moveFile(String oldFilePath, String newFilePath) {
        if (TextUtils.isEmpty(oldFilePath) || TextUtils.isEmpty(newFilePath)) {
            return false;
        }
        File oldFile = new DelFile(oldFilePath);
        if (oldFile.isDirectory() || !oldFile.exists()) {
            return false;
        }
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try {
            File newFile = new DelFile(newFilePath);
            if (!newFile.exists()) {
                createParentIfNecessary(newFilePath);
                newFile.createNewFile();
            }
            bis = new BufferedInputStream(new FileInputStream(oldFile));
            fos = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int read;
            while ((read = bis.read(buf)) != -1) {
                fos.write(buf, 0, read);
            }
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取文件
     *
     * @param filePath
     * @return 输入流
     */
    public static InputStream readFile(String filePath) {
        if (null == filePath) {
            if (DrLog.DEBUG) DrLog.e("Invalid param. filePath: " + filePath);
            return null;
        }

        InputStream is = null;

        try {
            if (fileIsExist(filePath)) {
                File f = new DelFile(filePath);
                is = new FileInputStream(f);
            } else {
                return null;
            }
        } catch (Exception ex) {
            if (DrLog.DEBUG) DrLog.e("Exception, ex: " + ex.toString());
            return null;
        }
        return is;
    }

    /**
     * 读取文件转为字符串
     *
     * @param filePath
     * @return
     */
    public static String readFileToString(String filePath) {
        String str = "";
        FileInputStream inStream = null;
        try {
            File readFile = new DelFile(filePath);
            if (!readFile.exists()) {
                return null;
            }
            inStream = new FileInputStream(readFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            str = stream.toString();
            stream.close();
            return str;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取输入流 转化为 byte[]
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static byte[] readIn(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        byte[] buf = new byte[1024];
        int c = is.read(buf);
        while (-1 != c) {
            baos.write(buf, 0, c);
            c = is.read(buf);
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    public static boolean isM4aPathOfEncrypt(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        path = path.toLowerCase();
        if (path.endsWith(".m4a")) {
            return true;
        }

        return false;
    }

    /**
     * 从m4a读取mp3哈希值
     *
     * @param m4a m4a文件路径
     * @return
     */
    /*
    public static String readMp3HashFromM4a(String m4a) {
        if (TextUtils.isEmpty(m4a)) {
            return null;
        }

        if (isRealEncryptedFile(m4a)) {
            byte[] b = new byte[TAG_KGMP3HASH_LENGTH];
            if (FileServiceUtil.readFileTail(m4a, b) == TAG_KGMP3HASH_LENGTH) {
                String taghash = new String(b);
                if (!TextUtils.isEmpty(taghash) && taghash.startsWith(TAG_KGMP3HASH)) {
                    return taghash.substring(TAG_KGMP3HASH.length());
                }
            }

            return "";
        }

        File m4afile = new DelFile(m4a);
        RandomAccessFile accessFile = null;
        try {
            createParentIfNecessary(m4a);
            accessFile = new RandomAccessFile(m4afile, "r");
            accessFile.skipBytes((int) (m4afile.length() - TAG_KGMP3HASH_LENGTH));// parasoft-suppress PB.LOGIC.CRRV-1
            byte[] b = new byte[TAG_KGMP3HASH_LENGTH];
            if (accessFile.read(b) == TAG_KGMP3HASH_LENGTH) {
                String taghash = new String(b);
                if (!TextUtils.isEmpty(taghash) && taghash.startsWith(TAG_KGMP3HASH)) {
                    return taghash.substring(TAG_KGMP3HASH.length());
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
    */


    /*
    public static String getRealM4aHash(File file) {
        String hash = null;
        InputStream in = null;
        if (file == null || !file.exists()) {
            return null;
        }
        String filePath = file.getAbsolutePath();
        long fileSize = file.length();
        int realSize = (int) fileSize - TAG_KGMP3HASH_LENGTH;
        try {
            byte[] fileData = FileUtil.readFileData(filePath, 0, realSize);
            in = new ByteArrayInputStream(fileData);
            hash = FileMD5Util.getInstance().getStreamHash(in);
            in.close();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
        return hash;
    }
    */

    /**
     * 重命名文件/文件夹
     *
     * @param path
     * @param newName
     */
    public static boolean rename(final String path, final String newName) {
        boolean result = false;
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(newName)) {
            return result;
        }
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                result = file.renameTo(new DelFile(newName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 重写文件的数据
     *
     * @param path
     * @param data
     */
    public static void rewriteData(String path, byte[] data) {
        FileOutputStream fos = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                fos = new FileOutputStream(file, false);
                fos.write(data);
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 重写文件的数据
     *
     * @param path
     * @param is
     */
    public static void rewriteData(String path, InputStream is) {
        FileOutputStream fos = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                fos = new FileOutputStream(file, false);
                byte[] data = new byte[1024];
                int receive = 0;
                while ((receive = is.read(data)) != -1) {
                    fos.write(data, 0, receive);
                    fos.flush();
                }
            }
        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 保存文件
     *
     * @param toSaveString
     * @param filePath
     */
    public static void saveFile(String toSaveString, String filePath) {
        FileOutputStream outStream = null;
        try {
            File saveFile = new DelFile(filePath);
            if (!saveFile.exists()) {
                File dir = new DelFile(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }

            outStream = new FileOutputStream(saveFile);
            outStream.write(toSaveString.getBytes());
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void saveFileBeforeDelete(String toSaveString, String filePath) {
        FileOutputStream outStream = null;
        try {
            File saveFile = new DelFile(filePath);
            if (saveFile.exists()) {
                saveFile.delete();
            }
            if (!saveFile.exists()) {
                File dir = new DelFile(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }

            outStream = new FileOutputStream(saveFile);
            outStream.write(toSaveString.getBytes());
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 保存文件
     *
     * @param data
     * @param filePath
     */
    public static boolean saveFile(byte[] data, String filePath) {
        FileOutputStream outStream = null;
        try {
            File saveFile = new DelFile(filePath);
            if (!saveFile.exists()) {
                File dir = new DelFile(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }
            outStream = new FileOutputStream(saveFile);
            outStream.write(data);
            return true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(outStream);
        }

        return false;
    }

    /**
     * 保存文件
     *
     * @param data
     * @param filePath
     */
    public static boolean saveFile(byte[] data, int off, int len, String filePath) {
        FileOutputStream outStream = null;
        try {
            File saveFile = new DelFile(filePath);
            if (!saveFile.exists()) {
                File dir = new DelFile(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }
            outStream = new FileOutputStream(saveFile);
            outStream.write(data, off, len);
            return true;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            IOUtils.closeQuietly(outStream);
        }

        return false;
    }

    /**
     * 设置文件最后修改时间
     *
     * @param filePath
     * @param modifyTime
     * @return
     */
    public static boolean setFileModifyTime(String filePath, long modifyTime) {
        if (null == filePath) {
            if (DrLog.DEBUG) DrLog.e("Invalid param. filePath: " + filePath);
            return false;
        }

        File file = new DelFile(filePath);
        if (file == null || !file.exists()) {
            return false;
        }

        return file.setLastModified(modifyTime);
    }

    public static ArrayList<byte[]> split(String filePath, int startPos, int blockSize) {
        if (TextUtils.isEmpty(filePath) || blockSize <= 0) {
            return null;
        }
        File file = new DelFile(filePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        long fileLen = file.length();
        if (startPos >= fileLen) {
            return null;
        }

        int realLen = (int) (fileLen - startPos);
        if (realLen < blockSize) {
            blockSize = realLen;
        }

        int blocks = realLen / blockSize;
        int lastBlockSize = realLen % blockSize;
        ArrayList<byte[]> resultList = new ArrayList<byte[]>();

        RandomAccessFile randomFile = null;
        try {
            byte[] buffer = null;
            int pos = startPos;

            randomFile = new RandomAccessFile(file, "r");
            for (int i = 0; i < blocks; i++) {
                buffer = new byte[blockSize];
                randomFile.seek(pos);
                randomFile.read(buffer);
                pos += blockSize;
                resultList.add(buffer);
            }
            if (lastBlockSize > 0 && pos < (fileLen - 1)) {
                buffer = new byte[lastBlockSize];
                randomFile.seek(pos);
                randomFile.read(buffer);
                resultList.add(buffer);
            }
            return resultList;
        } catch (Exception e) {
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static final byte[] readFileData(String filePath, long offset, int size) {
        if (TextUtils.isEmpty(filePath) || size <= 0) {
            return null;
        }
        File file = new DelFile(filePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        long fileLen = file.length();
        if (offset >= fileLen) {
            return null;
        }

        RandomAccessFile randomFile = null;
        try {
            byte[] buffer = new byte[size];
            randomFile = new RandomAccessFile(file, "r");
            randomFile.seek(offset);
            int read = randomFile.read(buffer);
            if (read > 0) {
                return buffer;
            }
        } catch (Exception e) {
            if (DrLog.DEBUG) DrLog.d("FileUtil", "exception: " + e.getMessage());

        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * 解压路径为zipFile的.zip文件到路径folderPath下， 如果有同名folderPath的文件，则删除该文件，新建为文件夹
     * 解压过程使用buffer大小为5k
     *
     * @param folderPath
     * @return
     * @throws ZipException
     * @throws IOException
     */
    public static boolean unZipFile(String zipFilePath, String folderPath) {
        // new ZipInputStream(null);
        if (!folderPath.endsWith("/")) {
            folderPath = folderPath + "/";
        }
        ZipFile zfile = null;
        OutputStream os = null;
        ZipInputStream is = null;
        try {
            File folder = new DelFile(folderPath);
            if (folder.exists() && !folder.isDirectory()) {
                deleteFile(folder, DEL_FILE_BY_CREATE_FOLDER);
            }
            if (!folder.exists()) {
                folder.mkdirs();
            }

            is = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry zipEntry;
            while ((zipEntry = is.getNextEntry()) != null) {
                String subfilename = zipEntry.getName();
                if (TextUtils.isEmpty(subfilename) || subfilename.startsWith("..")) continue;
                if (zipEntry.isDirectory()) {
                    File subDire = new DelFile(folderPath + subfilename);
                    if (subDire.exists() && subDire.isDirectory()) {
                        continue;
                    } else if (subDire.exists() && subDire.isFile()) {
                        deleteFile(subDire, DEL_FILE_BY_CREATE_FOLDER);
                    }
                    subDire.mkdirs();
                } else {
                    File subFile = new DelFile(folderPath + subfilename);
                    if (subFile.exists()) {
                        continue;
                    }
                    subFile.getParentFile().mkdirs();
                    subFile.createNewFile();
                    os = new FileOutputStream(subFile);
                    int len;
                    byte[] buffer = new byte[5120];
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                        os.flush();
                    }
                }
            }

        } catch (Exception e) {
            if (DrLog.DEBUG) DrLog.e("unzipfile exception: " + e.toString());
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (zfile != null) {
                try {
                    zfile.close();
                } catch (IOException e) {
                }
            }

        }
        return true;
    }

    /**
     * 写入新文件
     */
    public static void writeData(String path, byte[] data) {
        FileOutputStream fos = null;
        try {
            File file = new DelFile(path);
            if (!file.exists()) {
                createParentIfNecessary(path);
                fos = new FileOutputStream(file, false);
                fos.write(data);
                fos.flush();
            }
        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }


    public static void saveHashToCache(String savePath, String cacheStr) {
        if (TextUtils.isEmpty(cacheStr)) {
            return;
        }
        if (!FileUtil.isExist(savePath)) {
            FileUtil.createFile(savePath, FileUtil.MODE_UNCOVER);
        }
        FileUtil.rewriteData(savePath, cacheStr.getBytes());
    }


    /**
     * 保存文本内容
     *
     * @param path    保存路径
     * @param content 需要保存的文本内容
     */
    public static void writeData(String path, String content) {
        writeData(path, content.getBytes());
    }

    /**
     * 将byte[]写入文件
     *
     * @param filePath 格式如： /sdcard/abc/a.obj
     * @param content  写入内容byte[]
     * @return
     * @attention 当文件存在将被替换 当其所在目录不存在，将尝试创建
     */
    public static boolean writeFile(String filePath, byte[] content) {
        if (null == filePath || null == content) {
            if (DrLog.DEBUG)
                DrLog.e("Invalid param. filePath: " + filePath + ", content: " + content);
            return false;
        }

        FileOutputStream fos = null;
        try {
            String pth = filePath.substring(0, filePath.lastIndexOf("/"));
            File pf = null;
            pf = new DelFile(pth);
            //需要创建的文件filePath，所在的文件夹存在一个同名的文件，那么删除
            if (pf.exists() && !pf.isDirectory()) {
                FileUtil.deleteFile(pf, DEL_FILE_BY_CREATE_FOLDER);
            }
            pf = new DelFile(filePath);
            if (pf.exists()) {
                if (pf.isDirectory())
                    deleteDirectory(filePath);
                else
                    FileUtil.deleteFile(pf, DEL_FILE_BY_UNKNOW);
            }

            pf = new DelFile(pth + File.separator);
            if (!pf.exists()) {
                if (!pf.mkdirs()) {
                    if (DrLog.DEBUG) DrLog.e("Can't make dirs, path=" + pth);
                }
            }

            fos = new FileOutputStream(filePath);
            fos.write(content);
            fos.flush();
            fos.close();
            fos = null;
            pf.setLastModified(System.currentTimeMillis());

            return true;

        } catch (Exception ex) {
            if (DrLog.DEBUG) DrLog.e("Exception, ex: " + ex.toString());
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return false;
    }

    private FileUtil() {

    }

    /**
     * @param path
     * @return
     */
    public static byte[] getFileData(String path, int startPos) {
        byte[] data = null;// 返回的数据
        FileInputStream fis = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                data = new byte[(int) file.length() - startPos];
                fis = new FileInputStream(file);
                fis.skip(startPos);// parasoft-suppress PB.LOGIC.CRRV-1
                fis.read(data);// parasoft-suppress PB.LOGIC.CRRV-1
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static byte[] getFileData(String path, int startPos, int lengthAtMost) {
        byte[] data = null;// 返回的数据
        FileInputStream fis = null;
        try {
            File file = new DelFile(path);
            if (file.exists()) {
                int mostLength = (int) Math
                        .max(0, Math.min(lengthAtMost, file.length() - startPos));
                data = new byte[mostLength];
                fis = new FileInputStream(file);
                fis.skip(startPos);// parasoft-suppress PB.LOGIC.CRRV-1
                fis.read(data);// parasoft-suppress PB.LOGIC.CRRV-1
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static String replaceIllegalChar(String fileName) {

        Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|]");
        return fileName == null ? "_" : FilePattern.matcher(fileName).replaceAll("_");
    }


    /**
     * 描述: 配合rewriteData(String path, InputStream is, IContentBytes
     * contentBytes)函数使用，用来记录写了多少数据
     *
     * @author zhengsun
     * @since 2014年11月18日 下午9:57:48
     */
    public static interface onDataByteReceiver {
        /**
         * @param receive
         */
        public void onReceive(int receive);
    }

    /**
     * 获取sdka的根目录，这种方式比较全面
     *
     * @return
     */
    public static String getRootPath() {
        String rootPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            rootPath = Environment.getExternalStorageDirectory()// /mnt/sdcard0
                    .getAbsolutePath() + File.separator;
        } else if ((new DelFile("/mnt/sdcard2")).exists()) {// 以下为一些手机常见的SD卡路径，无法用以上方法获取
            rootPath = "/mnt/sdcard2" + File.separator;
        } else if ((new DelFile("/mnt/sdcard-ext")).exists()) {
            rootPath = "/mnt/sdcard-ext" + File.separator;
        } else if ((new DelFile("/mnt/ext_sdcard")).exists()) {
            rootPath = "/mnt/ext_sdcard" + File.separator;
        } else if ((new DelFile("/mnt/sdcard/SD_CARD")).exists()) {
            rootPath = "/mnt/sdcard/SD_CARD" + File.separator;
        } else if ((new DelFile("/mnt/sdcard/extra_sd")).exists()) {
            rootPath = "/mnt/sdcard/extra_sd" + File.separator;
        } else if ((new DelFile("/mnt/extrasd_bind")).exists()) {
            rootPath = "/mnt/extrasd_bind" + File.separator;
        } else if ((new DelFile("/mnt/sdcard/ext_sd")).exists()) {
            rootPath = "/mnt/sdcard/ext_sd" + File.separator;
        } else if ((new DelFile("/mnt/sdcard/external_SD")).exists()) {
            rootPath = "/mnt/sdcard/external_SD" + File.separator;
        } else if ((new DelFile("/storage/sdcard1")).exists()) {
            rootPath = "/storage/sdcard1" + File.separator;
        } else if ((new DelFile("/storage/extSdCard")).exists()) {
            rootPath = "/storage/extSdCard" + File.separator;
        }
        return rootPath;
    }

    /////////////////////////删除文件的工具方法/////////////////////////////
    public static final int DEL_FILE_BY_UNKNOW = 0; //未知
    public static final int DEL_FILE_BY_USER = 1;   //用户手动删除
    public static final int DEL_FILE_BY_UPGRADE = 2;//音质升级


    //A)删除同目录下需要创建的文件夹同名的文件，确保文件夹创建成功
    // ---这种情况有可能存在于文件夹损坏被认为是文件的情况，从而导致整个文件夹中的所有歌曲丢失，例如:kgmusic文件夹
    // ---也有可能真的存在同名的文件,并将其删除了
    //B)或者创建文件夹以cover模式创建时将原来的空文件夹删除
    // ---这种情况很奇怪但是代码逻辑中存在
    public static final int DEL_FILE_BY_CREATE_FOLDER = 3;

    public static final int DEL_FILE_BY_MOVE = 4;

    public static final int DEL_FILE_BY_UNCARE = 5;

    public static final int DEL_FILE_BY_SCAN = 6;

    public static final int DEL_FILE_BY_ERROR = 7;

    public static final int DEL_FILE_BY_UPDATE_ENCRYPT_CACHE = 8;

    // 下载引擎内部原因删除
    public static final int DEL_FILE_BY_ENCRYPTED = 90;

    public static final int DEL_FILE_BY_FORCE_DOWNLOAD = 91;

    public static final int DEL_FILE_BY_P2P_DIFFUSION = 92;


    //非文件删除的统计
    //将kgtmp或者kgt临时文件插入本地音乐
    public static final int BY_INSERT_TMP_LOCAL_ERROR = 100;


    /**
     * 文件删除的统一方法
     *
     * @param f
     * @param byWho 被谁删除,
     * @return
     */
    public static boolean deleteFile(File f, int byWho) {
        boolean ret = false;
        if (f != null) {
            ret = (f instanceof DelFile) ? ((DelFile) f).deleteNoCheck() : f.delete();
        }
        if (DrLog.DEBUG) DrLog.d("DeleteTips", "ret: " + ret);
        return ret;
    }

    public static boolean deleteNonMusicFile(File f) {
        boolean ret = false;
        if (f != null) {
            ret = (f instanceof DelFile) ? ((DelFile) f).deleteNoCheck() : f.delete();
        }
        return ret;
    }

    /**
     * 删除文件或文件夹(包括目录下的文件)
     *
     * @param filePath
     */
    public static boolean deleteFileOrFloder(String filePath, int byWho) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        try {
            File f = new DelFile(filePath);
            if (f.exists()) {
                if (f.isDirectory()) {
//                    File[] delFiles = f.listFiles();//vz1234--需要过滤
                    File[] delFiles = DelFileChecker.getInstance().listFilesNonKugouFolderMusic(f);
                    if (delFiles != null) {
                        for (int i = 0; i < delFiles.length; i++) {
                            return deleteFileOrFloder(delFiles[i].getAbsolutePath(), byWho);
                        }
                    }
                }
                return FileUtil.deleteFile(f, byWho);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * copy raw文件到sdcard卡中
     *
     * @param context
     * @param rawId
     * @param file
     */
    public static void copyRawFileToInternalStorage(Context context, int rawId, File file) {
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            is = context.getResources().openRawResource(rawId);
            byte[] buffer = new byte[4096];
            bos = new BufferedOutputStream(new FileOutputStream(file));
            int readByte;
            while ((readByte = is.read(buffer)) > 0) {
                bos.write(buffer, 0, readByte);
            }
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bos);
        }
    }

    /**
     * 获取文件的数据
     *
     * @param file
     * @return
     */
    public static byte[] getFileData(File file) {
        byte[] data = null;// 返回的数据
        FileInputStream fis = null;
        try {
            if (file != null && file.exists()) {
                data = new byte[(int) file.length()];
                fis = new FileInputStream(file);
                IOUtils.readAndIgnoreReturnValue(fis, data);
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return data;
    }

    /**
     * 把 srcPath 目录下的所有文件和文件夹，拷贝到 destPath 目录下
     * @param srcPath 源文件夹
     * @param destPath 目标文件夹
     */
    public static void copyDir(String srcPath, String destPath) {
        File src = new File(srcPath);
        File destFile = new File(destPath);
        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        File[] listFiles = src.listFiles();
        if (listFiles != null) {
            for (File s : listFiles) {
                if (s.isFile()) {
                    copyFile(s.getPath(), destPath + File.separator + s.getName());
                } else {
                    copyDir(s.getPath(), destPath + File.separator + s.getName());
                }
            }
        }
    }

    /**
     * @param fromFile 被复制的文件
     * @param toFile   复制的目录文件
     * @param rewrite  是否重新创建文件
     *
     *                 <p>文件的复制操作方法
     */
    public static boolean copyFile(File fromFile, File toFile, Boolean rewrite) {
        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;

        if (!fromFile.exists()) {
            return false;
        }

        if (!fromFile.isFile()) {
            return false;
        }
        if (!fromFile.canRead()) {
            return false;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }


        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭输入、输出流
            IOUtils.closeQuietly(fosfrom);
            IOUtils.closeQuietly(fosto);
        }
        return false;
    }

    public static void copyFile(String fromFile, String toFile, Boolean rewrite) {
        if (StringUtils.isEmpty(fromFile) || StringUtils.isEmpty(toFile)) {
            return;
        }
        copyFile(new File(fromFile), new File(toFile), rewrite);
    }

    /**
     * Android上不包含文件目录的单个文件名最长长度为255字节
     *
     * @param fileName
     * @return
     */
    public static boolean isFileNameTooLong(String fileName) {
        return fileName.getBytes().length >= 255;
    }

    public static void createFileWithByte(byte[] bytes, String fileName) {
        // TODO Auto-generated method stub
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File file = null;
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            file = new File(fileName);
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public static void copyFolderIfNeed(Context context, String folder) {
        try {
            String fileNames[] = context.getAssets().list(folder);
            for (int i = 0; fileNames != null && i < fileNames.length; i++)
                copyFolderIfNeed(context, folder, fileNames[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean copyFolderIfNeed(Context context, String folder, String fileName) {
        String path = getExternalFilesFilePath(context, folder + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) {
                //如果模型文件不存在
                try {
                    File folderFile = new File(getExternalFilesFilePath(context, folder));
                    if (!folderFile.exists()) {
                        folderFile.mkdirs();
                    }
                    if (file.exists())
                        file.delete();

                    file.createNewFile();
                    InputStream in = context.getApplicationContext().getAssets().open(folder + File.separator + fileName);
                    if (in == null) {
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static String getExternalFilesFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }
        return path;
    }

}
