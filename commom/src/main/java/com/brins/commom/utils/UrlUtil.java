package com.brins.commom.utils;

import android.text.TextUtils;
import android.webkit.URLUtil;
import java.net.URL;

/**
 * @author lipeilin
 * @date 2022/10/17
 * @desc
 */
public class UrlUtil {

    private static final String baseImageUrl = "http://img.acsing.kugou.com/";

    public static String getHostByUrl(String url) {
        try {
            URL u = new URL(url);
            return u.getHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String combineUrls(String pre, String suf) {
        if(!TextUtils.isEmpty(pre) && !TextUtils.isEmpty(suf)) {
            if(suf.startsWith("/v2")) {
                suf = suf.substring(3);
            }

            StringBuilder sb = new StringBuilder(pre);
            String separator = "/";
            if(pre.endsWith(separator) && suf.startsWith(separator)) {
                sb.append(suf.substring(1));
            } else if(pre.endsWith(separator) && !suf.startsWith(separator)) {
                sb.append(suf);
            } else if(!pre.endsWith(separator) && suf.startsWith(separator)) {
                sb.append(suf);
            } else if(!pre.endsWith(separator) && !suf.startsWith(separator)) {
                sb.append(separator).append(suf);
            }
            return sb.toString();
        }
        return suf;
    }

    public static String makeKtvImageUrl(String fileName) {
        if(TextUtils.isEmpty(fileName) || fileName.startsWith("http")){
            return fileName;
        }
        String url = combineUrls(baseImageUrl, fileName);
        if(illeagalUrl(url)) {
            return "";
        }
        return url;
    }

    public static boolean illeagalUrl(String url) {
        if(!URLUtil.isValidUrl(url)) {
            return true;
        }
        return false;
    }

}

