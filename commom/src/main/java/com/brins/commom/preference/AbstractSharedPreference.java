package com.brins.commom.preference;

import android.content.Context;
import com.brins.commom.app.DRCommonApplication;

import static com.brins.commom.preference.SharedPreferencesFunc.*;

/**
 * @author lipeilin
 * @date 2022/10/17
 * @desc
 */
public abstract class AbstractSharedPreference implements SharedPreferencesFunc {

    protected Context context;

    private String name;

    private boolean cacheOpen;

    protected AbstractSharedPreference(String name) {
        this.context = DRCommonApplication.getContext();
        this.name = name;
        this.cacheOpen = true;
        SharedPreferencesForeProcess.getInstance().setNameOfFile(name);
    }

    private int getDataType(Object value) {
        int type;
        if (value instanceof Integer) {
            type = DATA_INT;
        } else if (value instanceof Long) {
            type = DATA_LONG;
        } else if (value instanceof Float) {
            type = DATA_FLOAT;
        } else if (value instanceof Boolean) {
            type = DATA_BOOLEAN;
        } else {
            type = DATA_STRING;
        }
        return type;
    }

    @Override public String getString(String key, String defValue) {
        return SharedPreferencesForeProcess.getInstance().getString(key, defValue);
    }

    @Override public int getInt(String key, int defValue) {
        return SharedPreferencesForeProcess.getInstance().getInt(key, defValue);
    }

    @Override public long getLong(String key, long defValue) {
        return SharedPreferencesForeProcess.getInstance().getLong(key, defValue);
    }

    @Override public float getFloat(String key, float defValue) {
        return SharedPreferencesForeProcess.getInstance().getFloat(key, defValue);
    }

    @Override public boolean getBoolean(String key, boolean defValue) {
        return SharedPreferencesForeProcess.getInstance().getBoolean(key, defValue);
    }

    @Override public boolean contains(String key) {
        return SharedPreferencesForeProcess.getInstance().contains(key);
    }

    @Override public boolean putString(String key, String value) {
        return false;
    }

    @Override public boolean putInt(String key, int value) {
        return false;
    }

    @Override public boolean putLong(String key, long value) {
        return false;
    }

    @Override public boolean putFloat(String key, float value) {
        return false;
    }

    @Override public boolean putBoolean(String key, boolean value) {
        return false;
    }

    @Override public boolean remove(String key) {
        return false;
    }

    /*************** 支持资源ID ******************/

    public String getString(int resId, String defValue) {
        return getString(context.getString(resId), defValue);
    }

    public int getInt(int resId, int defValue) {
        return getInt(context.getString(resId), defValue);
    }

    public float getFloat(int resId, float defValue) {
        return getFloat(context.getString(resId), defValue);
    }

    public boolean getBoolean(int resId, boolean defValue) {
        return getBoolean(context.getString(resId), defValue);
    }

    public long getLong(int resId, long defValue) {
        return getLong(context.getString(resId), defValue);
    }

    public boolean putInt(int resId, int value) {
        return putInt(context.getString(resId), value);
    }

    public boolean putFloat(int resId, float value) {
        return putFloat(context.getString(resId), value);
    }

    public boolean putBoolean(int resId, boolean value) {
        return putBoolean(context.getString(resId), value);
    }

    public boolean putString(int resId, String value) {
        return putString(context.getString(resId), value);
    }

    public boolean putLong(int resId, long value) {
        return putLong(context.getString(resId), value);
    }
}
