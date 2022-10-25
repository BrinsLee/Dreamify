package com.brins.commom.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import com.brins.commom.font.CustomTypefaceSpan;
import com.brins.commom.font.SemiBoldFontManager;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.widget.VerticalImageSpan;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作类，如截取、合并等操作
 *
 * @author Chenys
 */
public class StringUtil {

	public static String mapToString(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (String key : map.keySet()) {
			String value = map.get(key);
			sb.append(key + "=" + value + "&");
		}
		String paramString = sb.toString();
		if (!TextUtils.isEmpty(paramString)) { // 删除最后一个多余"&"
			return paramString.substring(0, paramString.length() - 1);
		}
		return "";
	}

	public static String mapObjToString(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		for (String key : map.keySet()) {
			String value = String.valueOf(map.get(key));
			sb.append(key + "=" + value + "&");
		}
		String paramString = sb.toString();
		if (!TextUtils.isEmpty(paramString)) { // 删除最后一个多余"&"
			return paramString.substring(0, paramString.length() - 1);
		}
		return "";
	}

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 小于10的正整数前面补0
     *
     * @param i
     * @return
     */
    public static String add0IfLgTen(int i) {
        if (0 < i && i < 10) {
            return "0" + i + ".";
        } else {
            return i + ".";
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 统计字符串(汉字、数字和英文)中字节个数
     *
     * @param str
     * @return
     */
    public static int countWords(String str) {
        int len = 0;
        try {
            if (!TextUtils.isEmpty(str)) {
                len = str.getBytes("GBK").length;
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return len;
    }

    /**
     * EncodeMV的文件名，防止无法存储
     *
     * @param orgFileName
     * @return
     */
    public static String EncodeMvFileName(String orgFileName) {
        if (TextUtils.isEmpty(orgFileName)) {
            return "";
        }
        final char[] name = orgFileName.toCharArray();
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (char c : name) {
            char newc = getConvertChar(c);
            if (newc < 128) {
                count += 1;
            } else if (newc < 2048) {
                count += 2;
            } else {
                count += 3;
            }
            builder.append(newc);
            if (count > 200) {
                break;
            }
        }
        return builder.toString();

//        return orgFileName.replace("/", "／").replace("*", "×").replace("?", "？").replace("<", "＜")
//                .replace(">", "＞").replace(":", "：").replace("\"", "＂").replace("\\", "＼")
//                .replace("|", "｜");
    }

    private static char getConvertChar(char c) {
        switch (c) {
            case '/':
                return '／';
            case '*':
                return '×';
            case '?':
                return '？';
            case '<':
                return '＜';
            case '>':
                return '＞';
            case ':':
                return '：';
            case '\"':
                return '＂';
            case '\\':
                return '＼';
            case '|':
                return '｜';
            default:
                return c;
        }
    }

    /**
     * 对乱码进行转码
     *
     * @param s
     * @return
     */
    public static String errEncode(String s) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5\\u0800-\\u4e00]+");
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            try {
                return s = new String(s.getBytes("iso-8859-1"), // parasoft-suppress BD.EXCEPT.NP-1
                        "GBK");
            } catch (UnsupportedEncodingException e) {
            }
        }
        return s;
    }

    /**
     * 格式化文件路径（去除一些特殊字符）
     *
     * @param filePath
     * @return
     */
    public static String formatFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return filePath.replace("\\", "").replace("/", "").replace("*", "").replace("?", "")
                .replace(":", "").replace("\"", "").replace("<", "").replace(">", "")
                .replace("|", "");
    }

    public static String formatSize(long size) {
        long SIZE_KB = 1024;
        long SIZE_MB = SIZE_KB * 1024;
        long SIZE_GB = SIZE_MB * 1024;

        // if (size < SIZE_KB) {
        // return String.format("%dB", (int) size);
        // } else
        if (size < SIZE_MB) {
            return String.format("%.2fK", (float) size / SIZE_KB);
        } else if (size < SIZE_GB) {
            return String.format("%.2fM", (float) size / SIZE_MB);
        } else {
            return String.format("%.2fG", (float) size / SIZE_GB);
        }
    }

    public static String getCharSize(long size) {
        String charsize = "";
        double doublesize = 0d;
        DecimalFormat df = new DecimalFormat("#.0");
        if (size / (1024 * 1024 * 1024) >= 1) {
            doublesize = size / (1024 * 1024 * 1024d);
            charsize = df.format(doublesize) + "G";
        } else if (size / (1024 * 1024) >= 1) {
            doublesize = size / (1024 * 1024d);
            charsize = df.format(doublesize) + "M";
        } else {
            charsize = "0K";
            if (size != 0L) {
                doublesize = size / 1024d;
                charsize = df.format(doublesize) + "K";
            }
        }
        return charsize;
    }

    public static String getStringFilterNull(String src) {
        return null == src ? "" : src;
    }

