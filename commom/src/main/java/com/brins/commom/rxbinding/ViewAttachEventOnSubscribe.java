package com.brins.commom.rxbinding;

import android.support.annotation.NonNull;
import android.view.View;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static com.brins.commom.rxbinding.ViewAttachEvent.Kind.ATTACH;
import static com.brins.commom.rxbinding.ViewAttachEvent.Kind.DETACH;
import static com.brins.commom.rxlifecycle.internal.Preconditions.checkUiThread;

final class ViewAttachEventOnSubscribe implements Observable.OnSubscribe<ViewAttachEvent> {
  final View view;

  ViewAttachEventOnSubscribe(View view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super ViewAttachEvent> subscriber) {
    checkUiThread();

    final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
      @Override public void onViewAttachedToWindow(@NonNull final View v) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(ViewAttachEvent.create(view, ATTACH));
        }
      }

      @Override public void onViewDetachedFromWindow(@NonNull final View v) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(ViewAttachEvent.create(view, DETACH));
        }
      }
    };
    view.addOnAttachStateChangeListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.removeOnAttachStateChangeListener(listener);
      }
    });
  }
}
