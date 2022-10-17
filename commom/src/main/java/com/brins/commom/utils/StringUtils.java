/*
 * Copyright (c) 2014. kugou.com
 */

package com.brins.commom.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Formatter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Created by LeonLee on 2014/9/4 17:21.</p>
 * <p><a href="mailto:liwenlong1@kugou.net">Email:liwenlong1@kugou.net</a></p>
 *
 * 字符工具处理类
 *
 * <ul>
 *     对网络操作相关
 *     <li>{@link #errEncode(String)} 对乱码进行转码</li>
 *     <li>{@link #isErrCode(String)} 是否乱码</li>
 * </ul>
 * <ul>
 *     对字符处理操作(合并、分割)
 *     <li>{@link #split(String, String)} 分割字符串</li>
 *     <li>{@link #subStrByByteLen(String, String, int, boolean)} 按字节截取字符串</li>
 *     <li>{@link #splitString(String, String)} 把|分隔的字符串转化成 字符串List</li>
 *     <li>{@link #mergeString(List, String)} 把string list 转化为 | 分隔的字符串</li>
 * </ul>
 * <ul>
 *     对字符格式化相关
 *     <li>{@link #add0IfLgTen(int)} 小于10的正整数前面补0</li>
 *     <li>{@link #getSizeText(long)} 单位换算</li>
 *     <li>{@link #getSizeText(Context, long)} 返回文件大小表示</li>
 *     <li>{@link #formatTime(DateFormat, long)} 格式化时间</li>
 * </ul>
 * <ul>
 *     对字符获取与判断相关
 *     <li>{@link #getRandomString(int)} 产生一个描写长度的随机字符串</li>
 *     <li>{@link #bytesToHex(byte[])} 将byte数组转换为16进制</li>
 *     <li>{@link #isEmpty(CharSequence)} 判断字符串是否为空</li>
 *     <li>{@link #versionOver(String, String)} 判断两个版本的高低</li>
 *     <li>{@link #countWords(String)} 统计字符串(汉字、数字和英文)中字节个数</li>
 *     <li>{@link #loadRawRes(Context, int)} 加载Raw里的资源，并转为字符串</li>
 *     <li>{@link #getExceptionString(Exception)} 将Exception转换成字符串</li>
 * </ul>
 * <ul>
 *     对文件相关的操作
 *     <li>{@link #formatFilePath(String)} 格式化文件路径（去除一些特殊字符）</li>
 *     <li>{@link #spiltImageName(String)} 截取图片名称</li>
 *     <li>{@link #hashImageName(String, String)} 通过图像地址和hash值获得由hash值和图片扩展名组成的一个文件名</li>
 * </ul>
 * */
public class StringUtils {

    private StringUtils() {}

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
     * 生成GUID字符串号
     * @return
     */
    public static String makeGUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

	/**
	 * 分割字符串
	 *
	 * @param src
	 * @param delimiter
	 * @return
	 */
	public static String[] split(String src, String delimiter) {
		if (src == null || delimiter == null || src.trim().equals("") || delimiter.trim().equals("")) {
			return new String[] { src };
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
	 * 修正连接使其符合网络协议
	 *
	 * @param url
	 * @return
	 */
/*	public static String modifyUrl(String url) {
		if (url == null)
			return url;
		String enc = "UTF-8";
		StringBuffer strBuffer = new StringBuffer();
		for (int i = 0; i < url.length(); i++) {
			char c = url.charAt(i);
			if (c == '\\')
				c = '/';
			if (c > 256 || c == ' ' || c == '[' || c == ']' || c == '.' || c == '(' || c == ')') {
				strBuffer.append(UrlEncoderUtil.encode("" + c, enc));
			} else {
				strBuffer.append(c);
			}
		}
		return strBuffer.toString();
	}*/

	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
	 *
	 * @return int 得到的字符串长度
	 */
	public static double getLength(String s) {
		if(TextUtils.isEmpty(s)){
			return 0;
		}
		double valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < s.length(); i++) {
			// 获取一个字符
			String temp = s.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		//进位取整
		return Math.ceil(valueLength);
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
				return s = new String(s.getBytes("iso-8859-1"), "GBK");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return s;
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

	/**
	 * 单位换算
	 *
	 * @param fileSize
	 * @return
	 */
	public static String getSizeText(long fileSize) {
		if (fileSize <= 0) {
			return "0.0M";
		}
		if (fileSize > 0 && fileSize < 100 * 1024) {
			// 大于0小于100K时，直接返回“0.1M”（产品需求）
			return "0.1M";
		}
		float result = fileSize;
		String suffix = "M";
		result = result / 1024 / 1024;
		return String.format("%.1f", result) + suffix;
	}

	/**
	 * 返回文件大小表示
	 *
	 * @param context
	 * @param bytes
	 *            字节数
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
			end = imageurl.indexOf(".png", start);
			if (end == -1) {
				return null;
			} else {
				end += 4;
			}
		} else {
			end += 4;
		}
		return imageurl.substring(start, end);
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
		return hash + imageUrl.substring(index);
	}

    /**
     * 将Exception转换成字符串
     * */
	public static String getExceptionString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString().replace("\n", "<br />");
	}

	/**
	 * 把IMEI用一大整数代替
	 *
	 * @param imei
	 * @return
	 */
/*	public static BigInteger imeiTolong(String imei) {
		BigInteger result = new BigInteger("0");
		try {
			BigInteger n = new BigInteger("16");
			String md5 = MD5Util.getMd5(imei);
			int size = md5.length();// parasoft-suppress BD.EXCEPT.NP-1
			for (int i = 0; i < size; i++) {
				BigInteger a = new BigInteger("" + md5.charAt(i), 16);
				BigInteger b = n.pow(size - 1 - i);
				result = result.add(a.multiply(b));
			}
			return result;
		} catch (Exception e) {
		}
		return result;
	}*/

	/**
	 * <p>按字节截取字符串。</p> 按照指定的有效编码格式，指定的字节长度，以及截断方向(右截断/左截断)。截取后不产生乱码。<br>
	 * 返回的字符串的字节长度将小于等于指定长度。可能为空字符串。<br>
	 *
	 * @param original
	 *            原字符串
	 * @param charsetName
	 *            编码格式名
	 * @param byteLen
	 *            字节长度
	 * @param isRightTruncation
	 *            是否右截断。
	 * @return String
	 * @throws UnsupportedEncodingException
	 * @author leo_soul
	 */
	public static String subStrByByteLen(String original, String charsetName, int byteLen, boolean isRightTruncation) throws UnsupportedEncodingException {
		if (original == null || "".equals(original.trim()))
			return "";
		if (charsetName == null || "".equals(charsetName))
			throw new UnsupportedEncodingException("subStrByByteLen方法，必须指定编码格式");
		byte[] bytes = original.getBytes(charsetName);
		if (byteLen <= 0)
			return "";
		if (byteLen >= bytes.length)
			return original;

		int tempLen = 0;
		String result = "";
		if (isRightTruncation) {
			//右截断
			//按照指定字节长度截断，再转成临时String，计算长度。
			tempLen = new String(bytes, 0, byteLen, charsetName).length();
			//根据该长度右截取原字符串。
			result = original.substring(0, tempLen);
			//超过预订字节长度，则去掉一个字符。
			if (result != null && !"".equals(result.trim()) && result.getBytes(charsetName).length > byteLen)
				result = original.substring(0, tempLen - 1);
		} else {
			//左截断
			//全字符长-左截预订点(注意必须是预定点，bytes.length-byteLen+1)所右截断的串的字符长+1，计算长度。(为了给左截串多留一个字符。)
			//tempLen = original.length()-new String(bytes,0,bytes.length-byteLen+1,charsetName).length()+1;
			//根据该长度左截取原字符串。注意起始下标计算方法。
			//result = original.substring(original.length()-tempLen);
			//由于以上公式可以展开，由此得到简化版。
			tempLen = new String(bytes, 0, bytes.length - byteLen + 1, charsetName).length() - 1;
			result = original.substring(tempLen);
			//超过预订字节长度，则去掉一个字符(左截)。
			if (result != null && !"".equals(result.trim()) && result.getBytes(charsetName).length > byteLen)
				result = original.substring(tempLen + 1);
		}
		return result;
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
     * 加载Raw里的资源，并转为字符串
     * @param context 上下文
     * @param resId raw资源Id
     * @throws Exception
     * */
	public static String loadRawRes(Context context, int resId) throws Exception {
		InputStream in = context.getResources().openRawResource(resId);
		return new String(IOUtils.toByteArray(in));// parasoft-suppress BD.EXCEPT.NP-1
	}

    /**
     * 判断两个版本的高低
     * @param v1
     * @param v2
     * */
	public static boolean versionOver(String v1, String v2) {
		if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2))
			return false;
		if (v1.startsWith("v"))
			v1 = v1.substring(1);
		if (v2.startsWith("v"))
			v2 = v2.substring(1);
		String[] vs1 = v1.split("\\.");
		String[] vs2 = v2.split("\\.");
		int len = vs1.length;
		len = len < vs2.length ? len : vs2.length;
		for (int i = 0; i < len; i++) {
			try {
				int vi1 = Integer.parseInt(vs1[i]);
				int vi2 = Integer.parseInt(vs2[i]);
				if (vi1 < vi2)
					return false;
				else if (vi1 > vi2)
					return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		if (vs1.length < vs2.length)
			return false;
		return true;
	}

    /**
     * 格式化时间
     * @param dateFormat
     * @param time
     * @return
     */
    public static String formatTime(DateFormat dateFormat, long time) {
        // 如果时间只有10位，转化为android时间
        if(String.valueOf(time).length() < 13) {
            time *= 1000;
        }
        return dateFormat.format(new Date(time));
    }

	/**
	 *  把|分隔的字符串转化成 字符串List
	 *
	 * @return
	 */
	public static ArrayList<String> splitString(String strNameLst,String split) {
		if (TextUtils.isEmpty(strNameLst)) {
			return null;
		}
		String[] array = StringUtils.split(strNameLst, split);
		if (array == null || array.length == 0) {
			return null;
		}
		ArrayList<String> temp = new ArrayList<String>();
		for (String string : array) {
			temp.add(string);
		}
		return temp;
	}

	/**
	 * 把string list 转化为 | 分隔的字符串
	 * @param stringLst
	 * @return
	 */
	public static String mergeString(List<String> stringLst,String split) {
		StringBuilder sb = new StringBuilder();
		int len = stringLst.size();
		for (int i = 0; i < len; i++) {
			sb.append(stringLst.get(i));
			if (i < (len - 1)) {
				sb.append(split);
			}
		}
		return sb.toString();
	}

    /**
     * 将byte数组转换为16进制
     * @param bytes
     * */
    public static String bytesToHex(byte[] bytes) {
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
     * 判断字符串是否为空
     * @param str 要判断的字符串
     * */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
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
        return filePath.replace("\\", "").replace("/", "").replace("*", "").replace("?", "").replace(":", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "");
    }

    /**
     * 一个散列方法,改变一个字符串(如URL)到一个散列适合使用作为一个磁盘文件名。
     */
/*    public static String hashKeyForDisk(String key) {
        if(TextUtils.isEmpty(key)) {
            return key;
        }
        String cacheKey = MD5Util.getMd5(key);
        if(cacheKey == null) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }*/

	/**
	 * 根据长度折叠字符串
	 * @param s
	 * @param length
     * @return
     */
	public static String getFoldStringByLength(String s, int length) {
		if(TextUtils.isEmpty(s) || length == 0) {
			return "";
		}
		if (length >= s.length()) {
			return s;
		}
		return s.substring(0, length) + "…";
	}

	/**
	 * 获取指定长度的字串，中文占2个字符,英文数字占1个字符
	 *
	 * @param src 指定的字符串
	 * @param maxLength 最大长度
	 * @return 字符串的长度
	 */
	public static String substring(String src, int maxLength) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < src.length(); i++) {
			// 获取一个字符
			String temp = src.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				// 中文字符长度为2
				valueLength += 2;
			} else {
				// 其他字符长度为1
				valueLength += 1;
			}
			if (valueLength >= maxLength) {
				return src.substring(0, i+1);
			}
		}
		return src;
	}

	/**
	 * 获取指定长度的字串，中文占2个字符,英文数字占1个字符
	 * 超过部分用...代替
	 * @param src 指定的字符串
	 * @param maxLength 最大长度
	 * @param addDot 是否使用...代替超出部分
	 * @return 字符串的长度
	 */
	public static String substring(String src, int maxLength, boolean addDot) {
		String result = substring(src, maxLength);
		if (!result.equals(src)) {
			return result + "...";
		}
		return src;
	}


	/**
	 * 两个流是否来自同一个地址，如果有?号，则将?号后面的去掉，得到地址
	 * @return
     */
	public static boolean isFromSameAddress(String stream1, String stream2){
		if (stream1 == null || stream2 == null
				|| stream1.length()==0 || stream2.length() == 0){
			return false;
		}
		int index1 = stream1.indexOf("?");
		int index2 = stream2.indexOf("?");
		String addr1 = stream1;
		String addr2 = stream2;
		if (index1 != 0){
			addr1 = stream1.substring(0,index1);
		}
		if (index2 != 0){
			addr2 = stream2.substring(0,index2);
		}
		return addr1.equals(addr2);
	}
	/**
	 * 字符串转整数
	 *
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static long toLong(String str, int defValue) {
		try {
			return Long.parseLong(str); // parasoft-suppress BD.EXCEPT.NP-1
		} catch (Exception e) {
		}
		return defValue;
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
}