    public static String getCutString(String string, int len) {
        if (TextUtils.isEmpty(string) || string.length() < len) {
            return string;
        }
        return string.substring(0, len - 1);
    }

    /**
     * 把秒转换成YYYY-MM-DD
     *
     * @param sec
     * @return
     */
    public static String getDateTimeBySecond(long sec) {
        Date date = new Date(sec * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(date);
        return time;
    }

    public static String getDistanceString(int distance) {
        if (distance < 1000) {
            return distance + "米";
        } else if (distance < 10 * 1000) {
            return distance / 1000 + "千米";
        } else {
            return "10千米以外";
        }
    }

    public static String getExceptionString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replace("\n", "<br />");
    }

    /**
     * 获取文件扩展名 有.
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
            return fileName.substring(index, fileName.length());
        }
    }

    /**
     * 解析文件所在的文件夹
     *
     * @param filePath 文件路径
     * @return 文件所在的文件夹路径
     */
    public static String getFileFolderPath(String filePath) {
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
     * 解析文件全名,不带扩展名
     *
     * @param filePath 文件路径
     * @return 文件全名
     */
    public static String getFileName(String filePath) {
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
     * 解析文件全名,不带扩展名
     * 有些filePath有问题，会出现"/"在"."前面一位，导致subString报StringIndexOutOfBoundsException
     *
     * @param filePath 文件路径
     * @return 文件全名
     */
    public static String getFileNameSafe(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int last = filePath.lastIndexOf("/");
        int index = filePath.lastIndexOf(".");
        if (last == -1 && index == -1) {
            return filePath;
        } else if (index <= last + 1) {
            return "";
        } else {
            return filePath.substring(last + 1, index);
        }
    }

    /**
     * 把文件分割为文件名和后缀名
     *
     * @param fileFullName 文件的全称
     * @return
     */
    public static String[] getFileNameAndExtName(String fileFullName) {
        if (fileFullName == null) {
            return null;
        }
        int indexDot = fileFullName.lastIndexOf(".");
        if (indexDot > 0) {
            String[] result = new String[2];
            String displayName = fileFullName.substring(0, indexDot);
            result[0] = displayName;
            result[1] = fileFullName.substring(indexDot + 1, fileFullName.length());
            return result;
        }
        return null;
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
        if (last == -1) {
            return filePath;
        } else {
            return filePath.substring(last + 1);
        }
    }

    /**
     * 从字符串中获取第1组符合正则表达式的内容
     *
     * @param regex
     * @param source
     * @return
     */
    public static String getMatcher(String regex, String source) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group(1);// 只取第一组
        }
        return result;
    }

    /**
     * 产生一个描写长度的随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random(System.currentTimeMillis());
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);// 0~61
            sbf.append(str.charAt(number));
        }
        return sbf.toString();
    }

    /**
     * 获取文件名，带后缀名
     *
     * @param filePath
     * @return
     */
    public static String getShortFileName(String filePath) {
        if (filePath == null) {
            return null;
        }
        if (filePath.indexOf("/") == -1) {
            return filePath;
        }
        int begin = filePath.lastIndexOf("/");
        return filePath.substring(begin + 1);
    }

    /**
     * 返回文件大小表示
     *
     * @param context
     * @param bytes 字节数
     * @return
     */
    public static String getSizeText(Context context, long bytes) {
        String sizeText = "";
        if (bytes < 0) {
            return sizeText;
        } else {
            sizeText = Formatter.formatFileSize(context, bytes);
        }
        return sizeText;

    }

    /**
     * 单位换算
     *
     * @param fileSize
     * @return
     */
    public static String getSizeText(long fileSize) {
        if (fileSize <= 0) {
            return "0M";
        }
        if (fileSize > 0 && fileSize < 1024 * 0.1) {
            float result = 0.1f;
            String suffix = "K";
            return String.format("%.1f", result) + suffix;
        }
        if (fileSize >= 1024 * 0.1 && fileSize < 1024 * 1024) {
            float result = fileSize;
            String suffix = "K";
            result = result / 1024;
            return String.format("%.1f", result) + suffix;
        }
        float result = fileSize;
        String suffix = "M";
        result = result / 1024 / 1024;
        return String.format("%.1f", result) + suffix;
    }

    /**
     * 少于1M显示单位为K
     *
     * @param fileSize
     * @return
     */
    public static String getSizeText2(long fileSize) {
        if (fileSize <= 1) {    //1字节忽略
            return "0M";
        }
        if (fileSize < 1024 * 1024) {
            float result = fileSize;
            String suffix = "K";
            result = result / 1024;
            return String.format("%.1f", result) + suffix;
        }
        return getSizeText(fileSize);
    }

