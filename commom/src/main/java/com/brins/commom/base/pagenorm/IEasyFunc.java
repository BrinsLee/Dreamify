package com.brins.commom.base.pagenorm;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

/**
 * Created by burone on 2017/2/22.
 */

public interface IEasyFunc {

    void showToastLong(int strResId);

    void showToastLong(String msg);

    void showToastLong(String msg, int gravity, int xOffset, int yOffset, int duration);

    void showSuccessedToast(String msg);

    void showFailToast(String msg);

    void showToastWithIcon(int drawableId, int msgId, int duration);
    
    void showToastWithIcon(Drawable drawable, String msg, int duration);

    void showToast(int strResId);

    void showToast(Drawable drawable, int strResId);
    
    void showToast(Drawable drawable, String msg);

    void showToast(CharSequence str);

    void showToastLeft(CharSequence str);

    void showToast(int strResId, int gravity, int xOffset, int yOffset);

    void showToast(CharSequence str, int gravity, int xOffset, int yOffset);

    void showToastCenter(int strResId);

    void showToastCenter(CharSequence str);

    boolean isProgressDialogShowing();

    interface ProgressDialoginterface {
        void showProgressDialog();

        void showProgressDialog(boolean canCancel);

        void showProgressDialog(DialogInterface.OnDismissListener listener);

        void showProgressDialog(boolean canCancel, String text);

        void showProgressDialog(boolean canCancel, boolean canCancelOutside);

        void showProgressDialog(int loadingType);

        void showProgressDialog(int loadingType, boolean canCancel);

        void showProgressDialog(int loadingType, DialogInterface.OnDismissListener listener);

        void showProgressDialog(int loadingType, boolean canCancel, String text);

        void showProgressDialog(int loadingType, boolean canCancel, String text, String secondStr);

        void showProgressDialog(int loadingType, boolean canCancel, boolean canCancelOutside);
        void showProgressDialog(String text,boolean canCancel, boolean canCancelOutside);
        void dismissProgressDialog();
    }

    interface ProgressDialoginterface2 {
        void showProgressDialog(int pageId, int loadingType);

        void showProgressDialog(int pageId, int loadingType, boolean canCancel);

        void showProgressDialog(int pageId, int loadingType, DialogInterface.OnDismissListener listener);

        void showProgressDialog(int pageId, int loadingType, boolean canCancel, String text);

        void showProgressDialog(int pageId, int loadingType, boolean canCancel, String text, String secondStr);

        void showProgressDialog(int pageId, int loadingType, boolean canCancel, boolean canCancelOutside);
        void showProgressDialog(int pageId, int loadingType, boolean canCancel, boolean canCancelOutside, String text);

        void dismissProgressDialog();
    }
}
