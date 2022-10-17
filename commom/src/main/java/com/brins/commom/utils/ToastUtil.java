package com.brins.commom.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.FloatRange;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.toast.CustomToast;
import com.brins.commom.toast.ToastCompat;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.brins.commom.widget.ToastLoadingView;

public class ToastUtil {
    
    private static Toast mToast;
    
    private static boolean checkToastNotNull(Context context) {
        // 所有调用发生在主线程
        if ((!isToastReused() || mToast == null) && context != null) {
            //传入的context 可能是Activity 会引起内存泄露 用getApplicationContext来规避！
            mToast = createCommonToast(context.getApplicationContext());
        }
        return (mToast != null);
    }
    

    public static boolean isToastReused() {// 7.1.1和7.1.2也不复用了。收到了他们的不少崩溃
        // 魅族手机ROM在复用的toast处理上有问题，可能出现toast的没有向WindowManage反注册就使用的情况
        // 针对Android 8.1及以后的系统版本也开启该处理方式
        // update 2019.02.15, 7.1(api 25) 机型禁止复用
        String osVersion = CommonUtil.getOsVersion(DRCommonApplication.getContext());

        return !"Meizu".equalsIgnoreCase(Build.MANUFACTURER)
                && Build.VERSION.SDK_INT < 27
                && !"7.1.1".equals(osVersion)
                && !"7.1.2".equals(osVersion);
    }