    /**
     * 单位换算（G）
     *
     * @param fileSize
     * @return
     */
    public static String getSizeText3(long fileSize) {
        if (fileSize <= 0) {
            return "0M";
        }
        if (fileSize > 0 && fileSize < 1024 * 0.1) {
            float result = 0.1f;
            String suffix = "K";
            return String.format(Locale.ENGLISH, "%.1f", result) + suffix;
        }
        if (fileSize >= 1024 * 0.1 && fileSize < 1024 * 1024) {
            float result = fileSize;
            String suffix = "K";
            result = result / 1024;
            return String.format(Locale.ENGLISH, "%.1f", result) + suffix;
        }
        if (fileSize >= 1024 * 1024 * 0.1 && fileSize < 1024 * 1024 * 1024) {
            float result = fileSize;
            String suffix = "M";
            result = result / 1024 / 1024;
            return String.format(Locale.ENGLISH, "%.1f", result) + suffix;
        }
        float result = fileSize;
        String suffix = "G";
        result = result / 1024 / 1024 / 1024;
        return String.format(Locale.ENGLISH, "%.1f", result) + suffix;
    }

    public static String getTimeDiff(long standard, long time) {
        long timeDistance = standard - time;
        if (timeDistance < 60 * 60) {
            long temp = timeDistance / 60;
            if (temp == 0) {
                temp = 1;
            }
            if (temp <= 1) {
                return "刚刚";
            } else {
                String minute = String.valueOf(temp);
                return minute + "分钟前";
            }
        } else if (timeDistance >= 60 * 60 && timeDistance < 24 * 60 * 60) {
            String hour = String.valueOf(timeDistance / (60 * 60));
            return hour + "小时前";
        } else if (timeDistance >= 24 * 60 * 60 && timeDistance < 31 * 24 * 60 * 60) {
            String day = String.valueOf(timeDistance / (24 * 60 * 60));
            return day + "天前";
        } else if (timeDistance >= 31 * 24 * 60 * 60) {
            return "30天前";
        }
        return "刚刚";
    }

    public static String getTimeforDate(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        Date date = new Date();
        calendar.setTime(date);
        int taday = calendar.get(Calendar.DATE);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        String hourstr = String.valueOf(hour);
        if (hour == 0) {
            hourstr = "00";
        } else if (String.valueOf(hour).length() == 1) {
            hourstr = "0" + hour;
        }
        String minutesstr = String.valueOf(minutes);
        if (minutes == 0) {
            minutesstr = "00";
        } else if (String.valueOf(minutes).length() == 1) {
            minutesstr = "0" + minutes;
        }
        long timeDistance = (System.currentTimeMillis() - timeMillis) / 1000; // 秒
        if (timeDistance > 0 && timeDistance < 60 * 60) {
            long temp = timeDistance / 60;
            if (temp == 0) {
                temp = 1;
            }
            if (temp < 5 * 60) {
                return "刚刚";
            } else {
                String minute = String.valueOf(temp);
                return minute + "分钟前";
            }
        } else if (currentYear == year && month == currentMonth && day == taday) {
            return "今天" + hourstr + ":" + minutesstr;
        } else if (currentYear == year && month == currentMonth && day == taday - 1) {
            return "昨天" + hourstr + ":" + minutesstr;
        } else if (currentYear == year) {
            return month + "-" + day;
        } else {
            return year + "-" + month + "-" + day;
        }
    }

