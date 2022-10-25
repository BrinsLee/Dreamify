package com.brins.commom.base.lifecycle;

import android.support.annotation.IntDef;

public interface KGLifeCycleObserver {
    @interface state {

    }

    @IntDef({
        Event.ON_CREATE,
            Event.ON_START,
            Event.ON_RESUME,
            Event.ON_PAUSE,
            Event.ON_STOP,
            Event.ON_DESTROY,
            Event.ON_FRAGMENT_RESUME,
            Event.ON_FRAGMENT_PAUSE,
            Event.NOTHING})
    @interface Event {
        int NOTHING = -1;//这个不是给业务用的
        int ON_CREATE = 0;
        int ON_START = 1;//注意，这些生命周期，home键也会触发
        int ON_RESUME = 2;
        /**
         * 注意，这个回调依赖Fragment的onFragmentResume方法，对于二级页面，可能不符合预期
         */
        int ON_FRAGMENT_RESUME = 3;
        int ON_FRAGMENT_PAUSE = 4;
        int ON_PAUSE = 5;
        int ON_STOP = 6;
        int ON_DESTROY = 7;
    }

    void onStateChanged(KGLifeCycleOwner owner, @Event int event);
}