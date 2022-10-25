package com.brins.commom.utils.xscreen.vivo;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.JsonReader;
import android.util.Log;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

/**
 * Created by vivo fingerprint team on 2018/3/20.
 * Utils to get fingerprint information such as icon position when
 * under display fingerprint valid.
 */
public final class FingerprintInsets {

    private final static String TAG = "FingerprintInsets";

    public final static int ICON_INVISIBLE = 0;
    public final static int ICON_VISIBLE = 1;

    private final static int VERSION_MAJOR = 1;
    private final static int VERSION_MINOR = 0;

    /** Keep the same as service. */
    private final static int MSG_QUERY_INFO = 10000;
    private final static int MSG_ICON_STATE_CHANGE = 10001;
    private final static int MSG_CLEAR = 10002;

    private final static int MSG_INTERNAL_NOTIFY_READY = 10;

    private final static String KEY_MAJOR_VERSION = "version_major";
    private final static String KEY_MINOR_VERSION = "version_minor";
    private final static String KEY_QUERY_JSON_STRING = "query_json";
    private final static String KEY_HAS_UNDER_DISPLAY_FINGERPRINT = "has_under_display_fingerprint";
    private final static String KEY_ICON_POSITION = "icon_position";
    private final static String KEY_ICON_STATE = "icon_state";

    private final static String KEY_TOKEN = "token";

    /** Flag indicating whether we have called bind on the service. */
    private boolean mIsBound;

    private Map<String, Property> mProperties;

    private FingerprintInsetsListener mListener;

    /** Messenger for communicating with service. */
    private Messenger mService;

    private Handler mHandler;

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private Messenger mMessenger;

    private WeakReference<Context> mContextRef;

    private static volatile FingerprintInsets sInstance;

    private FingerprintInsets(Context context) {
        mContextRef = new WeakReference<>(context);
        mProperties = new ArrayMap<>(8);

        mHandler = new IncomingHandler(this);
        mMessenger = new Messenger(mHandler);

        info(String.format(Locale.ENGLISH,
                "model:%s, product:%s, device:%s, manufacturer:%s",
                Build.MODEL, Build.PRODUCT, Build.DEVICE, Build.MANUFACTURER));
    }

    @Nullable
    public static FingerprintInsets create(Context context, FingerprintInsetsListener listener) {
        /*if (!isVivoDevice()) { 已经判断过了，无需再次处理
            return null;
        }*/

        if (sInstance == null) {
            final FingerprintInsets insets = new FingerprintInsets(context);
            insets.setFingerprintInsetsListener(listener);
            insets.doBindService();

            sInstance = insets;
        }

        return sInstance;
    }

    public boolean isReady() {
        return !mIsBound || !mProperties.isEmpty();
    }

    public void destroy() {
        if (sInstance != null) {
            doUnbindService();

            mProperties.clear();
            mContextRef.clear();

            sInstance = null;
        }
    }

    public boolean hasUnderDisplayFingerprint() {
        return getPropertyBoolean(KEY_HAS_UNDER_DISPLAY_FINGERPRINT);
    }

    public void setFingerprintInsetsListener(FingerprintInsetsListener listener) {
        mListener = listener;
    }

    public int getFingerprintIconState() {
        return getPropertyInteger(KEY_ICON_STATE);
    }

    public Rect getFingerprintIconPosition() {
        final Rect rect = getPropertyRect(KEY_ICON_POSITION);
        if (rect == null) {
            return new Rect(-1, -1, -1, -1);
        }
        return new Rect(rect);
    }

    public int getFingerprintIconLeft() {
        final Rect rect = getPropertyRect(KEY_ICON_POSITION);
        if (rect == null) {
            return -1;
        }
        return rect.left;
    }

    public int getFingerprintIconTop() {
        final Rect rect = getPropertyRect(KEY_ICON_POSITION);
        if (rect == null) {
            return -1;
        }
        return rect.top;
    }

    public int getFingerprintIconRight() {
        final Rect rect = getPropertyRect(KEY_ICON_POSITION);
        if (rect == null) {
            return -1;
        }
        return rect.right;
    }

    public int getFingerprintIconBottom() {
        final Rect rect = getPropertyRect(KEY_ICON_POSITION);
        if (rect == null) {
            return -1;
        }
        return rect.bottom;
    }

    private boolean doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        Context context = mContextRef.get();
        if (context == null) {
            debug("Context missed!");
            return false;
        }

