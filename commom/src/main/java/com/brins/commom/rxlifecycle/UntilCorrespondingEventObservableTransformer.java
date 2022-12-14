package com.brins.commom.rxlifecycle;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import static com.brins.commom.rxlifecycle.TakeUntilGenerator.takeUntilCorrespondingEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 *
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
final class UntilCorrespondingEventObservableTransformer<T, R> implements LifecycleTransformer<T> {

    final Observable<R> sharedLifecycle;
    final Func1<R, R> correspondingEvents;

    public UntilCorrespondingEventObservableTransformer( Observable<R> sharedLifecycle,
                                                         Func1<R, R> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents));
    }


    @Override
    public Single.Transformer<T, T> forSingle() {
        return new UntilCorrespondingEventSingleTransformer<>(sharedLifecycle, correspondingEvents);
    }


    @Override
    public Completable.Transformer forCompletable() {
        return new UntilCorrespondingEventCompletableTransformer<>(sharedLifecycle, correspondingEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilCorrespondingEventObservableTransformer<?, ?> that
            = (UntilCorrespondingEventObservableTransformer<?, ?>) o;

        if (!sharedLifecycle.equals(that.sharedLifecycle)) { return false; }
        return correspondingEvents.equals(that.correspondingEvents);
    }

    @Override
    public int hashCode() {
        int result = sharedLifecycle.hashCode();
        result = 31 * result + correspondingEvents.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UntilCorrespondingEventObservableTransformer{" +
            "sharedLifecycle=" + sharedLifecycle +
            ", correspondingEvents=" + correspondingEvents +
            '}';
    }
}
