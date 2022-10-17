package com.brins.commom.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.brins.commom.R;
import com.kugou.uilib.widget.imageview.KGUIImageView;
import com.kugou.uilib.widget.textview.KGUITextView;

public class ToastLoadingView extends FrameLayout {

    public static final String DEF_LOADING_MESSAGE = "正在加载，请稍候";
    public static final String DEF_LOADING_DELAY_MESSAGE = "网络较慢，正在努力加载";
    public static final String DEF_LOADING_FAILED = "加载失败";

    private MiniLoadingView vMiniLoading = null;
    private KGUIImageView ivLoadingFailed = null;
    private KGUITextView tvLoadingMessage = null;

    private String defDelayMessage = null;

    public ToastLoadingView(Context context) {
        super(context);
        init();
    }

    public ToastLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToastLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_kg11_common_loading_toast, this, true);
        vMiniLoading = findViewById(R.id.v_base_loading);
        vMiniLoading.setWithThemeAdjust(false);
        ivLoadingFailed = findViewById(R.id.iv_failed);
        tvLoadingMessage = findViewById(R.id.tv_loading_message);
        vMiniLoading.setOnDelayListener(new CommLoadingView.OnDelayListener() {
            @Override
            public void onDelay() {
                tvLoadingMessage.setText(TextUtils.isEmpty(defDelayMessage) ? DEF_LOADING_DELAY_MESSAGE : defDelayMessage);
            }
        });
    }

    public void setDefDelayMessage(String defDelayMessage) {
        this.defDelayMessage = defDelayMessage;
    }

    public void setLoading(String message) {
        ivLoadingFailed.setVisibility(View.GONE);
        vMiniLoading.setVisibility(View.VISIBLE);
        vMiniLoading.startLoading();
        setMessage(message);
    }

    public void setFailed(String message) {
        ivLoadingFailed.setVisibility(View.VISIBLE);
        vMiniLoading.endLoading();
        vMiniLoading.setVisibility(View.GONE);
        setMessage(message);
    }

    private void setMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            tvLoadingMessage.setVisibility(GONE);
        } else {
            tvLoadingMessage.setText(message);
            tvLoadingMessage.setVisibility(VISIBLE);
        }
    }

    public void setTextSize(int size) {
        tvLoadingMessage.setTextSize(size);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != View.VISIBLE) {
            vMiniLoading.endLoading();
        }
    }

    public CommLoadingView getLoading() {
        return vMiniLoading;
    }
}
