package com.brins.commom.delegate;

import com.brins.commom.rxlifecycle.LifecycleTransformer;
import com.brins.commom.rxlifecycle.RxLifecycle;
import com.brins.commom.rxlifecycle.android.ActivityEvent;
import com.brins.commom.rxlifecycle.android.RxLifecycleAndroid;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by jasonzuo on 2016/11/21.
 */

public class RxActivityLifeDelegate extends AbstractDelegate {

    private BehaviorSubject<ActivityEvent> activityLifecycleSubject;

    public RxActivityLifeDelegate(DelegateFragment fragment) {
        super(fragment);
    }

    public RxActivityLifeDelegate(DelegateActivity activity) {
        super(activity);
        activityLifecycleSubject = BehaviorSubject.create();
    }

    @Override
    public void init() {
    }

    public final Observable<ActivityEvent> lifecycle() {
        return activityLifecycleSubject.asObservable();
    }

    public final <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(activityLifecycleSubject, event);
    }

    public final <T> LifecycleTransformer<T> bindToActivityLifecycle() {
        return RxLifecycleAndroid.bindActivity(activityLifecycleSubject);
    }

    protected void onCreate() {
        activityLifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    protected void onStart() {
        activityLifecycleSubject.onNext(ActivityEvent.START);
    }

    protected void onResume() {
        activityLifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    protected void onPause() {
        activityLifecycleSubject.onNext(ActivityEvent.PAUSE);
    }

    protected void onStop() {
        activityLifecycleSubject.onNext(ActivityEvent.STOP);
    }

    protected void onDestroy() {
        activityLifecycleSubject.onNext(ActivityEvent.DESTROY);
    }
}
