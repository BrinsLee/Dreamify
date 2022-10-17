package com.brins.commom.toast;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.brins.commom.R;
import com.brins.commom.utils.ToastUtil;

public class CustomToast extends Toast {

    private static Toast mToast;

    public CustomToast(Context context) {
        super(context);
    }

    /**
     * 普通带图片Toast
     *
     * @param context
     * @param imageResId
     * @param text
     * @param duration
     * @return
     */
    public static Toast makeCustomToast(Context context, int imageResId,
                                        CharSequence text, int duration) {
        return makeCustomToast(context, imageResId, text, 0, duration);
    }

    /**
     * 普通带图片Toast
     *
     * @param context
     * @param imageResId
     * @param textResId  文本资源ID
     * @param duration
     * @return
     */
    public static Toast makeCustomToast(Context context, int imageResId,
                                        int textResId, int duration) {
        return makeCustomToast(context, imageResId, context.getResources()
                .getText(textResId), duration);
    }


    /**
     * 8.0样式的toast
     *
     * @param context
     * @param imageResId       如果左侧没图片，传-1
     * @param toastDescription toast的文字内容
     * @param duration         toast持续时间
     * @return
     */
    public static Toast makeKGCustomToast(Context context, int imageResId,
                                          CharSequence toastDescription, int duration) {
        mToast = ToastCompat.makeText(context, "", duration);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.common_custom_icon_toast_layout, null);
        TextView toastDescriptionTextView = (TextView) layout.findViewById(R.id.comm_progress_description);
        toastDescriptionTextView.setText(toastDescription);
        ImageView toastImage = (ImageView) layout.findViewById(R.id.comm_progress_loading);
        if (imageResId < 0) {
            toastImage.setVisibility(View.GONE);
        } else {
            toastImage.setImageResource(imageResId);
        }
        mToast.setView(layout);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        return mToast;
    }

    public static Toast makeKGCustomToast(Context context, int imageResId, int strResID, int duration) {
        return makeKGCustomToast(context, imageResId, context.getString(strResID), duration);
    }

    /**
     * 可设置背景图片Toast
     *
     * @param context
     * @param imageResId
     * @param text
     * @param backIconResId
     * @param duration
     * @return
     */
    public static Toast makeCustomToast(Context context, int imageResId,
                                        CharSequence text, int backIconResId, int duration) {
        mToast = ToastCompat.makeText(context, "", duration);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.custom_toast_layout, null);
        TextView textView = (TextView) layout.findViewById(R.id.toast_text);
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        ImageView toastImage = (ImageView) layout
                .findViewById(R.id.toast_image);
        if (imageResId < 0) {
            toastImage.setVisibility(View.GONE);
        } else {
            toastImage.setImageResource(imageResId);
        }
        RelativeLayout containerLayout = (RelativeLayout) layout
                .findViewById(R.id.toast_layout);
        containerLayout.setBackgroundResource(R.drawable.toast_bg);
        containerLayout.getBackground().setAlpha(200);
        ImageView backIcon = (ImageView) layout.findViewById(R.id.back_icon);
        if (backIconResId == 0) {
            containerLayout.removeView(backIcon);
        } else {
            backIcon.setImageResource(backIconResId);
            backIcon.setAlpha(100);
        }
        mToast.setView(layout);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        return mToast;
    }

    /**
     * 可设置背景图片Toast
     *
     * @param context
     * @param imageResId
     * @param textResId     文本资源ID
     * @param backIconResId
     * @param duration
     * @return
     */
    public static Toast makeCustomToast(Context context, int imageResId,
                                        int textResId, int backIconResId, int duration) {
        return makeCustomToast(context, imageResId, context.getResources()
                .getText(textResId), backIconResId, duration);
    }

    /**
     * 撤销Toast，可放在BaseActivity的onPause()中
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     *
     */
    public static void destoryToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }


    private static boolean isInMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        if (myLooper == mainLooper) {
            return true;
        }
        return false;
    }

    private static Toast createConstantToast(Context context, int imageResId,
                                          CharSequence toastDescription, int duration) {
        if (isInMainThread() && context != null) {
            if (!ToastUtil.isToastReused() || null == mToast) {
                mToast =ToastCompat.makeText(context, "", duration);
            }
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.common_custom_icon_toast_layout, null);
            TextView toastDescriptionTextView = (TextView) layout.findViewById(R.id.comm_progress_description);
            toastDescriptionTextView.setText(toastDescription);
            ImageView toastImage = (ImageView) layout.findViewById(R.id.comm_progress_loading);
            if (imageResId < 0) {
                toastImage.setVisibility(View.GONE);
            } else {
                toastImage.setImageResource(imageResId);
            }
            mToast.setView(layout);
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            return mToast;
        }
        return null;
    }

    /**
     * 在同一个Toast上修改，预防多次弹出Toast在同一时间，子线程和UI线程都可以使用
     * @param context 上下文
     * @param imageResId 左边图标id，-1为不显示
     * @param toastDescription 文本
     * @param duration 显示时间
     * @return
     */
    public static Toast showKGConstantToast(final Context context, final int imageResId,
                                            final CharSequence toastDescription, final int duration) {
        Toast toast = null;
        if (context != null) {
            if (isInMainThread()) {
                toast = createConstantToast(context, imageResId, toastDescription, duration);
                if (null != toast) {
                    toast.show();
                }
            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast2 = createConstantToast(context, imageResId, toastDescription, duration);
                        if (null != toast2) {
                            toast2.show();
                        }
                    }
                });
            }
        }
        return toast;
    }
}