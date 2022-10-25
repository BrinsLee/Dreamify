package com.brins.commom.utils.log;

import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import com.brins.commom.constant.GlobalStorageManager;

import static android.util.Log.e;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc
 */
public class DrLog {

    private static final String TAG = "DrLog";
    private static boolean isDebug = true;

    public static boolean DEBUG = isDebug;

    public static final String LOG_FOLDER = GlobalStorageManager.getExternalStorageRootDir() + "/dream/log/";

    public static final String LOG_FILENAME = "dream.log";

    private static String mLogFilePath = LOG_FOLDER + LOG_FILENAME;

    private static int logCount = 0;

    public static volatile long endTime;

    public static boolean isEncrypPassword = true;

    /**
     * 是否开启logcat日志－－－iLF(tag,msg),eLF(tag,msg)...
     * 请不要在其他代码里动态改变这个值。这样将会影响到其他人的日志。
     */
    private static boolean mIsLogcat = false;

    /**
     * 是否开启文件日志－－－iLF(tag,msg),eLF(tag,msg)...
     * 请不要在其他代码里动态改变这个值。这样将会影响到其他人的日志。
     */
    private static boolean mIsLogFile=false;

    public static void d(String msg) {
        if (mIsLogcat && !TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
            //writeAllLog(msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mIsLogcat) {
            Log.d(tag, msg+""+ getCurLineForJump(4));//msg为null时会报java.lang.NullPointerException: println needs a message
            //writeAllLog(msg);
        }
    }

    public static void d(String tag, String message, Object... args) {
        if (mIsLogcat) {
            String s = (message == null) ? "" : String.format(message, args);
            Log.d(tag, s + getCurLineForJump(4));
            //writeAllLog(s);
        }
    }

    public static void v(String tag, String msg) {
        if (mIsLogcat) {
            Log.v(tag, msg + "" + getCurLineForJump(4));
            //writeAllLog(msg);
        }
    }

    public static void v(String tag, String message, Object... args) {
        if (mIsLogcat) {
            String msg = (message == null) ? "" : String.format(message, args);
            Log.v(tag, msg + getCurLineForJump(4));
            //writeAllLog(msg);
        }
    }

    public static void s(String tag) {
        if (isDebug) {
            iLFCurrentStack(tag);
        }
    }

    public static void s(String tag, String msg) {
        if (isDebug) {
            e(tag, msg);
            iLFCurrentStack(tag);
        }
    }

    public static void sFull(String tag, String msg) {
        if (isDebug) {
            e(tag, msg);
            iLFCurrentFullStack(tag, 3);
        }
    }

    public static void e(String tag, String msg) {
        if (mIsLogcat) {
            Log.e(tag, msg == null ? "" : msg + getCurLineForJump(4));
            //writeAllLog(msg);
        }

    }

    public static void w(String tag, String msg) {
        if (mIsLogcat) {
            Log.w(tag, msg == null ? "" : msg + getCurLineForJump(4));
            //writeAllLog(msg);
        }
    }

    public static void w(String tag, String message, Object... args) {
        if (mIsLogcat) {
            String msg = (message == null) ? "" : String.format(message, args);
            Log.w(tag, msg + getCurLineForJump(4));
            //writeAllLog(msg);
        }
    }

    public static void w(Exception e) {
        printException(e);
    }

    public static void e(String msg) {
        if (msg == null) msg = "log msg value is null";
        if(mIsLogcat) {
            Log.e(TAG, msg);
        }

    }

    public static void e(Throwable e) {
        if (isDebug) {
            DrLog.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void e(String tag, Throwable e) {
        if (isDebug) {
            DrLog.e(tag, Log.getStackTraceString(e));
        }
    }

    public static void e(String tag, Throwable e, String message, Object... args) {
        if (isDebug) {
            String s = (message == null) ? "" : String.format(message, args);
            DrLog.e(tag, s + Log.getStackTraceString(e));
        }
    }


    public static void i(String msg) {
        if (mIsLogcat) {
            Log.i(TAG, msg);
            //writeAllLog(msg);
        }

    }

    public static void i(String tag, String msg) {
        if (mIsLogcat) {
            Log.i(tag, msg == null ? "" : msg + getCurLineForJump(4));
        }
    }

    public static void i(String tag, String message, Object... args) {
        if (mIsLogcat) {
            String s = (message == null) ? "" : String.format(message, args);
            Log.i(tag, s + getCurLineForJump(4));
        }
    }

    private static String getCurLineForJump(int precount) {
        /*if (!SHOWLINE) {
            return "";
        }*/
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && precount >= 0 && precount < stackTrace.length) {
            return "\n==> at " + stackTrace[precount]
                //               待优化     + (precount + 1 < stackTrace.length ? "\n==> at " + stackTrace[precount + 1] : "")
                ;
        }
        return "";
    }

    public static void printException(String tag, Throwable e) {
        if (isDebug) {
            DrLog.w(tag, Log.getStackTraceString(e));
        }
    }

    public static void printException(Throwable e) {
        printException(TAG, e);
    }

    /**
     * 是否处于调试模式
     *
     */
    public static boolean isDebug() {
        return isDebug;
    }


    /**
     * 打印：当前堆栈信息
     * @param tag
     */
    public static void iLFCurrentStack(String tag){
        iLFCurrentStack(tag, null);
    }

    private static void iLFCurrentStack(String tag, String stack) {
        if (mIsLogcat) {
            if (stack == null) {
                stack = getStack();
            }
            splitLogI(tag, stack);
            //            Log.i(tag, stack);
        }
        if (mIsLogFile) {
            if (stack == null) {
                stack = getStack();
            }

            //FileLogDelegate.i(tag, stack);
        }
    }

    @WorkerThread
    public static void iLFCurrentFullStack(String tag, int discardAhead) {
        /*if (Looper.getMainLooper() == Looper.myLooper()) {
            // 真的被误放到主线程调用，那就帮调用者换到工作线程吧
            iLFCurrentFullStackAsync(tag, " log force switched to worker thread", discardAhead);
            return;
        }

        String stack = null;
        if (StackTraceManager.isEnabled()) {
            stack = StackTraceManager.get().getInterprocessFullStack(discardAhead, null);
        }
        iLFCurrentStack(tag, stack);*/
    }

    /**
     * 避免log过长导致logcat截断
     */
    public static void splitLogI(String tag, String log) {
        if (log == null) {
            return;
        }
        final int maxLength = 3500;
        int length = log.length();
        if (length <= maxLength) {
            Log.i(tag, log);
            return;
        }

        int splitNum = length / maxLength + (length % maxLength == 0 ? 0 : 1);
        for (int i = 0; i < splitNum; i++) {
            String splited;
            if (i != splitNum - 1) {
                splited = log.substring(i * maxLength, (i + 1) * maxLength);
            } else {
                splited = log.substring(i * maxLength, Math.min((i + 1) * maxLength, length));
            }
            splited = "[-" + i + "-]" + splited;
            Log.i(tag, splited);
        }
    }

    public static String getStack() {
        /*if (StackTraceManager.isEnabled()) {
            return StackTraceManager.get().getFullStack(5, null);
        }*/
        return Log.getStackTraceString(new RuntimeException("KGLog_StackTrace"));
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }
}
