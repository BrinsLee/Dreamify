package com.brins.commom.page.callback;

import android.content.Context;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.page.toolbar.background.DefaultTitleBackgroundMode;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.kugou.page.FrameworkCallbacks;
import com.kugou.page.core.TitleBackgroundMode;
import com.kugou.page.face.MenuItem;
import com.kugou.page.utils.LogUtil;
import org.greenrobot.eventbus.EventBus;

public class KGFrameworkCallback implements FrameworkCallbacks.Callback {
    @Override
    public int getStatusBarHeight(Context context) {
        return SystemUtils.getStatusBarHeight(context);
    }

    @Override
    public MenuItem getMenuItem(int menuItemId, Object... args) {
        return null;
    }

    @Override
    public int getBackgroundLayout() {
        return R.layout.common_background_layout;
    }

    @Override
    public int getBackgroundContentId() {
        return R.id.framework_fragment_content_background;
    }

    @Override
    public int getBackgroundTitleId() {
        return R.id.framework_fragment_title_bar_background;
    }

    @Override
    public LogUtil.Logger getLogger() {
        return new LogUtil.Logger() {
            @Override
            public boolean isDebug() {
                return DrLog.DEBUG;
            }

            @Override
            public void d(String tag, String msg) {
                if (DrLog.DEBUG) DrLog.d(tag, msg);
            }

            @Override
            public void i(String tag, String msg) {
                if (DrLog.DEBUG) DrLog.i(tag, msg);
            }

            @Override
            public void e(String tag, String msg) {
                if (DrLog.DEBUG) DrLog.e(tag, msg);
            }

            @Override
            public void throwExceptionIfDebug(Throwable e) {

            }
        };
    }

    @Override
    public boolean isRenderThreadEnable() {
        return false;
    }

    @Override
    public String getRenderThreadSpecialRom() {
        return "";
    }

    @Override
    public int[] getPhysicalSS(Context context) {
        return SystemUtils.getPhysicalSS(context);
    }

    @Override
    public int getCommToolBarLayoutId() {
        return R.layout.comm_tool_bar;
    }

    @Override
    public TitleBackgroundMode getDefaultTitleBackgroundMode() {
        return new DefaultTitleBackgroundMode();
    }

    @Override
    public Context getApplicationContext() {
        return DRCommonApplication.getContext();
    }

    @Override
    public void onMenuCardSetViewMode(int viewMode) {

    }
}
