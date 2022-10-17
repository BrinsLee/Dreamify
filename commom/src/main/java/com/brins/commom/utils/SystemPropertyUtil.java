package com.brins.commom.utils;

import com.brins.commom.utils.log.DrLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by xfengZhang
 * data 2020/3/3 16:00
 * email xfengv@yeah.net
 * 读取系统属性工具类(可能为耗时操作)
 */
public class SystemPropertyUtil {
    /**
     * 使用命令方式读取系统属性
     * @param propName 属性名称(各个厂商与机型有不同的属性,注意甄别)
     * @return
     */
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            if(DrLog.isDebug()){
                DrLog.w("SystemPropertyUtil","Unable to read sysprop " + propName+ex);
            }
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    if(DrLog.isDebug()){
                        DrLog.w("SystemPropertyUtil", "Exception while closing InputStream"+e);
                    }
                }
            }
        }
        return line;
    }

    /**
     * 读取系统属性，装载至Properties
     * 全部属性,如必要,建议写值文件
     * @return
     */
    public static Properties getProperty() {
        Properties properties = new Properties();
        try {
            Process p = Runtime.getRuntime().exec("getprop");
            properties.load(p.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
