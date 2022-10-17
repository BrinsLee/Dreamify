package com.brins.commom.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.worker.Instruction;
import com.brins.commom.worker.WorkScheduler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vectorzeng on 16/6/20.
 */
public class DelFileChecker {
    private final static String TG = "vz-DelFileChecker";
    private static volatile DelFileChecker ourInstance = new DelFileChecker();

    public static DelFileChecker getInstance() {
        return ourInstance;
    }

    private DelFileChecker() {
        initListFilter();
    }
    

    public String getStackMsg(){
        return "Empty";
    }
    
    

    private final String[] EXT_SONG_FILE ={".mp3",".m4a",".wma",
            ".ogg",".aac",".wav",".ape",".flac",".m4r",".amr"};
    

    public boolean isMusicFile(File f){
        return f != null && isMusicFile(f.getName());
    }

    public boolean isMusicFile(String pathOrFileName){
        if(!TextUtils.isEmpty(pathOrFileName)) {
            for (String str : EXT_SONG_FILE) {
                if (pathOrFileName.endsWith(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通过文件全路径md5生成唯一的文件名
     * @param
     * @return
     */
/*    public static String getFileNameByAbsPath(String filePath){
        String ret = MD5Util.sGetMD5OfStr(filePath.toLowerCase());
        if(!TextUtils.isEmpty(ret)){
            ret += ".log";
        }
        return ret;
    }*/

    public static void saveToFile(String folderPath, String fileName, String content, boolean append){
        if(TextUtils.isEmpty(folderPath)
                || TextUtils.isEmpty(fileName)
                || TextUtils.isEmpty(content)){
            return;
        }
        File floder = new File(folderPath);
        if (!floder.exists()) {
            floder.mkdirs();
        }
        File file = new File(floder, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            byte[] data = content.getBytes("UTF-8");
            fos.write(data);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String readLogFile(File f){
        StringBuilder ret = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = br.readLine();
            while (line != null){
                ret.append(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            if (DrLog.DEBUG) DrLog.e(TG, "readLogFile error " + e);
            return null;
        } catch (IOException e) {
            if (DrLog.DEBUG) DrLog.e(TG, "readLogFile error " + e);
            return null;
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret.toString();
    }

    ///////补充删除文件的额外信息///

    public void recordDelFileExtraMsg(String path, String msg, boolean needCallStack, boolean recordImmediately){
        recordDelFileExtraMsg(new File(path), msg, needCallStack, recordImmediately);
    }

    /**
     *  注意该方法被调用所在的进程，请确保recordDelFileExtraMsg()调用在f.delete之前
     * @param f     被删除的文件
     * @param msg   记录的信息
     * @param needCallStack 是否需要记录当前堆栈信息
     * @param recordImmediately 是否立即保存到文件，还是通过handler来保存。recordDelFileExtraMsg()在前台进程，f.delete在后台进程，那么recordImmediately 最好是true
     */
    public void recordDelFileExtraMsg(File f, String msg, boolean needCallStack, boolean recordImmediately){
//        if(!isPicked()){
//            return ;
//        }
//        if((!TextUtils.isEmpty(msg)||needCallStack) && f != null && isMusicFile(f)){
//            RuntimeException re = null;
//            if(needCallStack){
//                re = new RuntimeException();
//            }
//            ExtraMsgBean b = new ExtraMsgBean(re, f, msg);
//            if(recordImmediately) {
//                onRecordDelFileExtraMsg(b);
//            }else{
//                mHandler.obtainMessage(MSG_DEL_FILE_EXTRA_MSG, b).sendToTarget();
//            }
//        }
    }

    ///////以下是下载引擎相关代码///////
    private SparseArray<String> mHolderCacheDirs = null;
    private SparseArray<String> mHolderDirs = null;
    public void setDownloadEngineInfo(SparseArray<String> holderCacheDirs, SparseArray<String> holderDirs){
        mHolderCacheDirs = holderCacheDirs;
        mHolderDirs = holderDirs;
    }

    private String getDownloadInfo(){
        String info = "";
        try {
            if (mHolderCacheDirs != null) {
                info += "\n mHolderCacheDirs: " + mHolderCacheDirs.toString();
            }
            if (mHolderDirs != null) {
                info += "\n mHolderDirs: " + mHolderDirs.toString();
            }
        }catch (Exception e){
            info += "\ngetDownloadInfo exception " + e.toString();
        }
        return info;
    }

    //成功删除文件
    private static final int MSG_DEL_FILE_SUCCESSED = 1;
    //记录删除文件的信息
    private static final int MSG_DEL_FILE_EXTRA_MSG = 2;
    static class MyHandler extends WorkScheduler {
        public MyHandler(String name) {
            super(name);
        }

        @Override
        public void handleInstruction(Instruction msg) {

        }
    }

    static class Bean{
        //该Exception只是用于获取堆栈信息
        RuntimeException callStackException;
        File f;
        int byWho;
        boolean successed;
        boolean forceRecord;//强制记录并上发，无需调用：needRecordFile()过滤

        public Bean(RuntimeException callStackException, File f, int byWho, boolean successed, boolean forceRecord) {
            this.callStackException = callStackException;
            this.f = f;
            this.byWho = byWho;
            this.successed = successed;
            this.forceRecord = forceRecord;
        }
    }

    static class ExtraMsgBean{
        //该Exception只是用于获取堆栈信息
        RuntimeException callStackException;
        File f;
        String msg;

        public ExtraMsgBean(RuntimeException callStackException, File f, String msg) {
            this.callStackException = callStackException;
            this.f = f;
            this.msg = msg;
        }
    }

    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 歌曲下载目标目录相关的处理 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    public static File newStandbyDownloadFile(File oldFile){
        File parent = oldFile.getParentFile();
        File newFile;
        for( int i = 0; i < 10; i++) {
            newFile = new DelFile(parent, "dl_" + i);
            if (!newFile.exists()||(newFile.isDirectory()&&newFile.canWrite())) {//not exits or is directory can be used
                return newFile;
            }
        }
        return null;
    }
    //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 歌曲下载目标目录相关的处理 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑



    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 安全清理文件夹 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    private final HashMap<String,String[]> mMapFilter = new HashMap<>();
    private void initListFilter(){

    }

    /**
     *
     * @param folder 必须是文件夹，不能是文件，调用者，请在调用前确保他是个文件夹
     * @return null 在mMapFilter没有的文件夹,
     *  正常情况下将返回：1）mMapFilter中的指定的文件类型 2）没有后缀的文件,文件夹
     *  没有在mMapFilter中配置的文件夹，默认返回所有文件
     */
    @Deprecated
    private FilenameFilter getSafeFilter(File folder){
        String key;
        String path = folder.getAbsolutePath();
        int index = path.indexOf("kugou/");
        if (DrLog.DEBUG) DrLog.i(TG, "path: " + path + ", index " + index);
        if(index > 0){
            key = path.substring(index-1);
            final String[] suffixs = mMapFilter.get(key);
            if(suffixs != null) {
                if (DrLog.DEBUG) DrLog.i(TG, "path: " + path + ", suffixsx " + suffixs);
                return new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        for (String s : suffixs) {
                            //endWidth效率比Pattern要高 http://carver.iteye.com/blog/847490
                            boolean ret = filename.endsWith(s)
                                    ||filename.endsWith(".ver")
                                    ||filename.indexOf('.') <= 0;//如果index==0说明是隐藏文件或者文件夹，直接返回.没有后缀的也直接返回//test
                            if (ret) {
                                if (DrLog.DEBUG) DrLog.e(TG, "return true:" + dir + filename);
                            }else{
                                if (DrLog.DEBUG) DrLog.e(TG, "s = " + s + ", filename = " + filename + ", dir " + dir);
                            }
                            return ret;
                        }
                        return false;
                    }
                };
            }
        }
        return null;
    }

    /**
     * 获取 可以滤掉kugou文件夹下的音频文件的 filter
     * @param folder
     * @return
     */
    private FilenameFilter getNonNusicFilterOfKugou(File folder){
        String path = folder.getAbsolutePath();
        int index = path.indexOf("kugou");
        if(index > 0) {
            if ('/' == path.charAt(index - 1)){
                return new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String filename) {
                        boolean ret = !isMusicFile(filename);
                        return ret;
                    }
                };
            }
        }
        return null;
    }

    /**
     * 枚举folder的所有文件，如果是kugou目录下的文件夹，那么将会过滤掉音频文
     * @param folder you must ensure @floder is to directory,must not be null
     * @return
     */
    @Nullable
    public File[] listFilesNonKugouFolderMusic(File folder){
        FilenameFilter filter = getNonNusicFilterOfKugou(folder);
        if(filter != null){
            return folder.listFiles(filter);
        }else{
            return folder.listFiles();
        }
    }

    /**
     * test
     * @param path
     * @return
     */
    public File[] listFilesNonKugouFolderMusic(String path){
        if (!TextUtils.isEmpty(path)) {
            File file = new DelFile(path);
            if (file.exists() && file.isDirectory()) {
                return listFilesNonKugouFolderMusic(file);
            }
        }
        return null;
    }

    /**
     * 删除文件夹内所有文件 目前仅用于删除 私聊群聊语音文件 因为不需要过滤音频文件
     * @param folder
     */
    public File[] listAllFiles(File folder){
        FilenameFilter filter = getSafeFilter(folder);
        if(filter != null){
            return folder.listFiles(filter);
        }else{
            return folder.listFiles();
        }
    }

    /**
     * 删除文件夹内所有文件 目前仅用于删除 私聊群聊语音文件 因为不需要过滤音频文件
     * @param path
     */
    public File[] listAllFiles(String path){
        if (!TextUtils.isEmpty(path)) {
            File file = new DelFile(path);
            if (file.exists() && file.isDirectory()) {
                return listAllFiles(file);
            }
        }
        return null;
    }

    /**
     * 枚举folder的所有文件，如果是kugou目录下的文件夹，那么将会过滤掉音频文件
     * @param folder you must ensure @floder is to directory,must not be null
     * @return
     */
    public String[] listBySafe(File folder){
        FilenameFilter filter = getNonNusicFilterOfKugou(folder);
        if(filter != null){
            return folder.list(filter);
        }else{
            return folder.list();
        }
    }

    //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 安全清理文件夹↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}
