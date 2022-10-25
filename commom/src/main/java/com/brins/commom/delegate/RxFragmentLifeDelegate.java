package com.brins.commom.delegate;


import com.brins.commom.rxlifecycle.LifecycleTransformer;
import com.brins.commom.rxlifecycle.RxLifecycle;
import com.brins.commom.rxlifecycle.android.FragmentEvent;
import com.brins.commom.rxlifecycle.android.RxLifecycleAndroid;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by jasonzuo on 2016/11/21.
 */

public class RxFragmentLifeDelegate extends AbstractDelegate {

    private BehaviorSubject<FragmentEvent> fragmentLifecycleSubject;

    public RxFragmentLifeDelegate(DelegateFragment fragment) {
        super(fragment);
        fragmentLifecycleSubject = BehaviorSubject.create();
    }

    public RxFragmentLifeDelegate(DelegateActivity activity) {
        super(activity);
    }

    @Override
    public void init() {

    }

    public final Observable<FragmentEvent> lifecycle() {
        return fragmentLifecycleSubject.asObservable();
    }

    public final <T> LifecycleTransformer<T> bindUntilEvent(FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(fragmentLifecycleSubject, event);
    }

    public final <T> LifecycleTransformer<T> bindToFragmentLifecycle() {
        return RxLifecycleAndroid.bindFragment(fragmentLifecycleSubject);
    }

    public void onAttach() {
        fragmentLifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    public void onCreate() {
        fragmentLifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    public void onViewCreated() {
        fragmentLifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    public void onStart() {
        fragmentLifecycleSubject.onNext(FragmentEvent.START);
    }

    public void onResume() {
        fragmentLifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    public void onPause() {
        fragmentLifecycleSubject.onNext(FragmentEvent.PAUSE);
    }

    public void onStop() {
        fragmentLifecycleSubject.onNext(FragmentEvent.STOP);
    }

    public void onDestroyView() {
        fragmentLifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
    }

    public void onDestroy() {
        fragmentLifecycleSubject.onNext(FragmentEvent.DESTROY);
    }

    public void onDetach() {
        fragmentLifecycleSubject.onNext(FragmentEvent.DETACH);
    }

}
