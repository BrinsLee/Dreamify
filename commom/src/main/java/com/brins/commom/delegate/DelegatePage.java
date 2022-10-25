package com.brins.commom.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import com.brins.commom.base.ui.IKugouPage;

public interface DelegatePage extends IKugouPage {

    String getSourcePath();

    void showToast(String msg);

    void showToast(int strResId);

    Bundle getArguments();

    void startActivity(Intent intent);

    View findViewById(int id);

    Activity getActivity();

    Context getContext();

    Context getApplicationContext();

    void finish();

    FragmentManager getPageFragmentManager();

}
