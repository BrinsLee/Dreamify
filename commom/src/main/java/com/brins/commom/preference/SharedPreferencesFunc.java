package com.brins.commom.preference;

/**
 * Created by burone on 2017/11/28.
 */

public interface SharedPreferencesFunc {

    int DATA_INT     = 1;
    int DATA_LONG    = 2;
    int DATA_FLOAT   = 3;
    int DATA_BOOLEAN = 4;
    int DATA_STRING  = 5;

    String getString(String key, String defValue);

    int getInt(String key, int defValue);

    long getLong(String key, long defValue);

    float getFloat(String key, float defValue);

    boolean getBoolean(String key, boolean defValue);

    boolean contains(String key);

    boolean putString(String key, String value);

    boolean putInt(String key, int value);

    boolean putLong(String key, long value);

    boolean putFloat(String key, float value);

    boolean putBoolean(String key, boolean value);

    boolean remove(String key);

}
