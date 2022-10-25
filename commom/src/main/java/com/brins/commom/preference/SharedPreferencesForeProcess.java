package com.brins.commom.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.log.DrLog;

/**
 * 前台进程专用sharedpreferences
 * @author vectorzeng
 *
 */
public class SharedPreferencesForeProcess {
    private final static String TG = "SharedPreferencesDelegate";
    private final Context mAppContext;

    public String getNameOfFile() {
        return mNameOfFile;
    }

    public void setNameOfFile(String mNameOfFile) {
        this.mNameOfFile = mNameOfFile;
    }

    private String mNameOfFile = NAME_FILE_PREFERENCE;
    private final int mMode;

    private SharedPreferencesForeProcess(Context c, String file, int mode){
        mAppContext = c.getApplicationContext();
        mNameOfFile = file;
        mMode = mode;
    }

    private SharedPreferences getSharedPreferences() {
        /**
         * 由于ContextImpl.getSharedPreferences(),并不会每次都new 一个SharedPreferences,
         * 而是会通过数组，将该引用存储起来，如果已经new过，那么会直接使用。并不会再new。
         * 所以这里并不需要考虑，每次调用导致，浪费内存
         */
        if(TextUtils.isEmpty(mNameOfFile)){
            if (DrLog.DEBUG) DrLog.e(TG + "::getSharedPreferences", "error mNameOfFile " + mNameOfFile + " mode " + mMode);
            return null;
        }
        return mAppContext.getSharedPreferences(mNameOfFile, mMode);
    }

    public boolean putString(String key, String value){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.edit().putString(key, value).commit();
        }
        return false;
    }

    public void putStringApply(String key, String value){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            pre.edit().putString(key, value).apply();
        }
    }


    public String getString(String key, String defValue){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.getString(key, defValue);
        }
        return defValue;
    }

    public boolean putBoolean(String key, boolean value){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.edit().putBoolean(key, value).commit();
        }
        return false;
    }

    public float getFloat(String key, float defValue){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.getFloat(key, defValue);
        }
        return defValue;
    }

    public boolean putFloat(String key, float value){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.edit().putFloat(key, value).commit();
        }
        return false;
    }

    public boolean getBoolean(String key, boolean defValue){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.getBoolean(key, defValue);
        }
        return defValue;
    }

    public boolean putInt(String key, int value){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.edit().putInt(key, value).commit();
        }
        return false;
    }

    public int getInt(String key, int defaultValue){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean putLong(String key, long value){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.edit().putLong(key, value).commit();
        }
        return false;
    }

    public long getLong(String key, long defaultValue){
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    ///////////////////////////////////////
    /**
     * 文件名
     */
    final public static String NAME_FILE_PREFERENCE = "fore_process";
    final public static String KEY_IS_FIRST_START = "key_is_first_start";
    final public static String APP_STATE = "app_state";

    public static final String KEY_HAS_SHOW_SELECT_SKIN_SUCCESS = "has_show_select_skin_success";

    /**
     * app的versioncode
     */
    final public static String KEY_APP_VERSION_CODE = "key_app_version_code";

    private static volatile SharedPreferencesForeProcess mInstance;

    public static SharedPreferencesForeProcess getInstance(){
        if(mInstance == null){
            mInstance = new SharedPreferencesForeProcess(DRCommonApplication.getContext(),
                    NAME_FILE_PREFERENCE, Context.MODE_PRIVATE); // DRCommonApplication.isForeProcess()?1:4
            //当mode为4时，前台进程put完之后，立即退出酷狗时，put*()返回true但实际没有成功写入的情况
            //顾这里，前台进程使用1，后天进程由于只读，所以使用4
        }
        return mInstance;
    }


    public static boolean setIsFirstStartApp(boolean value){
       return getInstance().putBoolean(KEY_IS_FIRST_START, value);
    }

    public static boolean isFirstStartApp(boolean defaultValue){
        return getInstance().getBoolean(KEY_IS_FIRST_START, defaultValue);
    }

    public static int getAppVersionCode(int defaultVaule){
        return getInstance().getInt(KEY_APP_VERSION_CODE, defaultVaule);
    }

    public static boolean setAppVersionCode(int value){
        return getInstance().putInt(KEY_APP_VERSION_CODE, value);
    }


    public static boolean hasShowConfigSkin(){
        return getInstance().getBoolean(KEY_HAS_SHOW_SELECT_SKIN_SUCCESS,false);
    }

    public static boolean setShowConfigSkin(boolean isShowed){
        return getInstance().putBoolean(KEY_HAS_SHOW_SELECT_SKIN_SUCCESS,isShowed);
    }


    /**
     * 保存软件使用状态
     *
     * @param value 1为启动状态，2为正在使用状态，3为正在退出状态，0为正常状态（默认)
     */
    public static boolean saveAppState(int value) {
        return getInstance().putInt(APP_STATE, value);
    }

    public static int getAppState() {
        return getInstance().getInt(APP_STATE, 0);
    }

    private static final String FRAGMENT_TRANSACTION_TRACE_ENABLE = "fragment_transaction_trace_enable";

    public static boolean isFragmentTransactionTraceEnable() {
        return getInstance().getBoolean(FRAGMENT_TRANSACTION_TRACE_ENABLE, false);
    }

    public static void setFragmentTransactionTraceEnable(final boolean value) {
        getInstance().putBoolean(FRAGMENT_TRANSACTION_TRACE_ENABLE, value);
    }

    /**
     * 引导页是否正在展示（页面恢复时引导页可能盖在登录页上）
     */
    final static String IS_SPLASH_SHOWING = "IS_SPLASH_SHOWING";


    public static boolean isSplashShowing(){
        return getInstance().getBoolean(IS_SPLASH_SHOWING,false);
    }

    public static boolean setSplashShowing(boolean show){
        return getInstance().putBoolean(IS_SPLASH_SHOWING, show);
    }

    public boolean contains(String key) {
        SharedPreferences pre = getSharedPreferences();
        if(pre != null){
            return pre.contains(key);
        }
        return false;
    }
}
