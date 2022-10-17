package com.brins.commom.loading;

import com.brins.commom.widget.CommonLoadingView;

public interface ILoadingPresenter {
    /**
     * 注意设置时机: 在View初始化后即刻调用(保证开始动画之前设置好了时间)
     */
    void setTimeSpec(int timeSpec);
    void setTimerCallback(LoadingPresenter.LoadingCallback callback);

    void startAnimWithTimer();
    void startAnim();
    void stopAnim();
    void startTimer();
    void cancelTimer();

    void attachView(CommonLoadingView view);
    boolean checkLocation();
}
