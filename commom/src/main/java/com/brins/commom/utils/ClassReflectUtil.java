/**
 * 
 */

package com.brins.commom.utils;

import com.brins.commom.utils.log.DrLog;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 类反射工具类
 * 
 * @author liaodongmingRabbit
 */
public class ClassReflectUtil {
    private static final String TAG = "ClassReflectUtil";

    /**
     * @param className 需要反射类的名字
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    public static Class creatClassObject(String className) throws ClassNotFoundException {
        Class cls = Class.forName(className);
        return cls;
    }

    /**
     * 得到系统属性key值对应的value值
     * 
     * @param cls 反射类
     * @param key 系统属性key值
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getSystemProperties(Class cls, String key) {
        String value = null;
        try {
            Method hideMethod = cls.getMethod("get", String.class);
            Object object = cls.newInstance();
            value = (String) hideMethod.invoke(object, key);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 查看反射类的 所有方法和常量
     * 
     * @param cls
     */
    @SuppressWarnings("rawtypes")
    static public void printMethod(Class cls) {
        Method[] hideMethod = cls.getMethods();
        // 取得所有方法
        int i = 0;
        for (i = 0; i < hideMethod.length; i++) {
            if (DrLog.DEBUG) DrLog.d(TAG, hideMethod[i].getName().toString());
        }
        // 取得所有常量
        Field[] allFields = cls.getFields();
        if (DrLog.DEBUG) {
            for (i = 0; i < allFields.length; i++) {
                DrLog.d(TAG, allFields[i].getName());
            }
        }
    }

    /**
     * @param cls 反射类
     * @param key 系统属性key值
     * @param val key对应的value值
     */
    public static void setSystemProperties(Class cls, String key, String val) {
        try {
            Method hideMethod = cls.getMethod("set", new Class[] {
                    String.class, String.class
            });
            Object object = cls.newInstance();
            hideMethod.invoke(object, new Object[] {
                    new String(key), new String(val)
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static <E> E callWithDefault(Object target, String methodName, E defaultValue) {
        try {
            return (E) target.getClass().getMethod(methodName, (Class[]) null).invoke(target);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        return defaultValue;
    }

    public static void setBoolean(Object target, String fieldName, boolean value) {
        try {
            target.getClass().getField(fieldName).setBoolean(target, value);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

    public static Object tryInvoke(Object target, String methodName, Class<?>[] argTypes,
            Object... args) {
        try {
            return target.getClass().getMethod(methodName, argTypes).invoke(target, args);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        return null;
    }

    public static Object tryInvoke(Object target, String methodName, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        return tryInvoke(target, methodName, argTypes, args);
    }
}
