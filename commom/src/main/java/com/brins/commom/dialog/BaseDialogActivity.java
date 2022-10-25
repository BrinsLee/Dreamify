
package com.brins.commom.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;
import com.brins.commom.R;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.base.ActivityDialogable;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.skin.SkinSetting;
import com.brins.commom.utils.log.DrLog;
import java.util.ArrayList;

/**
 * 通用标题Activity
 * 
 * @author HalZhang
 * @version 2011-11-24下午05:16:00
 */
public class BaseDialogActivity extends AbsBaseActivity implements ActivityDialogable {

    private TextView mTextView;

    private View mView;

    private View mBottomView;

    private View mButtonView;

    protected Button mOK, mCancel;

    public static final String SONG_KEY = "song";

    /**
     * type=1:本地收藏列表 type=2：网络收藏列表
     */
    public static final String PLAY_LISTS_TYPE_KEY = "type";

    public static final String SONG_LIST_KEY = "songList";

    /** 分为两种，单个和多个 */
    public static final String SONG_LIST_SIZE_TYPE = "songlistsizetype";

    /** 单个song */
    public static final int SONG_LIST_SIZE_ONE = 1;

    /** 多个song */
    public static final int SONG_LIST_SIZE_MULTI = 2;


    protected ArrayList[] mKGSongArray;

    protected boolean isNeedInitCompoment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isChangedSkin = false;
        super.onCreate(savedInstanceState);

        registerDismissReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterDismissReceiver();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mTextView = (TextView) findViewById(R.id.common_dialog_title_text);
        if (mTextView == null) {
            throw new RuntimeException("Your content must have a TextView whose id attribute is "
                    + "'R.id.common_dialog_title_text'");
        }
        mView = (View) findViewById(R.id.common_dialog_divider_line);
        if (mView == null) {
            throw new RuntimeException(
                    "Your content must have a ImageButton whose id attribute is "
                            + "'R.id.common_dialog_divider_line'");
        }
        mView.setBackgroundDrawable(SkinSetting.getColorResDrawable(this));

        if (isNeedInitCompoment) {
            mBottomView = (View) findViewById(R.id.common_dialog_bottom_divider_line);
            if (mBottomView == null) {
                throw new RuntimeException(
                        "Your content must have a ImageButton whose id attribute is "
                                + "'R.id.common_dialog_bottom_divider_line'");
            }
            mButtonView = (View) findViewById(R.id.common_dialog_button_divider_line);
            if (mButtonView == null) {
                throw new RuntimeException(
                        "Your content must have a ImageButton whose id attribute is "
                                + "'R.id.common_dialog_button_divider_line'");
            }
            mOK = (Button) findViewById(R.id.common_dialog_btn_ok);
            if (mButtonView == null) {
                throw new RuntimeException(
                        "Your content must have a ImageButton whose id attribute is "
                                + "'R.id.common_dialog_btn_ok'");
            }
            mCancel = (Button) findViewById(R.id.common_dialog_btn_cancel);
            if (mButtonView == null) {
                throw new RuntimeException(
                        "Your content must have a ImageButton whose id attribute is "
                                + "'R.id.common_dialog_btn_cancel'");
            }
            mOK.setOnClickListener(mOKClickListener);
            mOK.setTextColor(SkinSetting.getDefaultThemeColor(BaseDialogActivity.this));
            mCancel.setOnClickListener(mCancelClickListener);
        }
    }

    private OnClickListener mCancelClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onCancelButtonClick(v);
        }
    };

    private OnClickListener mOKClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onOKButtonClick(v);
        }
    };

    private boolean mCanceledOnTouchOutside = true;

    /**
     * 确定按钮事件处理
     * 
     * @param v
     */
    protected void onOKButtonClick(View v) {
    }

    /**
     * 确定按钮事件处理
     * 
     * @param v
     */
    protected void onCancelButtonClick(View v) {
        hideSoftInput();
        finish();
    }

    /**
     * 设置标题
     * 
     * @param 
     */
    protected void setCommonTitle(final String title) {
        mTextView.setText(title);
    }

    protected void setCommonTitleGravityCenter() {
        mTextView.setGravity(Gravity.CENTER);
    }

    /**
     * 设置标题
     * 
     * @param strResId 字符串id
     */
    protected void setCommonTitle(int strResId) {
        mTextView.setText(strResId);
    }

    /**
     * 设置取消按钮名称
     */
    protected void setCancelText(int strResId) {
        mCancel.setText(strResId);

    }

    /**
     * 设置取消按钮名称
     */
    protected void setCancelText(String str) {
        mCancel.setText(str);

    }

    /**
     * 设置ok按钮名称
     */
    protected void setOKBtnText(String str) {
        mOK.setText(str);

    }

    /**
     * 设置ok按钮名称
     */
    protected void setOKBtnText(int strResId) {
        mOK.setText(strResId);

    }

    /**
     * 设置OK按钮是否显示
     * 
     * @param visibility
     */
    protected void setOKBtnVisibility(boolean visibility) {
        mOK.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mButtonView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置Cancel按钮是否显示
     * 
     * @param visibility
     */
    protected void setCancelBtnVisibility(boolean visibility) {
        mCancel.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mCanceledOnTouchOutside = cancel;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanceledOnTouchOutside && event.getAction() == MotionEvent.ACTION_DOWN
                && isOutOfBounds(event)) {
            // onDismissTouchOutside();
            finish();
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(this).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

    private void registerDismissReceiver() {
        IntentFilter filter = new IntentFilter();
        
        BroadcastUtil.registerReceiver(mReceiver, filter);
        if (DrLog.DEBUG) DrLog.e("nathaniel", "dialog:registerDismissReceiver");
    }

    private void unregisterDismissReceiver() {
        BroadcastUtil.unregisterReceiver(mReceiver);
        if (DrLog.DEBUG) DrLog.e("nathaniel", "dialog:unregisterDismissReceiver");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            if (DrLog.DEBUG) DrLog.e("nathaniel", "dialog:dismiss");
        }
    };
    /*
     * protected void onDismissTouchOutside() { }
     */
}
