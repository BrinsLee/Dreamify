package com.brins.commom.res;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.entity.SkinBgType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.BitmapUtil;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NResources {

    private Handler mUIHandler;
    private ExecutorService mPools;
    private ArrayMap<String, NRunnable> nRunnableArrayMap;
    private boolean released = false;

    private NResources() {
        mUIHandler = new StackTraceHandler(Looper.getMainLooper());
        nRunnableArrayMap = new ArrayMap<>();
        mPools = new ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * call this method when your process exit.
     */
    public synchronized void release() {
        mUIHandler.removeCallbacksAndMessages(null);
        mPools.shutdown();

        released = true;
        nRunnableArrayMap = null;
    }

    private static class InstanceHolder {
        private final static NResources sInstance = new NResources();
    }

    public static NResources getInstance() {
        return InstanceHolder.sInstance;
    }

    public void setBackground(View view, int resId) {
        setBackground(view, "", resId);
    }

    public void setBackground(View view, String filePath) {
        setBackground(view, filePath, 0);
    }

    public void setBackground(View view, String filePath, int defaultResId) {
        set(new Wrapper("Background@" + view.hashCode(), view, defaultResId, filePath));
    }

    public void setSkinBackground(View view, SkinBgType bgType) {
        set(new Wrapper("Background@" + view.hashCode(), view, bgType));
    }

    public void setSrc(ImageView view, int resId) {
        setSrc(view, "", resId);
    }

    public void setSrc(ImageView view, String filePath) {
        setSrc(view, filePath, 0);
    }

    public void setSrc(ImageView view, String filePath, int defaultResId) {
        set(new Wrapper("Src@" + view.hashCode(), view, defaultResId, filePath));
    }

    public void setSkinSrc(ImageView view, String resName, int resId) {
        set(new Wrapper("Src@" + view.hashCode(), view, resName, resId));
    }

    private synchronized void set(Wrapper wrapper) {
        if (released) return;

        String key = wrapper.runnableKey;
        NRunnable oldLoadRunnable = nRunnableArrayMap.get(key);
        if (oldLoadRunnable != null) oldLoadRunnable.abort();

        NRunnable loadRunnable = new LoadRunnable(wrapper);
        nRunnableArrayMap.put(key, loadRunnable);
        mPools.execute(loadRunnable);
    }

    private class LoadRunnable implements NRunnable {
        private Wrapper wrapper;
        private DisplayRunnable displayRunnable;
        private boolean abort = false;

        public LoadRunnable(Wrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public void run() {
            String key = wrapper.runnableKey;
            boolean decodeFile = false;
            if (!TextUtils.isEmpty(wrapper.filePath) && new File(wrapper.filePath).exists()) decodeFile = true;

            View view = wrapper.viewWeakRef.get();
            Drawable drawable = null;
            Bitmap bitmap = null;
            displayRunnable = new DisplayRunnable();
            displayRunnable.setDisplayType(key.startsWith("Src") ? SRC : BACKGROUND);

            // Setup 1, try load drawable or default res.
            try {
                if (!abort) {
                    switch (wrapper.type) {
                        case Wrapper.Type.NORMAL:
                            drawable = DRCommonApplication.getContext().getResources().getDrawable(wrapper.resId);
                            break;
                        case Wrapper.Type.SKIN_DRAWABLE:
                            drawable = SkinResourcesUtils.getInstance().getDrawable(wrapper.resName, wrapper.resId);
                            break;
                        case Wrapper.Type.SKIN_BG:
                            if (SkinProfileUtil.isBlurOrSolidOrSimpleSkin()) {
                                Bitmap bottomBitmap = BitmapUtil.createColorBitmap(Color.WHITE);
                                Canvas canvas = new Canvas(bottomBitmap);
                                canvas.drawColor(Color.parseColor("#0F6277C0"));
//                                drawable = new BitmapDrawable(BitmapUtil.createColorBitmap(Color.parseColor("#0F6277C0")));
                                drawable = new BitmapDrawable(bottomBitmap);
                            } else {
                                drawable = SkinResourcesUtils.getInstance().getDrawableBg(wrapper.skinBgType);
                            }
                            break;
                    }
                }
            } catch (Throwable e) {
            }

            // Setup 2, if got drawable from res, display it.
            if ((!decodeFile || drawable != null) && !abort) {
                displayRunnable.fill(wrapper.viewWeakRef ,drawable);
                mUIHandler.post(displayRunnable);
            }

            // Setup 3, decode bitmap form file, if need be.
            if (decodeFile && !abort) {
                int width = view.getWidth();
                int height = view.getHeight();
                bitmap = BitmapUtil.decodeFile(wrapper.filePath, width, height);
            }

            // Setup 4, if got bitmap from file, display it.
            if (bitmap != null && !bitmap.isRecycled() && !abort) {
                displayRunnable.fill(wrapper.viewWeakRef, new BitmapDrawable(bitmap));
                mUIHandler.post(displayRunnable);
            }

            // Setup 5, remove task record.
            synchronized (NResources.this) {
                nRunnableArrayMap.remove(key);
            }
        }

        @Override
        public void abort() {
            if (displayRunnable != null) displayRunnable.abort();
            abort = true;
        }
    }

    private class DisplayRunnable implements NRunnable {
        private WeakReference<View> viewWeakRef;
        private Drawable drawable;
        private @DisplayType int displayType = BACKGROUND;
        private boolean abort = false;

        public void fill(WeakReference<View> viewWeakRef, Drawable drawable) {
            this.viewWeakRef = viewWeakRef;
            this.drawable = drawable;
        }

        public void setDisplayType(@DisplayType int displayType) {
            this.displayType = displayType;
        }

        @Override
        public void run() {
            if (abort) return;

            View view = viewWeakRef.get();

            if (view != null && !abort) {
                switch (displayType) {
                    case SRC:
                        if (view instanceof ImageView) ((ImageView) view).setImageDrawable(drawable);
                        break;
                    case BACKGROUND:
                        view.setBackgroundDrawable(drawable);
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void abort() {
            abort = true;
        }
    }
    public static final int BACKGROUND = 0;
    public static final int SRC = 1;
    @IntDef({BACKGROUND, SRC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayType {}
//    private enum DisplayType {
//        BACKGROUND, SRC
//    }

    interface NRunnable extends Runnable {
        void abort();
    }

    static class Wrapper {
        static class Type {
            public static final int NORMAL = 0;
            public static final int SKIN_DRAWABLE = 1;
            public static final int SKIN_BG = 2;
        }

        int type = Type.NORMAL;
        String runnableKey;
        WeakReference<View> viewWeakRef;
        String filePath;
        int resId;
        String resName;
        SkinBgType skinBgType;

        private Wrapper(String key, View view, int type) {
            this.runnableKey = key;
            this.viewWeakRef = new WeakReference<>(view);
            this.type = type;
        }

        public Wrapper(String key, View view, int resId, String filePath) {
            this(key, view, Type.NORMAL);
            this.resId = resId;
            this.filePath = filePath;
        }

        public Wrapper(String key, View view, String resName, int resId) {
            this(key, view, Type.SKIN_DRAWABLE);
            this.resId = resId;
            this.resName = resName;
        }

        public Wrapper(String key, View view, SkinBgType bgType) {
            this(key, view, Type.SKIN_BG);
            this.skinBgType = bgType;
        }
    }
}