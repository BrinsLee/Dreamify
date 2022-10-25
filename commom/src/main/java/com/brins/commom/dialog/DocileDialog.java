package com.brins.commom.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.brins.commom.BuildConfig;
import com.brins.commom.utils.log.DrLog;

/**
 * Created by buronehuang on 2019/1/17.
 */

public class DocileDialog extends Dialog implements DocileInterface {

    private final DialogRegulator.DocileMember member = new DialogRegulator.DocileMember(this);

    public DocileDialog(@NonNull Context context) {
        super(context);
    }

    public DocileDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected DocileDialog(@NonNull Context context, boolean cancelable,
                           @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        final Context context = getContext();
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                if (DrLog.DEBUG) {
                    DrLog.d("AudioClimaxSelectSongMgr", "应该要弹出弹框:弹框失败：isFinishing()");
                }
                return;
            }
        }
        try {
            super.show();
        } catch (Exception e) { // WindowManager$BadTokenException
            DrLog.printException(e);
            if (BuildConfig.DEBUG) {//try catch输出异常日志，及时发现问题
                e.printStackTrace();
            }
        }
        if (DrLog.DEBUG) DrLog.i("DocileDialog", getClass().getName());
    }

    @Override
    public void dismiss() {
        final Context context = getContext();
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        super.dismiss();
    }

    @Override
    public final void askShow() {
        DialogAuthorityAnnotation ant = DocileDialog.this.getClass()
                .getAnnotation(DialogAuthorityAnnotation.class);
        float authority = ant == null ? DialogAuthority.ORDER : ant.authority();
        askShow(authority);
    }

    @Override
    public final void askShow(float authority) {
        member.setNeedDelayShow(needDelayShow());
        member.pushToShow(authority);
    }

    @Override
    public final void clearBehinds() {
        member.clearSubsequents();
    }

    public boolean canShowNow() {
        return member.canShowNow();
    }

    public boolean cancelShow() {
        boolean result = member.cancelShow();
        if (result && DrLog.DEBUG) {
            DrLog.i("DocileDialog", "cancelShow suc.");
        }
        return result;
    }

    @Override
    protected void onStop() {
        super.onStop();
        member.notifyStop();
    }

    protected void notifyStop() {
        member.notifyStop();
    }

    protected boolean needDelayShow() {
        return true;
    }
}
