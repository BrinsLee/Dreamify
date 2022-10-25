package com.brins.commom.widget;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.loading.LoadingPresenter;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.worker.Instruction;
import com.brins.commom.worker.WorkScheduler;
import java.lang.ref.WeakReference;

/**
 * @author lipeilin
 * @date 2022/10/15
 * @desc
 */
public class LoadingManager {
    private final static String TAG = "LoadingManager";
    public final static int SCREEN_WIDTH = SystemUtils.getScreenWidth(DRCommonApplication.getContext());
    public static int timeLevel10 = TimeSpec.makeTime(10, 0);

    private WorkScheduler timerHandler;
    private Handler mainHandler;
    private final static int MSG_TIMER_START = 0x00;
    private final static int MSG_TIMER_PRIMARY = 0x01;
    private final static int MSG_TIMER_SECONDARY = 0x02;

    private WorkScheduler.Callback handlerCallback = new WorkScheduler.Callback() {

        @Override public boolean handleInstruction(Instruction msg) {
            switch (msg.what) {
                case MSG_TIMER_SECONDARY:
                case MSG_TIMER_PRIMARY:
                case MSG_TIMER_START:
            }
            return false;
        }
    };

    private LoadingManager() {
        timerHandler = new WorkScheduler("LoadingManager", handlerCallback);
        mainHandler = new Handler(Looper.getMainLooper());

    }

    private static class InstanceHolder {
        private final static LoadingManager sInstance = new LoadingManager();
    }

    public static LoadingManager getInstance() {
        return InstanceHolder.sInstance;
    }

    public void removeTimer(WeakReference<LoadingPresenter.TimerCallback> weakCallback) {
        timerHandler.removeInstructions(MSG_TIMER_START, weakCallback);
        timerHandler.removeInstructions(MSG_TIMER_PRIMARY, weakCallback);
        timerHandler.removeInstructions(MSG_TIMER_SECONDARY, weakCallback);
    }

    public void postTimer(WeakReference<LoadingPresenter.TimerCallback> weakCallback, int timeSpec) {
        removeTimer(weakCallback); // remove before post.


        int primaryTime = TimeSpec.getPrimaryTime(timeSpec);
        int secondaryTime = TimeSpec.getSecondaryTime(timeSpec);

        Instruction startMsg = timerHandler.obtainInstruction(MSG_TIMER_START, weakCallback);
        timerHandler.sendInstruction(startMsg);
        if (primaryTime > 0) {
            Instruction msg = timerHandler.obtainInstruction(MSG_TIMER_PRIMARY, weakCallback);
            timerHandler.sendInstructionDelayed(msg, primaryTime * 1000);
        }
        if (secondaryTime > 0) {
            Instruction msg = timerHandler.obtainInstruction(MSG_TIMER_SECONDARY, weakCallback);
            timerHandler.sendInstructionDelayed(msg, secondaryTime * 1000);
        }
    }

/*    public void startAnimAndTimer(View view, int viewId) {
        if (view instanceof XCommonLoadingLayout) {
            ((XCommonLoadingLayout) view).getLoadingPresenter().startAnimWithTimer();
        } else if (view instanceof ViewGroup) {
            View ladingView = view.findViewById(viewId);
            if (ladingView instanceof XCommonLoadingLayout) {
                ((XCommonLoadingLayout) ladingView).getLoadingPresenter().startAnimWithTimer();
            }
        }
    }*/

    public int getDefaultTime() {
        return timeLevel10;
    }


}
