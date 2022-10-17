package com.brins.commom.page.framework;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.brins.commom.utils.xscreen.ScreenHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc 可以获取旗下fragments的activity
 */
public abstract class StateFragmentActivity extends DreamFragmentActivity{

    private ArrayList<WeakReference<Fragment>> fragmentList = new ArrayList<WeakReference<Fragment>>();
    private boolean appExiting = false;
    protected Bundle savedInstanceState = null;
    protected boolean restoreCheckHasPermission = true;
    private static boolean hadCheckedBasicPermission = false;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenHandler.setHuaweiWindowInCutout(getWindow());
        super.onCreate(savedInstanceState);
    }
}
