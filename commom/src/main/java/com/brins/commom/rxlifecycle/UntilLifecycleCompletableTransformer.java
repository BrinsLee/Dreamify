package com.brins.commom.rxlifecycle;

import rx.Completable;
import rx.Observable;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilLifecycleCompletableTransformer<T> implements Completable.Transformer {

    final Observable<T> lifecycle;

    public UntilLifecycleCompletableTransformer( Observable<T> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Completable call(Completable source) {
        return Completable.amb(
            source,
            lifecycle
                .flatMap(Functions.CANCEL_COMPLETABLE)
                .toCompletable()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilLifecycleCompletableTransformer<?> that = (UntilLifecycleCompletableTransformer<?>) o;

        return lifecycle.equals(that.lifecycle);
    }

    @Override
    public int hashCode() {
        return lifecycle.hashCode();
    }

    @Override
    public String toString() {
        return "UntilLifecycleCompletableTransformer{" +
            "lifecycle=" + lifecycle +
            '}';
    }
}
