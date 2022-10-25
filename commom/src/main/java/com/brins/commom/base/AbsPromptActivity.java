package com.brins.commom.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import com.brins.commom.R;
import com.brins.commom.toast.ToastCompat;
import com.brins.commom.utils.ToastUtil;

/**
 * Created by huguoyin on 17/7/20.
 * Toast显示提取到父类
 */
public abstract class AbsPromptActivity extends AbsFrameworkActivity{

    protected Context mContext;

    protected Context mApplicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mApplicationContext = getApplicationContext();
    }

    // start--------------显示toast代码----------------
    private Toast mToast;

    private int mDefaultToastGravity, mDefaultToastXOffset, mDefaultToastYOffset;

    private boolean mDefaultToastValueInited = false;

    private void initDefaultToastGravity() {
        if (mDefaultToastValueInited)
            return;
        mDefaultToastValueInited = true;
        try {
            Toast temp = ToastCompat.makeText(this, "", Toast.LENGTH_SHORT);
            mDefaultToastGravity = temp.getGravity();
            mDefaultToastXOffset = temp.getXOffset();
            mDefaultToastYOffset = temp.getYOffset();
        } catch (OutOfMemoryError e) {// kg-suppress REGULAR.ERROR 合并重复代码，保留之前改动
            e.printStackTrace();
        } catch (Exception e){
            mDefaultToastGravity = 0;
            mDefaultToastXOffset = 0;
            mDefaultToastYOffset = 0;
        }
    }

    /**
     * 长时间显示一个Toast类型的消息
     *
     * @param strResId 显示的消息
     */
    public void showToastLong(final int strResId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    initDefaultToastGravity();
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastUtil.createCommonToast(getApplicationContext());
                    }
                    setToastIcon(mToast, null);
                    mToast.setDuration(Toast.LENGTH_LONG);
                    mToast.setGravity(mDefaultToastGravity, mDefaultToastXOffset,
                            mDefaultToastYOffset);
                    mToast.setText(strResId);
                    mToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 长时间显示一个Toast类型的消息
     *
     * @param msg 显示的消息
     */
    public void showToastLong(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastUtil.createCommonToast(getApplicationContext());
                    }
                    setToastIcon(mToast, null);
                    mToast.setDuration(Toast.LENGTH_LONG);
                    mToast.setText(msg);
                    mToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showToastLong(final String msg, final int gravity, final int xOffset,
                              final int yOffset, final int duration) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastCompat.makeText(getApplicationContext(), "", duration);
                    }
                    mToast.setDuration(Toast.LENGTH_LONG);
                    mToast.setGravity(gravity, xOffset, yOffset);
                    mToast.setText(msg);
                    mToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showSuccessedToast(String msg) {
        showToastWithIcon(mContext.getResources().getDrawable(R.drawable.common_toast_succeed), msg, Toast.LENGTH_SHORT);
    }

    public void showFailToast(String msg) {
        showToastWithIcon(mContext.getResources().getDrawable(R.drawable.common_toast_fail) ,msg, Toast.LENGTH_SHORT);
    }

    public void showToastWithIcon(final int drawableId,final int msgId,final int duration) {
        showToastWithIcon(mContext.getResources().getDrawable(drawableId),
                mContext.getResources().getString(msgId), duration);
    }

    public void showToastWithIcon(final Drawable drawable,final String msg,final int duration) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastUtil.createCommonToast(getApplicationContext());
                    }
                    setToastIcon(mToast, drawable);
                    mToast.setDuration(duration);
                    mToast.setText(msg);
                    mToast.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showToast(final int strResId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastUtil.createCommonToast(getApplicationContext());
                    }
                    setToastIcon(mToast, null);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setText(strResId);
                    mToast.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void setToastIcon(Toast toast, Drawable iconDrawable) {
        TextView messageText = (TextView) toast.getView().findViewById(android.R.id.message);
        if (iconDrawable != null) {
            iconDrawable.setBounds(0, 0, iconDrawable.getMinimumWidth(), iconDrawable.getMinimumHeight());
        }
        messageText.setCompoundDrawables(iconDrawable, null, null, null);
    }

    public void showToast(final CharSequence str) {
        showToast(null,str);
    }

    public void showToast(final Drawable drawable,final CharSequence str) {
        showToast(drawable, str, Gravity.CENTER);
    }

    public void showToast(final Drawable drawable,final CharSequence str, int gravity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastUtil.createCommonToast(getApplicationContext());
                    }
                    if (mToast != null && mToast.getView() != null) {
                        TextView messageText = (TextView) mToast.getView().findViewById(android.R.id.message);
                        if (messageText != null) {
                            messageText.setGravity(gravity);
                        }
                    }
                    setToastIcon(mToast, drawable);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setText(str);
                    mToast.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showToast(final int strResId, final int gravity, final int xOffset,
                          final int yOffset) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastCompat.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                    }
                    setToastIcon(mToast, null);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setGravity(gravity, xOffset, yOffset);
                    mToast.setText(strResId);
                    mToast.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showToast(final CharSequence str, final int gravity, final int xOffset,
                          final int yOffset) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!ToastUtil.isToastReused() || mToast == null) {
                        mToast = ToastCompat.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                    }
                    setToastIcon(mToast, null);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setGravity(gravity, xOffset, yOffset);
                    mToast.setText(str);
                    mToast.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