    private static boolean isInMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        return myLooper == mainLooper;
    }

    /**
     * at any thread
     * short time toast
     * @return Toast
     */
    public static void show(final Context context, final String msg) {
        if (isInMainThread()) {
            showToastShort(context, msg);
        } else {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToastShort(context, msg);
                }
            });
        }
    }

    public static void showToast(final String msg) {
        showToastShort(DRCommonApplication.getContext(), msg);
    }

    public static void showToast(final Context context, final String msg) {
        if (isInMainThread()) {
            showToastLong(context, msg);
        } else {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToastLong(context, msg);
                }
            });
        }
    }
    

    public static void show(Context context, int msgResId) {
        show(context, context.getString(msgResId));
    }

    public static Toast showKG11FailedToast(Context context, String message, int duration) {
        if (TextUtils.isEmpty(message)) {
            return null;
        }
        ToastLoadingView kg11ToastLoadingView = new ToastLoadingView(context);
        kg11ToastLoadingView.setFailed(message);
        return showKG11Toast(context, kg11ToastLoadingView, duration);
    }

    public static Toast showKG11Toast(Context context, final View view, int duration) {
        if (!isInMainThread()) {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showKG11Toast(context, view, duration);
                }
            });
            return null;
        }
        if (isInMainThread()) {
            if (checkToastNotNull(context) && view != null) {
                try {
                    mToast.setView(view);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.setDuration(duration);
                    mToast.show();
                } catch (NullPointerException e) {
                }
                return mToast;
            }
        }
        return null;
    }

    public static Toast showToastShortLeft(final Context context, final String msg) {
        return showToastShort(context, msg, Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }

    public static Toast showToastShort(final Context context, final String msg) {
        return showToastShort(context, msg, null);
    }

    public static Toast showToastShort(final Context context, final String msg, Integer gravity) {
        if (!isInMainThread()) {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToastShort(context, msg, gravity);
                }
            });
            return null;
        }
        if (isInMainThread()) {

            if (checkToastNotNull(context) && !isEmpty(msg) && context != null) {
                mToast.setText(msg);
                setToastIcon(null);
                mToast.setGravity(Gravity.CENTER, 0, 0);

                if (gravity != null && mToast != null && mToast.getView() != null) {
                    TextView messageText = mToast.getView().findViewById(android.R.id.message);
                    if (messageText != null) {
                        messageText.setGravity(gravity);
                    }
                }

                mToast.setDuration(Toast.LENGTH_SHORT);
                try {
                    mToast.show();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return mToast;
            }
        } else {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToastShort(context, msg, gravity);
                }
            });
        }
        return null;
    }

    public static Toast showToastShort(Context context, CharSequence msg) {
        if (!isInMainThread()) {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToastShort(context, msg);
                }
            });
            return null;
        }
        if (isInMainThread() && checkToastNotNull(context) && !TextUtils.isEmpty(msg) && context != null) {
            mToast.setText(msg);
            setToastIcon(null);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_SHORT);
            try{
                mToast.show();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
    }

	public static Toast showToastShort(Context context, int resId) {
        if (!isInMainThread()) {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToastShort(context, resId);
                }
            });
            return null;
        }
	    if (isInMainThread() && checkToastNotNull(context) && resId != 0 && context != null) {
            mToast.setText(resId);
            setToastIcon(null);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
            try{
                return mToast;
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        return null;
	}
	
	public static Toast showToastLong(Context context, String msg) {
        if (isInMainThread() && checkToastNotNull(context) && !isEmpty(msg) && context != null) {
            mToast.setText(msg);
            setToastIcon(null);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_LONG);
            try{
                mToast.show();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
	}

	public static Toast showToastLong(Context context, int resId) {
	    if (isInMainThread() && checkToastNotNull(context) && resId != 0 && context != null) {
            mToast.setText(resId);
            setToastIcon(null);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_LONG);
            try{
                mToast.show();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
	}

    public static Toast showToastLongWithGravity(Context context, String msg, int gravity, int yOffset) {
        if (isInMainThread() && checkToastNotNull(context) && !isEmpty(msg) && context != null) {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setGravity(gravity, 0, yOffset);
            try{
                mToast.show();
            }catch (Exception e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
    }

    public static Toast showWithOptions(String msg, int duration, int gravity, int xOffset, int yOffset) {
        if (isInMainThread() && checkToastNotNull(DRCommonApplication.getContext()) && !isEmpty(msg)) {
            mToast.setText(msg);
            mToast.setDuration(duration);
            mToast.setGravity(gravity, xOffset, yOffset);
            try{
                mToast.show();
            }catch (Exception e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
    }

    /**
     * 显示带icon的Toast
     * @param context
     * @param iconResId 图片id
     * @param msg 文字内容
     * @param duration 显示时长 Toast.LENGTH_LONG or Toast.LENGTH_SHORT
     * @return
     */
    public static Toast showToastWithIcon(Context context, int iconResId, String msg, int duration) {
        return showToastWithIcon(context, context.getResources().getDrawable(iconResId), msg, duration);
    }

    public static Toast showToastWithIcon(Context context, Drawable iconDrawable, String msg, int duration) {
        return showToastWithIcon(context, iconDrawable, msg, duration, Gravity.CENTER);
    }

    public static Toast showToastWithIcon(Context context, Drawable iconDrawable, String msg, int duration, int gravity) {
        if (isInMainThread() && checkToastNotNull(context)  && context != null) {
            mToast.setText(msg);
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            setToastIcon(iconDrawable);
            setToastMsgGravity(gravity);
            try{
                mToast.show();

            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
    }

    private static void setToastIcon(Drawable iconDrawable) {
        TextView messageText = (TextView) mToast.getView().findViewById(android.R.id.message);
        if (iconDrawable != null) {
            iconDrawable.setBounds(0, 0, iconDrawable.getMinimumWidth(), iconDrawable.getMinimumHeight());
        }
        messageText.setCompoundDrawables(iconDrawable, null, null, null);
    }

    private static void setToastMsgGravity(int gravity) {
        TextView messageText = (TextView) mToast.getView().findViewById(android.R.id.message);
        messageText.setGravity(gravity);
    }

    public static Toast showCustomToast(Context context, int resId, int iconId) {
        if (isInMainThread() && resId != 0 && context != null) {
            //因为有图片，需要新建一个Toast，不然会影响mToast显示
            Toast toast = ToastCompat.makeText(context, "", Toast.LENGTH_LONG);
            toast.setText(resId);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            ImageView image = new ImageView(context);
            image.setImageResource(iconId);
            toastView.addView(image, 0);
            toast.show();
            return toast;
        }
        return null;
    }

    public static Toast createCommonToast(Context context) {
        return createCommonToast(context, SystemUtils.dip2px(context, 3));
    }

    public static Toast createCommonToast(Context context, @FloatRange(from = 0.0, to = Float.MAX_VALUE)float radius) {
        if (isInMainThread() && context != null) {
            Toast toast = ToastCompat.makeText(context, "", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            View ParentView = toast.getView();
            ParentView.setBackgroundResource(R.drawable.transparent);
            TextView messageText = (TextView) ParentView.findViewById(android.R.id.message);
            if (messageText != null) {
                messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                messageText.setTextColor(Color.WHITE);
                messageText.setGravity(Gravity.CENTER);
                GradientDrawable bgDrawable = new GradientDrawable();
                bgDrawable.setColor(Color.parseColor("#CC000000"));
                bgDrawable.setCornerRadius(radius);
                messageText.setBackgroundDrawable(bgDrawable);
                messageText.setPadding(SystemUtils.dip2px(context, 18), SystemUtils.dip2px(context, 8),
                        SystemUtils.dip2px(context, 18), SystemUtils.dip2px(context, 8));
                messageText.setCompoundDrawablePadding(SystemUtils.dip2px(context, 7));
                return toast;
            }
        }
        return null;
    }

    public static boolean isEmpty(String str) {
	    if(str != null) {
	        str = str.trim();
	        return TextUtils.isEmpty(str);
	    }
	    return true;
	}

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * 8.0成功或失败短toast
     * @param context
     * @param toastDescription
     * @return
     */
    public static void showKGContentShortToast(Context context,boolean isSuccessed, CharSequence toastDescription){
        final int drawableId = isSuccessed ? R.drawable.common_toast_succeed : R.drawable.common_toast_fail;
        if (isInMainThread() && context != null) {
            CustomToast.makeKGCustomToast(context, drawableId, toastDescription, Toast.LENGTH_SHORT).show();
        }
    }
    public static void showKGContentShortToast(Context context,boolean isSuccessed, int toastDescription){
        final int drawableId = isSuccessed ? R.drawable.common_toast_succeed : R.drawable.common_toast_fail;
        if (isInMainThread() && context != null) {
            CustomToast.makeKGCustomToast(context, drawableId, toastDescription, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showKGContentShortToast(final String toastDescription) {
        if (isInMainThread()) {
            CustomToast.makeKGCustomToast(DRCommonApplication.getContext(), -1, toastDescription, Toast.LENGTH_SHORT).show();
        } else {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    CustomToast.makeKGCustomToast(DRCommonApplication.getContext(), -1, toastDescription, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * 8.0成功或失败长toast
     * @param context
     * @param toastDescription
     * @return
     */
    public static void showKGContentLongToast(Context context,boolean isSuccessed, CharSequence toastDescription){
        final int drawableId = isSuccessed ? R.drawable.common_toast_succeed : R.drawable.common_toast_fail;
        if (isInMainThread() && context != null) {
            CustomToast.makeKGCustomToast(context, drawableId, toastDescription, Toast.LENGTH_LONG).show();
        }
    }

    //可设置背景图片的toast
    public static Toast showCustomToastWithBg(Context context, String msg, int bgResId) {
        if (isInMainThread() && !msg.isEmpty() && context != null) {
            //因为有图片，需要新建一个Toast，不然会影响mToast显示
            Toast toast = ToastCompat.makeText(context, "", Toast.LENGTH_LONG);
            toast.setText(msg);
            toast.setGravity(Gravity.CENTER, 0, 0);
            View ParentView = toast.getView();
            ParentView.setBackgroundResource(bgResId);
            TextView messageText = (TextView) ParentView.findViewById(android.R.id.message);
            messageText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            messageText.setTextColor(Color.BLACK);
            messageText.setGravity(Gravity.CENTER);
            ParentView.setPadding(SystemUtils.dip2px(context, 13), SystemUtils.dip2px(context, 12),
                    SystemUtils.dip2px(context, 13), SystemUtils.dip2px(context, 12));
            //因为有个阴影所以文字看起来会有点模糊，去掉shadow
            messageText.getPaint().clearShadowLayer();
            toast.show();
            return toast;
        }
        return null;
    }

    public static Toast showBottomCustomToast(Context context, String msg,int y) {
        if (context != null) {
            Toast toast = ToastCompat.makeText(context, "", Toast.LENGTH_SHORT);
            toast.setText(msg);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, y);
            /*View ParentView = toast.getView();
            ParentView.setBackgroundDrawable(SystemUtil.getGradientDrawable(Color.parseColor("#77ffffff"),SystemUtil.dip2px(DRCommonApplication.getContext(),13)));
            TextView messageText = (TextView) ParentView.findViewById(android.R.id.message);
            messageText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            messageText.setTextColor(Color.WHITE);
            messageText.setGravity(Gravity.CENTER);*/
            toast.show();
            return toast;
        }
        return null;
    }


    /**
     * 展示加载失败Toast
     *
     * @param context
     * @return
     */
    public static void showLoadFailureToast(Context context) {
        showKGContentLongToast(context,false, "加载失败，请稍后重试");
    }

    /**
     * 展示加载失败Toast
     *
     * @param context
     * @return
     */
    public static void showLoadFailureToast(final Context context, final String errMsg) {
        final String msg = TextUtils.isEmpty(errMsg)?"加载失败，请稍后重试":errMsg;
        if (isInMainThread()) {
            showKGContentShortToast(context, false, msg);
        } else {
            new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showKGContentShortToast(context, false, msg);
                }
            });
        }
    }

}