    /**
     * 处理格式yyyy-MM-dd HH:mm:ss的时间
     *
     * @param date
     * @return
     */
    public static String getTimeforDate(String date) {
        try {
            if (TextUtils.isEmpty(date)) {
                date = dateFormat.format(new Date());
            }
            Date dateTime = dateFormat.parse(date);
            return getTimeforDate(dateTime.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public static String getTimeString(long distance) {
        long time = System.currentTimeMillis() / 1000;
        long timeDistance = time - distance;
        if (timeDistance < 60 * 60) {
            long temp = timeDistance / 60;
            if (temp == 0) {
                temp = 1;
            }
            if (temp < 1) {
                return "刚刚";
            } else {
                String minute = String.valueOf(temp);
                return minute + "分钟前";
            }
        } else if (timeDistance >= 60 * 60 && timeDistance < 24 * 60 * 60) {
            String hour = String.valueOf(timeDistance / (60 * 60));
            return hour + "小时前";
        } else if (timeDistance >= 24 * 60 * 60 && timeDistance < 31 * 24 * 60 * 60) {
            String day = String.valueOf(timeDistance / (24 * 60 * 60));
            return day + "天前";
        } else if (timeDistance >= 31 * 24 * 60 * 60) {
            return "30天前";
        }
        return "刚刚";
    }

    /**
     * 通过图像地址和hash值获得由hash值和图片扩展名组成的一个文件名
     *
     * @param imageUrl
     * @param hash
     * @return
     */
    public static String hashImageName(String imageUrl, String hash) {
        if (TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(hash)) {
            return null;
        }
        imageUrl = imageUrl.toLowerCase();
        hash = hash.toLowerCase();

        int index = imageUrl.indexOf(".jpg");
        if (index == -1) {
            index = imageUrl.indexOf(".png");
        }
        String imageName = hash;
        if (index >= 0) {
            imageName += imageUrl.substring(index);
        }
        return imageName;
    }
    
    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s) || "null".equals(s);
    }

    /**
     * 判断字符串是否非空
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s) {
        return !TextUtils.isEmpty(s);
    }

    /**
     * 是否乱码
     *
     * @param s
     * @return true 乱码
     */
    public static boolean isErrCode(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
        Matcher matcher = pattern.matcher(s);
        return !matcher.find();
    }

    public static String loadStringFromRaw(Context context, int resId) throws Exception {
        InputStream in = context.getResources().openRawResource(resId);
        byte[] bytes = toByteArray(in);
        if (bytes == null) {
            return null;
        }
        return new String(bytes);
    }
    

    public static String splitFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        url = url.toLowerCase();
        int start = url.lastIndexOf("/");
        if (start == -1) {
            return null;
        }
        return url.substring(++start, url.length());
    }
    /**
     * 截取图片名称
     *
     * @param imageurl
     * @return
     */
    public static String spiltImageName(String imageurl) {
        if (TextUtils.isEmpty(imageurl)) {
            return null;
        }
        imageurl = imageurl.toLowerCase();
        int start = imageurl.lastIndexOf("filename");
        if (start == -1) {
            start = imageurl.lastIndexOf("/");
            if (start == -1) {
                return null;
            } else {
                start += 1;
            }
        } else {
            start += 9;
        }
        int end = imageurl.indexOf(".jpg", start);

        if (end == -1) {
            end = imageurl.indexOf(".gif", start);
        }
        if (end == -1) {
            end = imageurl.indexOf(".png", start);
            if (end == -1) {
                end = imageurl.indexOf(".jpeg", start);
                if (end == -1) {
                    return null;
                } else {
                    end += 5;
                }
            } else {
                end += 4;
            }
        } else {
            end += 4;
        }
        return imageurl.substring(start, end);
    }

    /**
     * 分割字符串
     *
     * @param src
     * @param delimiter
     * @return
     */
    public static String[] split(String src, String delimiter) {
        if (src == null || delimiter == null || src.trim().equals("")
                || delimiter.trim().equals("")) {
            return new String[] {
                src
            };
        }
        ArrayList<String> list = new ArrayList<String>();
        int lengthOfDelimiter = delimiter.length();
        int pos = 0;
        while (true) {
            if (pos < src.length()) {
                int indexOfDelimiter = src.indexOf(delimiter, pos);
                if (indexOfDelimiter < 0) {
                    list.add(src.substring(pos));
                    break;
                } else {
                    list.add(src.substring(pos, indexOfDelimiter));
                    pos = indexOfDelimiter + lengthOfDelimiter;
                }
            } else if (pos == src.length()) {
                list.add("");
                break;
            } else {
                break;
            }
        }
        String[] result = new String[list.size()];
        list.toArray(result);
        return result;
    }

