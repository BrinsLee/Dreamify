
package com.brins.commom.dialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.view.View;
import com.brins.commom.R;
import com.brins.commom.broadcast.BroadcastUtil;

/**
 * 窗口
 * 
 * @author HalZhang
 * @version 2011-11-14上午10:38:15
 */
public abstract class BaseDialog extends SafeDissmissDialog {

    public BaseDialog(Context context) {
        super(context, R.style.PopMenu);

    }

    public BaseDialog(Activity activity) {
        super(activity, R.style.PopMenu);
        setOwnerActivity(activity);
    }

    public BaseDialog(Activity activity, int style) {
        super(activity, style);
        setOwnerActivity(activity);
    }

    public BaseDialog(Context context, int style) {
        super(context, style);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerDismissReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterDismissReceiver();
    }

    /**
     * 设置位置
     * 
     * @param rect
     * @param contentView
     */
    public void onMeasureAndLayout(Rect rect, View contentView) {
    }

    private void registerDismissReceiver() {
        IntentFilter filter = new IntentFilter();
        try {
            BroadcastUtil.registerReceiver(mReceiver, filter);
        } catch (Exception e) {
        }
    }

    private void unregisterDismissReceiver() {
        try {
            BroadcastUtil.unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (isShowing())
                    dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
