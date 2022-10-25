package com.brins.commom.base.lifecycle;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import com.brins.commom.base.AbsFrameworkFragment;
import com.brins.commom.utils.log.DrLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 3.初次设置界面监听时，应该把当前（有效的）数据回调一遍
 * 辅助管理生命周期监听者
 * todo 注意！由于AbsFrameworkFragment还可以作为子页面出现，故onFragmentResume回调要判断一下状态
 * 二级页面不保证onFragmentResume一定会回调（二级页面onFragmentResume回调是自己做的。）
 */
public class DRLifecycleRegistry {

    private static final String TAG = "KGLifecycleRegistry";
    private final List<KGLifeCycleObserver> lifecycleObservers = Collections.synchronizedList(new ArrayList<>());
    private AbsFrameworkFragment mFragment;
    //当前Fragment对应的状态，方便页面addLifeCycleObserver之前获取到当前状态
    private @KGLifeCycleObserver.Event int kgLifeCycleEvent = KGLifeCycleObserver.Event.NOTHING;

    public void addLifeCycleObserver(KGLifeCycleObserver observer) {
        if (observer == null) {
            return;
        }
        synchronized (lifecycleObservers) {
            if (!lifecycleObservers.contains(observer)) {
                lifecycleObservers.add(observer);
            }
        }
    }

    public void remove(KGLifeCycleObserver observer) {
        if (observer == null) {
            return;
        }
        lifecycleObservers.remove(observer);
    }

    private GenericLifecycleObserver lifecycleObserver = new GenericLifecycleObserver() {
        @Override
        public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
            kgLifeCycleEvent = convert(event);
            if (kgLifeCycleEvent != KGLifeCycleObserver.Event.NOTHING) {
                notifyEvent(kgLifeCycleEvent);
            }
        }
    };

    private void notifyEvent(@KGLifeCycleObserver.Event int event) {
        synchronized (lifecycleObservers) {
            for (KGLifeCycleObserver observer : lifecycleObservers) {
                if (observer != null) {
                    observer.onStateChanged(mFragment, event);
                }
            }
        }
    }

    @KGLifeCycleObserver.Event
    private static int convert(Lifecycle.Event event) {
        switch (event) {
            case ON_CREATE:
                return KGLifeCycleObserver.Event.ON_CREATE;
            case ON_START:
                return KGLifeCycleObserver.Event.ON_START;
            case ON_RESUME:
                return KGLifeCycleObserver.Event.ON_RESUME;
            case ON_PAUSE:
                return KGLifeCycleObserver.Event.ON_PAUSE;
            case ON_STOP:
                return KGLifeCycleObserver.Event.ON_STOP;
            case ON_DESTROY:
                return KGLifeCycleObserver.Event.ON_DESTROY;
            default:
                return KGLifeCycleObserver.Event.NOTHING;
        }
    }

    public void onAttach(AbsFrameworkFragment fragment) {
        this.mFragment = fragment;
        fragment.getLifecycle().addObserver(lifecycleObserver);
        if (DrLog.isDebug()) {
            DrLog.d(TAG, "onAttach---:" + mFragment.getClass().getSimpleName());
        }
    }

    public void onFragmentResume() {
        if (mFragment != null && mFragment.isActivityCreated()) {
            notifyEvent(KGLifeCycleObserver.Event.ON_FRAGMENT_RESUME);
            if (DrLog.isDebug()) {
                DrLog.d(TAG, "onFragmentResume---:" + mFragment.getClass().getSimpleName());
            }
        }
    }

    public void onFragmentPause() {
        if (mFragment != null) {
            notifyEvent(KGLifeCycleObserver.Event.ON_FRAGMENT_PAUSE);
            if (DrLog.isDebug()) {
                DrLog.d(TAG, "onFragmentPause---:" + mFragment.getClass().getSimpleName());
            }
        }
    }


    public void onDetach() {
        mFragment.getLifecycle().removeObserver(lifecycleObserver);
        lifecycleObservers.clear();
        if (DrLog.isDebug()) {
            DrLog.d(TAG, "onDetach---:" + mFragment.getClass().getSimpleName());
        }
    }

    public @KGLifeCycleObserver.Event int getKgLifeCycleEvent() {
        return kgLifeCycleEvent;
    }
}