    /**
     * 将input流转为byte数组，自动关闭
     *
     * @param input
     * @return
     */
    public static byte[] toByteArray(InputStream input) throws Exception {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = null;
        byte[] result = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 100];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str); // parasoft-suppress BD.EXCEPT.NP-1
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 字符串转长整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static long toLong(String str, long defValue) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
        }
        return defValue;
    }
    /**
     * 字符串转浮点形
     *
     * @param str
     * @param defValue
     * @return
     */
    public static float toFloat(String str, float defValue) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
        }
        return defValue;
    }
    /**
     * 将String转换为适合url使用的unicode
     *
     * @param src
     * @return
     */
    public static String toUnicode(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int length = src.length();
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(src.charAt(i));
            if (hex.length() <= 2) {
                builder.append("%").append(hex);
            } else {
                builder.append("%u").append(hex);
            }
        }
        return builder.toString();
    }

    public static String truncateUTF8String(String str) {
        final int DISPLAYNAME_MAX_COUNT = 200;
//        final boolean enabled = true;
//        if (enabled) {
//            return truncateUTF8String(str, DISPLAYNAME_MAX_COUNT);
//        } else {
            return str;
//        }
    }

    /** @hide */
    public static String truncateUTF8String(String str, int maxCount) {
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes();
        if (bytes.length <= maxCount) {
            return str;
        }
        int pos = maxCount;
        while (pos >= 0) {
            int v = bytes[pos] & 0xFF;
            if (v < 0x80 || v >= 0xC0) {
                break;
            }
            --pos;
        }
        byte[] newBytes = new byte[Math.max(0, pos)];
        System.arraycopy(bytes, 0, newBytes, 0, newBytes.length);
        return new String(newBytes);
    }


    /**
     * 获取默认的图片下载路径，已包含文件名
     *
     * @return
     */
    public static String getDefaultImagePath() {
        String savePath = GlobalEnv.IMAGE_OTHER_FOLDER + System.currentTimeMillis() + ".jpg";
        return savePath;
    }

    public static String getHDAlbumImagePath() {
        if (!GlobalEnv.IMAGE_OTHER_FOLDER.endsWith(File.separator)) {
            return GlobalEnv.IMAGE_OTHER_FOLDER + File.separator + System.currentTimeMillis() + ".jpg";
        } else {
            return GlobalEnv.IMAGE_OTHER_FOLDER + System.currentTimeMillis() + ".jpg";
        }
    }

    public static String getPlayListImagePath(String path, int size) {
        String savePath = "";
        if (size > 0) {
            savePath = GlobalEnv.IMAGE_THIRD_APPS_FOLDER
                    + (size + "_" + StringUtil.spiltImageName(path));
        } else {
            savePath = GlobalEnv.IMAGE_THIRD_APPS_FOLDER + StringUtil.spiltImageName(path);
        }
        return savePath;
    }

    public static String getCacheImagePath(String path) {
        String savePath = GlobalEnv.IMAGE_OTHER_FOLDER + StringUtil.spiltImageName(path) + ".jpg";
        return savePath;
    }


    /**unicode转utf字符串，如果失败返回源字符串
     * @param theUnicode
     * @return
     */
    public static String unicodeToUtf8(String theUnicode) {
        if(isEmpty(theUnicode)){
            return theUnicode;
        }
        char aChar;
        int len = theUnicode.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theUnicode.charAt(x++);
            if (aChar == '\\') {
                aChar = theUnicode.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theUnicode.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    public static String filterSongName(String displayName) {
        if (displayName == null)
            return null;
        if (displayName.endsWith(")") && displayName.lastIndexOf("(") != -1) {
            if (DrLog.DEBUG) DrLog.i("TIMON", "before:" + displayName);
            String subValue = displayName.substring(displayName.lastIndexOf("(") + 1,
                    displayName.length() - 1);
            if (isNumber(subValue)) {
                displayName = displayName.substring(0, displayName.lastIndexOf("("));
                if (DrLog.DEBUG) DrLog.i("TIMON", "after:" + displayName);
            }
        }else if (displayName.endsWith("[mqms]") || displayName.endsWith("[mqms2]")) {
            if (DrLog.DEBUG) DrLog.i("TIMON", "before:" + displayName);
            displayName = displayName.substring(0, displayName.lastIndexOf("["));
            if (DrLog.DEBUG) DrLog.i("TIMON", "after:" + displayName);
        }
        return displayName.trim();
    }

    public static String filterEmChar(String keyword){
        if (TextUtils.isEmpty(keyword)) {
            return "";
        }
        return keyword.replace("<em>","").replace("</em>","");
    }

    public static CharSequence replaceEmHighlight(String keyword,int highlightColor){
        if (TextUtils.isEmpty(keyword)) {
            return "";
        }
        return keyword.replaceAll("<em>", "<font color="+highlightColor+">").
                replaceAll("</em>", "</font>");
    }

    public static CharSequence replaceEmHighlightAndBreakLine(String keyword,int highlightColor){
        if (TextUtils.isEmpty(keyword)) {
            return "";
        }
        return keyword.replaceAll("<em>", "<font color="+highlightColor+">").
                replaceAll("</em>", "</font>").replaceAll("\r\n","<br/>").replaceAll("\n","<br/>");
    }

    public static boolean containsEmChar(String content){
        return !TextUtils.isEmpty(content) && content.contains("<em>") && content.contains("</em>");
    }

    public static boolean isNumber(String number) {
        if (number == null)
            return false;
        return number.matches("[+-]?[1-9]+[0-9]*(\\.[0-9]+)?");
    }

    /**
     * 判断是否符合手机号码格式
     *
     * @param str
     * @return
     */
    public static boolean isPhoneNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String regExp = "^1[3-9][0-9]{9}$";
        return str.matches(regExp);
    }

    /**
     * 获取字符限制的String，汉字算2个字符;
     * @param source 源
     * @param maxChar 最大字符数
     * @return
     */
    public static String getSubString(@NonNull String source, int maxChar) {
        int charCount = 0;
        int index = 0;

        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            charCount += isChinese(c) ? 2 : 1;
            if (charCount > maxChar) {
                break;
            }

            index = i + 1;
        }
        return source.substring(0, index);
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean compareString(String str1, String str2) {
        if (str1 != null)
            return str1.equals(str2);
        else
            return (str2 == null);
    }

    /**
     * 截取指定的字符串长度  支持中英文
     * @param target
     * @param maxLength  如果需要限制为8个中文或16个英文 则传8
     * @return
     */
    public static String substringSpecifiedLength(String target, int maxLength) {
        if (TextUtils.isEmpty(target)) {
            return target;
        }
        if (!TextUtils.isEmpty(target) && target.length() < maxLength + 1) {
            return target;
        } else {
            char[] chars = target.toCharArray();
            int length = 0;
            int position = 0;
            for (int j = 0; j < target.length(); j++) {
                if (isChinese(chars[j])) {
                    length += 2;
                } else {
                    length += 1;
                }
                if (length == maxLength * 2) {
                    position = j;
                    target = target.substring(0, position + 1);
                    break;
                }
                if (length == (maxLength * 2) + 1) {
                    position = j-1;
                    target = target.substring(0, position + 1);
                    break;
                }
            }
        }

        return target;
    }

    /**
     * 音乐短视频数量显示
     * */
    public static String formatCountForMusicVideo(int count) {
        if (count < 10000) {
            if (count < 0) {
                count = 0;
            }
            return String.valueOf(count);
        } else if (count >= 1000000) {
            return "99.9w+";
        } else {
            int tenTh = count / 1000;
            String formatted = String.valueOf(((double) tenTh) / 10);
            if (formatted.endsWith(".0") && formatted.length() > 2) {
                formatted = formatted.substring(0, formatted.length() - 2);
            }
            if(formatted.length() > 4){
                formatted = formatted.substring(0, formatted.length() - 2);
            }

            return formatted + "w";
        }
    }

    /**
     * 评论 阅读 讨论等数量显示规则format
     * @param count
     * @return
     */
    public static String formatCount(int count) {
        if (count < 10000) {
            if (count < 0) {
                count = 0;
            }
            return String.valueOf(count);
        } else if (count >= 9990000) {
            return "999万";
        } else {
            int tenTh = count / 1000;
            String formatted = String.valueOf(((double) tenTh) / 10);
            if (formatted.endsWith(".0") && formatted.length() > 2) {
                formatted = formatted.substring(0, formatted.length() - 2);
            }
            if(formatted.length() > 4){
                formatted = formatted.substring(0, formatted.length() - 2);
            }

            return formatted + "万";
        }
    }

    public static String formatCountWithOneDecimal(int number) {
        String viewer = "";
        if (number >= 10000000) {
            viewer = "999+万";
        } else if (number > 10000) {
            viewer = String.format("%.1f", number / 10000.0) + "万";
        } else {
            return String.valueOf(number);
        }
        return viewer;
    }

    public static String formatCount(int count, String tt) {
        if (count < 10000) {
            if (count < 0) {
                count = 0;
            }
            return String.valueOf(count);
        } else if (count >= 9990000) {
            return "999" + tt;
        } else {
            int tenTh = count / 1000;
            String formatted = String.valueOf(((double) tenTh) / 10);
            if (formatted.endsWith(".0") && formatted.length() > 2) {
                formatted = formatted.substring(0, formatted.length() - 2);
            }
            if(formatted.length() > 4){
                formatted = formatted.substring(0, formatted.length() - 2);
            }

            return formatted + tt;
        }
    }

    /**
     * @param count
     * @return
     */
    public static String formatCountByTenThousand(long count) {
        return formatCountByTenThousand(count, false);
    }

    /**
     * @param count
     * @return
     */
    public static String formatCountByTenThousand(long count, boolean showZero) {
        if (count <= 0) {
            if (showZero && count == 0) {
                return "0";
            }
            return "";
        } else if (count <= 999) {
            return String.valueOf(count);
        } else if (count <= 9999) {
            return "999+";
        } else if (count < 1000000) {
            String formatted = String.valueOf((int) Math.floor(count * 1f / 10000));
            return formatted + "w";
        } else {
            return "99w+";
        }
    }
    /**
     * @param count
     * @return
     */
    public static String formatFansCountByTenThousand(long count, boolean showZero) {
        if (count <= 0) {
            if (showZero && count == 0) {
                return "0";
            }
            return "";
        } else if (count <= 9999) {
            return String.valueOf(count);
        }  else if (count < 1000000) {
            String formatted = String.valueOf((int) Math.floor(count * 1f / 10000));
            return formatted + "w";
        } else {
            return "99w+";
        }
    }

    /**
     * @param count
     * @return
     */
    public static String formatFavsCountByTenThousand(@IntRange(from = Long.MIN_VALUE) long count, boolean showZero) {
        if (count <= 0) {
            if (showZero && count == 0) {
                return "0";
            }
            return "";
        } else if (count <= 999) {
            return String.valueOf(count);
        }  else if(count <= 10000){
            return "999+";
        }
        else if (count < 10000000) {
            String formatted = String.valueOf((int) Math.floor(count * 1f / 10000));
            return formatted + "w+";
        } else {
            return "999w+";
        }
    }

    /**
     * @param count
     * @return
     */
    public static String formatCountByTenThousand(long count, String zeroShow) {
        if (count <= 0) {
            return zeroShow;
        }
        String countStr;
        if (count < 10000 && count >= 0) {
            countStr = String.valueOf(count);
        } else {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            countStr = df.format(count / 10000.0) + "w";
        }
        return countStr;
    }

    public static String upperCase(String text) {
        if (text == null) {
            return text;
        }
        return text.toUpperCase();
    }

    public static String lowerCase(String text) {
        if (text == null) {
            return text;
        }
        return text.toLowerCase();
    }

    public static boolean containChinese(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param stringBuilder 为了避免多次调用，重复创建大量对象。
     * @param timeMs 单位:ms
     * @return 1:00:00
     */
    public static String stringForTime(@Nullable StringBuilder stringBuilder, int timeMs) {
        if (stringBuilder == null) stringBuilder = new StringBuilder();

        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        stringBuilder.delete(0, stringBuilder.length());
        if (hours > 0) {
            stringBuilder.append(hours);
            stringBuilder.append(":");
        }

        if (minutes > 9) {
            stringBuilder.append(minutes);
        } else {
            stringBuilder.append(0);
            stringBuilder.append(minutes);
        }

        stringBuilder.append(":");

        if (seconds > 9) {
            stringBuilder.append(seconds);
        } else {
            stringBuilder.append(0);
            stringBuilder.append(seconds);
        }

        return stringBuilder.toString();
    }

    public static String setImageUrlSize(String imageUrl,int size){
        if (!StringUtil.isEmpty(imageUrl) && imageUrl.contains("{size}")) {
            imageUrl = imageUrl.replace("{size}", String.valueOf(size));
        }
        return imageUrl;
    }

    public static String wipeImageUrlSize(String imageUrl){
        if (!StringUtil.isEmpty(imageUrl) && imageUrl.contains("{size}/")) {
            imageUrl = imageUrl.replace("{size}/", "");
        }
        return imageUrl;
    }

    public static String getTrimString(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return text.trim();
    }

    public static String getNoneNullString(String text){
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return text;
    }

    public static String formatAudienceNumber(long count) {
        String countStr;
        if (DrLog.DEBUG) DrLog.d("channelAudience", count + "");
        if (count < 10000 && count >= 0) {
            countStr = String.valueOf(count);
        } else {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.DOWN);
            countStr = df.format(count / 10000.0) + "万";
        }
        if (DrLog.DEBUG) DrLog.d("channelAudience", countStr);
        return countStr;
    }

    public static SpannableString getIconSpannableString(Context context, Bitmap bitmap) {
        SpannableString spanForNewTag = new SpannableString(" icon");
        if (bitmap != null && !bitmap.isRecycled()) {
            VerticalImageSpan imgSpan = new VerticalImageSpan(context, bitmap);
            spanForNewTag.setSpan(imgSpan, 1, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanForNewTag;
    }

    public static String[] disposeSongDisplayName(String displayName) {
        String singerName = null;
        String songName = null;
        int index1 = displayName.indexOf(" - ");
        int subStartIndex = index1 + 3;
        if (index1 == -1) {
            index1 = displayName.indexOf("-");
            subStartIndex = index1 + 1;
        }
        int index2 = displayName.indexOf(".");
        if (index1 > 0) {
            if (index2 > index1) {
                songName = displayName.substring(subStartIndex, index2).trim();
            } else {
                songName = displayName.substring(subStartIndex, displayName.length()).trim();
            }
        }
        if (index1 > 0) {
            singerName = displayName.substring(0, index1).trim();
        }
        String[] s = {singerName, songName};
        return s;
    }

    /**
     * 关键字高亮显示
     *
     * @param text    需要显示的文字
     * @param target  需要高亮的关键字
     * @param color   高亮颜色
     * @param start   头部增加高亮文字个数
     * @param end     尾部增加高亮文字个数
     * @return 处理完后的结果
     */
    public static SpannableString highlight( String text, String target,
                                            String color, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        Pattern pattern = Pattern.compile(target);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
            spannableString.setSpan(span, matcher.start() - start, matcher.end() + end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public static SpannableString highlight( String text, String target,
                                             int color, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        Pattern pattern = Pattern.compile(target);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            spannableString.setSpan(span, matcher.start() - start, matcher.end() + end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public static String formatDuring(int mss) {
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / (60);
        long seconds = mss % 60;
        String result = "";
        if (hours != 0) {
            result = hours + "时";
        }
        if (minutes != 0) {
            result = result + minutes + "分";
        }

        return result + seconds + "秒";
    }
    public static boolean isEnglish(String source) {
        String regex = "[^A-Za-z]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(source);
        return !m.find();
    }

    /**
     * 匹配字符串中是否含有中文字符
     *
     * @param source
     * @return
     */
    public static boolean isChinese(String source){
        String regex = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(source);
        return m.find();
    }

    public static String getMapString(Map<String, Object> oriHashtable) {
        if (oriHashtable != null) {
            Map.Entry<String, Object>[] entries = mapComparator(oriHashtable);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < entries.length; i++) {
                Map.Entry<String, Object> entry = entries[i];
                sb.append(entry.getKey() + "=" + entry.getValue());
            }
            return sb.toString();
        }
        return "";
    }

    @NonNull
    public static Map.Entry<String, Object>[] mapComparator(Map<String, Object> oriHashtable) {
        Set<Map.Entry<String, Object>> set = oriHashtable.entrySet();
        Map.Entry<String, Object>[] entries = set.toArray(new Map.Entry[set.size()]);
        Arrays.sort(entries, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> lhs, Map.Entry<String, Object> rhs) {
                return lhs.getKey().compareTo(rhs.getKey());
            }
        });
        return entries;
    }

    public static String getSignParms(Map<String, String> params) {
        if (params != null && params.size() >= 0) {
            StringBuilder builder = new StringBuilder();
            Set<String> sets = params.keySet();
            String[] entries = sets.toArray(new String[sets.size()]);
            Arrays.sort(entries, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            for (String key : entries) {
                builder.append(key).append("=").append(params.get(key)).append("&");
            }
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        } else {
            return "";
        }
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 判断是否纯空白字符串
     * @param s
     * @return
     */
    public static boolean isWhitespace(String s){
        if(TextUtils.isEmpty(s)){
           return true;
        }
        char[] chars = s.toCharArray();
        for (char c :
                chars) {

            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    public static CharSequence typeface(String text, int start, int end) {
        return typeface(text, SemiBoldFontManager.getInstance().getFontType(), start, end);
    }

    public static CharSequence typeface(String text, Typeface typeface, int start, int end) {
        if (TextUtils.isEmpty(text) || start < 0 || end > text.length()) {
            return text;
        }
        SpannableString spannableString = new SpannableString(text);
        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);
        spannableString.setSpan(typefaceSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 匹配字段并修改 颜色和大小
     * @param str
     * @param match
     * @param color
     * @param textSize
     * @return
     */
    public static CharSequence highLight(String str, String match, int color, int textSize) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(match)) {
            return str;
        }
        int start = str.indexOf(match);
        if (start < 0) {
            return str;
        }
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(span, start, start + match.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AbsoluteSizeSpan(textSize), start, start + match.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 匹配字符串中是否只有英文以及通配符
     *
     * @param source
     * @return
     */
    public static boolean isEnglishAndWildChard(String source){
        String regex = "[^\\w\\s~`!@#$%^&*()-_+=|\\{}<>,.;:'\"/s￥…]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(source);
        return !m.find();
    }

    public static String changeToUrlString(Map<String, String> data){
        if (data==null||data.isEmpty()){
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> item : data.entrySet()) {
            builder.append(item.getKey()).append("=").append(item.getValue()).append("&");
        }

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String buildStrFromAry(String[] strings,String split){
        if(strings == null || strings.length == 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if(i != strings.length-1){
                sb.append(split);
            }
        }
        return sb.toString();
    }

    public static String mapToString(Map map, String splitKV, String splitPair) {
        StringBuilder sb = new StringBuilder();
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            sb.append(key.toString() + splitKV + value.toString() + splitPair);
        }
        String paramString = sb.toString();
        if (!TextUtils.isEmpty(paramString)) { // 删除最后一个多余"&"
            return paramString.substring(0, paramString.length() - 1);
        }
        return "";
    }

    public static String iterableToString(Iterable list) {
        return iterableToString(list, ",");
    }

    public static String iterableToString(Iterable list, String separator) {
        if (list == null) {
            return "";
        }
        if (separator == null) {
            separator = ",";
        }
        StringBuilder sb = new StringBuilder();
        for (Object id : list) {
            sb.append(id).append(separator);
        }
        String ids;
        if (sb.length() > 0) {
            ids = sb.substring(0, sb.length() - 1);
        } else {
            ids = sb.toString();
        }
        return ids;
    }

    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }

        List<String> list = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    private static String substring(String str, int f, int t) {
        if (f > str.length()) {
            return "";
        }
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    public static boolean equalsIgnoreCase(String s, String s1) {
        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(s1)) {
            return false;
        }
        return TextUtils.equals(s.toLowerCase(), s1.toLowerCase());
    }


    public static String nonnullStr(Object obj, String defaultStr) {
        if (obj != null) {
            return obj.toString();
        }
        return defaultStr;
    }

    public static String checkEmpty(String value, String def) {
        return TextUtils.isEmpty(value) ? def : value;
    }

    public static boolean isNotBlankOrZero(String text) {
        return !TextUtils.isEmpty(text) && !"0".equals(text);
    }

    public static boolean isBlankOrZero(String text) {
        return TextUtils.isEmpty(text) || "0".equals(text);
    }

    public static int parseColorSafe(String color, @IntRange(from = Integer.MIN_VALUE) int defaultColor) {
        if (isNotEmpty(color)) {
            try {
                return Color.parseColor(color);
            } catch (Throwable ignored) {
            }
        }
        return defaultColor;
    }
}
