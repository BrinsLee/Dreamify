package com.brins.commom.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import com.brins.commom.utils.log.DrLog;

/**
 * Created by zhemingli on 2017/9/18.
 * 用于处理dismiss时报View not attached to window manager的情况
 */

public class SafeDissmissDialog extends DocileDialog {

    protected Context mContext;

    public SafeDissmissDialog(Context context) {
        super(context);
        mContext = context;
    }

    public SafeDissmissDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    protected SafeDissmissDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    public void dismiss() {
        // 参照com.kugou.common.dialog8.BaseDialog.dismiss()
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }
        try {
            super.dismiss();
        } catch (IllegalArgumentException e) {
            if (DrLog.DEBUG) {
                DrLog.e("lzm", "dialog.dismiss() cast an Exception : " + e.toString());
            }
        }
    }
}
