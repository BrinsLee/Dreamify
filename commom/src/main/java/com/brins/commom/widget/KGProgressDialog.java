package com.brins.commom.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.brins.commom.R;
import com.brins.commom.dialog.DocileProgressDialog;
import com.brins.commom.toast.LoadingTypes;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhaomingzhang on 2015/12/30.
 */
public class KGProgressDialog extends DocileProgressDialog {

    private ToastLoadingView toastLoadingView = null;
    private Context mContext;
    private View bodyView;

    private int mType = LoadingTypes.FLOATING;

    public KGProgressDialog(Context context) {
        super(context, R.style.KGProgressDialogTheme);

        configWindow(context);

        initView(context);
    }

    public KGProgressDialog(Context context, int theme) {
        super(context, theme);

        configWindow(context);

        initView(context);
    }


    private void configWindow(Context context) {
        if (!(context instanceof Activity)) {
            int permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            Window window = getWindow();
            if (window != null) {
                window.setType(permission);
            }
        }
    }

    public void initView(Context context){
        mContext = context;
        bodyView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        toastLoadingView = bodyView.findViewById(R.id.v_loading_toast);
    }

    protected int getLayoutId() {
        return R.layout.comm_progress_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bodyView);
    }

    public void setLoadingText(int textId){
        setLoadingText(mContext.getText(textId).toString());
    }

    public void setLoadingText(String text) {
        if (toastLoadingView != null) {
            toastLoadingView.setLoading(text);
        }
    }

    public void setLoadingText(String text, String secondStr) {
        setLoadingText(text);
        if (!TextUtils.isEmpty(secondStr)) {
            toastLoadingView.setDefDelayMessage(secondStr);
        }
    }


    long showTime = 0;


    public void updateLoadingText(String text){
        if (toastLoadingView != null) {
            toastLoadingView.setLoading(text);
        }
    }

    @Override
    public void show() {
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }
        super.show();
        if (toastLoadingView != null) {
            toastLoadingView.setVisibility(View.VISIBLE);
        }
        showTime = System.currentTimeMillis();
        //EventBus.getDefault().post(new ProgressVisibleEvent(false));
    }

    @Override
    public void dismiss() {
        if (showTime > 0) {
            long curTime = System.currentTimeMillis();
            long time = curTime - showTime;
            showTime = 0;
        }

        if (toastLoadingView != null) {
            toastLoadingView.setVisibility(View.GONE);
        }
        //EventBus.getDefault().post(new ProgressVisibleEvent(true));
        super.dismiss();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*if (bodyView != null) {
            if (mPageId == PageIds.INVALID) {
                bodyView.setTag(PageInfoTag.TAG_PAGE_INFO, PageInfoUtil.getPageIdOfDialogLoading(toastLoadingView.getLoading()));
            } else {
                bodyView.setTag(PageInfoTag.TAG_PAGE_INFO, mPageId);
            }
            if (toastLoadingView != null && toastLoadingView.getLoading() != null) {
                toastLoadingView.getLoading().setType(mType);
            }
        }*/
    }

    public void setLoadingType(int type) {
        mType = type;
    }

}
