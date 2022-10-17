package com.brins.commom.utils;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lipeilin
 * @date 2022/10/17
 * @desc
 */
public class IOUtils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    @Deprecated
    public static void readAndIgnoreReturnValue(InputStream inputStream, byte data[]) throws IOException {
        if (inputStream == null || data == null)
            return;

        readAndIgnoreReturnValue(inputStream, false, data, 0, data.length);
    }

    @Deprecated
    public static void readAndIgnoreReturnValue(InputStream inputStream,
        byte data[], int offset, int count) throws IOException {
        readAndIgnoreReturnValue(inputStream, false, data, offset, count);
    }

    @Deprecated
    public static void readAndIgnoreReturnValue(InputStream inputStream) throws IOException {
        readAndIgnoreReturnValue(inputStream, true, null, 0, 0);
    }

    private static void readAndIgnoreReturnValue(InputStream inputStream, boolean readOneByte,
        byte data[], int offset, int count) throws
        IOException {
        if (inputStream == null)
            return;

        if (!readOneByte && (data == null || offset < 0 || offset + count >= data.length))
            return;

        int ret = Integer.MIN_VALUE;
        if (readOneByte)
            ret = inputStream.read();
        else
            ret = inputStream.read(data, offset, count);

        if (ret < 0) {
            // 除非预先知道流长度，一般都需要检查返回值
            Log.d("IOUtils", "read length = " + ret);
        }
    }

    /**
     * 将input流转为byte数组，自动关闭
     *
     * @param in 输入流
     * @return
     */
    public static byte[] toByteArray(InputStream in) throws Exception {
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream output = null;
        byte[] result = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
        } finally {
            closeQuietly(in);
            closeQuietly(output);
        }
        return result;
    }
}