        final Intent intent = new Intent();
        intent.setClassName("com.vivo.udfingerprint",
                "com.vivo.udfingerprint.service.MessengerService");
        intent.putExtra(KEY_MAJOR_VERSION, VERSION_MAJOR);
        intent.putExtra(KEY_MINOR_VERSION, VERSION_MINOR);
        final boolean binded;
        try {
            binded = context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            if (!binded) {
                info("Service not exist");
                mIsBound = false;
                loadPropertiesOffline();

                Message.obtain(mHandler, MSG_INTERNAL_NOTIFY_READY).sendToTarget();
            } else {
                debug("Binding.");
                mIsBound = true;
            }
        } catch (SecurityException ignore) {
        }

        return mIsBound;
    }

    private void doUnbindService() {
        if (!mIsBound) {
            debug("Service not bound");
            return;
        }

        // If we have received the service, and hence registered with
        // it, then now is the time to unregister.
        if (mService != null) {
            try {
                Message msg = Message.obtain(null,
                        MSG_CLEAR);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // There is nothing special we need to do if the service
                // has crashed.
            }
            mService = null;
        }

        mIsBound = false;

        final Context context = mContextRef.get();
        if (context == null) {
            debug("Context missed!");
            return;
        }

        // Detach our existing connection.
        context.unbindService(mConnection);
        debug("Unbinding.");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void parseQueryResult(String jsonString) {
        setPropertyBoolean(KEY_HAS_UNDER_DISPLAY_FINGERPRINT, false);

        if (!TextUtils.isEmpty(jsonString)) {
            final JsonReader reader = new JsonReader(new StringReader(jsonString));
            try {
                parseProperties(reader);
            } catch (IOException e) {
                mProperties.clear();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    //
                }
            }
        }

        Message.obtain(mHandler, MSG_INTERNAL_NOTIFY_READY).sendToTarget();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void parseProperties(JsonReader reader) throws IOException {
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, KEY_ICON_STATE)) {
                setPropertyInteger(name, reader.nextInt());
            } else if (TextUtils.equals(name, KEY_HAS_UNDER_DISPLAY_FINGERPRINT)) {
                setPropertyBoolean(name, reader.nextBoolean());
            } else if (TextUtils.equals(name, KEY_ICON_POSITION)) {
                final Rect positionRect = parsePosition(reader);
                setPropertyRect(name, positionRect);
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Rect parsePosition(JsonReader reader) throws IOException {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        reader.beginArray();
        if (reader.hasNext()) {
            left = reader.nextInt();
        }

        if (reader.hasNext()) {
            top = reader.nextInt();
        }

        if (reader.hasNext()) {
            right = reader.nextInt();
        }

        if (reader.hasNext()) {
            bottom = reader.nextInt();
        }
        reader.endArray();

        return new Rect(left, top, right, bottom);
    }

    private boolean loadPropertiesOffline() {
        if (Build.VERSION.SDK_INT < 24) {
            // There is no device use under display fingerprint lower than android N.
            setPropertyBoolean(KEY_HAS_UNDER_DISPLAY_FINGERPRINT, false);
            return false;
        }

        if (Build.VERSION.SDK_INT < 26) {
            debug("fingerprint: " + getFingerprintModule());
        }

        int centerX = 0;
        int centerY = 0;
        int iconWidth = 0;
        int iconHeight = 0;
        Rect rect = new Rect();
        if (isX20PlusUD()) {
            debug("isX20PlusUD");
            centerX = 540;
            centerY = 2006;
            iconWidth = 160;
            iconHeight = 160;
        } else if (isX21UD()) {
            debug("isX21UD");
            centerX = 540;
            centerY = 1924;
            iconWidth = 170;
            iconHeight = 170;
        } else {
            debug("No under display fingerprint detected");
        }

        rect.set(centerX, centerY, centerX + iconWidth, centerY + iconHeight);
        rect.offset(-iconWidth/2, -iconHeight/2);

        if (rect.isEmpty()) {
            setPropertyBoolean(KEY_HAS_UNDER_DISPLAY_FINGERPRINT, false);
        } else {
            setPropertyRect(KEY_ICON_POSITION, rect);
            setPropertyInteger(KEY_ICON_STATE, ICON_VISIBLE);
            setPropertyBoolean(KEY_HAS_UNDER_DISPLAY_FINGERPRINT, true);
        }

        return true;
    }

    private static boolean isVivoDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("vivo");
    }

    private static boolean isX20PlusUD() {
        if (Build.DEVICE.equalsIgnoreCase("PD1721")) {
            return true;
        }

        if (Build.DEVICE.equalsIgnoreCase("PD1710")) {
            if (Build.VERSION.SDK_INT < 26) {
                // Just support level below O, Api after O_MR1 don't allow use reflection.
                if (isUdModule()) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isX21UD() {
        if (Build.DEVICE.equalsIgnoreCase("PD1728UD")) {
            return true;
        }

        if (Build.DEVICE.contains("1728") ||
                Build.DEVICE.contains("1725")) {
            if (Build.VERSION.SDK_INT <= 27) {
                // isUdModule use reflection to get fingerprint property.
                // Api after O_MR1 don't allow use reflection.
                if (isUdModule()) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isUdModule() {
        final String fingerprint = getFingerprintModule();
        return !TextUtils.isEmpty(fingerprint) && fingerprint.startsWith("udfp_");
    }

    private static String getFingerprintModule() {
        String fingerprint = getProperty("sys.fingerprint.boot", "");
        if (TextUtils.isEmpty(fingerprint)) {
            fingerprint = getProperty("persist.sys.fptype", "unknown");
        }
        return fingerprint;
    }

    /**
     * TODO: Api after O_MR1 don't allow use reflection.
     * @param key property name
     * @param defaultValue return value if property is empty.
     * @return property
     */
    private static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            debug(e.getMessage());
        }

        return value;
    }

    private void setPropertyBoolean(String key, boolean value) {
        if (mProperties.containsKey(key)) {
            debug("update property " + key);
        }

        Property property = new Property<Boolean>(key, value);
        mProperties.put(key, property);
    }

    private void setPropertyInteger(String key, int value) {
        if (mProperties.containsKey(key)) {
            debug("update property " + key);
        }

        Property property = new Property<Integer>(key, value);
        mProperties.put(key, property);
    }

    private void setPropertyRect(String key, Rect rect) {
        Property property = new Property<Rect>(KEY_ICON_POSITION, rect);
        mProperties.put(key, property);
    }

    private boolean getPropertyBoolean(String key) {
        Property property = mProperties.get(key);
        if (property == null) {
            return false;
        }
        return (Boolean)property.value;
    }

    private int getPropertyInteger(String key) {
        Property property = mProperties.get(key);
        if (property == null) {
            return -1;
        }
        return (Integer) property.value;
    }

    private Rect getPropertyRect(String key) {
        Property property = mProperties.get(key);
        if (property == null) {
            return null;
        }
        return (Rect) property.value;
    }

    private void notifyReady() {
        if (mListener != null) {
            mListener.onReady();
        }
    }

    private void notifyIconStateChanged(int state) {
        setPropertyInteger(KEY_ICON_STATE, state);

        if (mListener != null) {
            mListener.onIconStateChanged(state);
        }
    }

    private static void info(String msg) {
        Log.i(TAG, msg);
    }

    private static void debug(String msg) {
        if (DrLog.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            debug("Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null, MSG_QUERY_INFO);
                msg.arg1 = VERSION_MAJOR;
                msg.arg2 = VERSION_MINOR;

                Context context = mContextRef.get();
                if (context != null) {
                    Bundle extras = new Bundle();
                    extras.putString(KEY_TOKEN, context.getPackageName());
                    msg.setData(extras);
                }

                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
        }

        @Override public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            debug("Disconnected.");
        }
    };

    public static interface FingerprintInsetsListener {
        void onReady();
        void onIconStateChanged(int state);
    }

    private static class Property<T> {
        String key;
        T value;

        Property(String k, T v) {
            key = k;
            value = v;
        }
    }

    /**
     * Handler of incoming messages from service.
     */
    private static class IncomingHandler extends StackTraceHandler {
        private WeakReference<FingerprintInsets> mInsets;

        IncomingHandler(FingerprintInsets insets) {
            super();

            mInsets = new WeakReference<>(insets);
        }

        @Override
        public void handleMessage(Message msg) {
            final FingerprintInsets insets = mInsets.get();
            if (insets == null) {
                debug("missing insets reference");
                super.handleMessage(msg);
                return;
            }

            switch (msg.what) {
                case MSG_QUERY_INFO:
                    debug(String.format(Locale.ENGLISH,
                            "Received from service, version:%d.%d", msg.arg1, msg.arg2));
                    final Bundle extras = msg.getData();
                    final String jsonString = extras != null ? extras.getString(KEY_QUERY_JSON_STRING) : null;
                    insets.parseQueryResult(jsonString);
                    break;

                case MSG_ICON_STATE_CHANGE:
                    final int state = msg.arg1;
                    debug("Received from service, icon state:" + state);
                    insets.notifyIconStateChanged(state);
                    break;

                case MSG_INTERNAL_NOTIFY_READY:
                    insets.notifyReady();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
