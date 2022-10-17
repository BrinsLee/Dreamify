package com.brins.commom.utils;

import com.brins.commom.utils.log.DrLog;
import java.io.File;
import java.net.URI;

/**
 * Created by vectorzeng on 16/6/20.
 */
public class DelFile extends File {
    private final static String TG = "vz-DelFile";
    public DelFile(File dir, String name) {
        super(dir, name);
    }

    public DelFile(String path) {
        super(path); // parasoft-suppress BD.EXCEPT.NP-1
    }

    public DelFile(String dirPath, String name) {
        super(dirPath, name);
    }

    public DelFile(URI uri) {
        super(uri);
    }

    @Override
    @Deprecated
    public boolean delete() {
        return delete(FileUtil.DEL_FILE_BY_UNKNOW);
    }

    @Override
    @Deprecated
    public void deleteOnExit() {
        super.deleteOnExit();
        DelFileChecker.getInstance().check(this, FileUtil.DEL_FILE_BY_UNKNOW, true);
        if (DrLog.DEBUG) DrLog.e(TG, "deleteOnExit:" + getAbsolutePath());
    }

    //////////////////////////////
    public boolean delete(int byWho) {
        boolean ret = super.delete();
        DelFileChecker.getInstance().check(this, byWho, ret);
        return ret;
    }

    public boolean deleteNoCheck(){
        return super.delete();
    }

}
