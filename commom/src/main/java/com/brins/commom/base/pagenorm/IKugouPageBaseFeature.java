package com.brins.commom.base.pagenorm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by burone on 2017/7/28.
 */

public interface IKugouPageBaseFeature {

    Context getApplicationContext();

    View findViewById(int id);

    void hideSoftInput();

    void showSoftInput();

    void runOnUITread(Runnable action);

    void setFixInputManagerLeakEnable(boolean enable);


    LayoutInflater getLayoutInflater();
}
