
package com.brins.commom.utils;

import android.text.TextUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public class CheckUtils {

    public static boolean isAvailable(Collection list) {
        return list != null && !list.isEmpty();
    }

    public static <T> boolean isAvailable(T[] menus) {
        return menus != null && menus.length != 0;
    }

    public static boolean isAvailable(Map map) {
        return map != null && !map.isEmpty();
    }

    public static boolean isAvailable(String str) {
        return !TextUtils.isEmpty(str);
    }

    public static boolean isAvailable(CharSequence str) {
        return !TextUtils.isEmpty(str);
    }

    public static boolean isAvailable(List list, int i) {
        return isAvailable(list) && list.size() > i;
    }

    public static boolean isAvailableV2(List list, int pos) {
        return isAvailable(list) && pos >= 0 && list.size() > pos;
    }

    public static boolean isAvailable(JSONArray jsonArray) {
        return jsonArray != null && jsonArray.length() > 0;
    }
}
