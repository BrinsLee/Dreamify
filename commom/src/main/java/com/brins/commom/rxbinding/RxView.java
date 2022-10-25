package com.brins.commom.rxbinding;

import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import com.brins.commom.rxbinding.internal.Functions;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Action1
 * actions} for {@link View}.
 */
public final class RxView {
  /**
   * Create an observable which emits on {@code view} attach events. The emitted value is
   * unspecified and should only be used as notification.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Observable<Void> attaches( View view) {
    return Observable.create(new ViewAttachesOnSubscribe(view, true));
  }

  /**
   * Create an observable of attach and detach events on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Observable<ViewAttachEvent> attachEvents( View view) {
    return Observable.create(new ViewAttachEventOnSubscribe(view));
  }

  /**
   * Create an observable which emits on {@code view} detach events. The emitted value is
   * unspecified and should only be used as notification.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Observable<Void> detaches( View view) {
    return Observable.create(new ViewAttachesOnSubscribe(view, false));
  }

  /**
   * Create an observable which emits on {@code view} click events. The emitted value is
   * unspecified and should only be used as notification.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnClickListener} to observe
   * clicks. Only one observable can be used for a view at a time.
   */
  
  public static Observable<Void> clicks( View view) {
    return Observable.create(new ViewClickOnSubscribe(view));
  }

  /**
   * Create an observable of {@link DragEvent} for drags on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnDragListener} to observe
   * drags. Only one observable can be used for a view at a time.
   */
  
//  public static Observable<DragEvent> drags( View view) {
//    return Observable.create(new ViewDragOnSubscribe(view, Functions.FUNC1_ALWAYS_TRUE));
//  }

  /**
   * Create an observable of {@link DragEvent} for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnDragListener} to observe
   * drags. Only one observable can be used for a view at a time.
   *
   * @param handled Function invoked with each value to determine the return value of the
   * underlying {@link View.OnDragListener}.
   */
  
//  public static Observable<DragEvent> drags( View view,
//       Func1<? super DragEvent, Boolean> handled) {
//    return Observable.create(new ViewDragOnSubscribe(view, handled));
//  }

  /**
   * Create an observable for draws on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link ViewTreeObserver#addOnDrawListener} to
   * observe draws. Multiple observables can be used for a view at a time.
   */
  
//  public static Observable<Void> draws( View view) {
//    return Observable.create(new ViewTreeObserverDrawOnSubscribe(view));
//  }

  /**
   * Create an observable of booleans representing the focus of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnFocusChangeListener} to observe
   * focus change. Only one observable can be used for a view at a time.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  
//  public static Observable<Boolean> focusChanges( View view) {
//    return Observable.create(new ViewFocusChangeOnSubscribe(view));
//  }

  /**
   * Create an observable which emits on {@code view} globalLayout events. The emitted value is
   * unspecified and should only be used as notification.
   * <p></p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link
   * ViewTreeObserver#addOnGlobalLayoutListener} to observe global layouts. Multiple observables
   * can be used for a view at a time.
   */
  
//  public static Observable<Void> globalLayouts( View view) {
//    return Observable.create(new ViewTreeObserverGlobalLayoutOnSubscribe(view));
//  }

  /**
   * Create an observable of hover events for {@code view}.
   * <p>
   * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
   * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
   * on a different thread). If you want to cache or delay reading the items emitted then you must
   * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
   * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnHoverListener} to observe
   * touches. Only one observable can be used for a view at a time.
   */
  
//  public static Observable<MotionEvent> hovers( View view) {
//    return hovers(view, Functions.FUNC1_ALWAYS_TRUE);
//  }

  /**
   * Create an observable of hover events for {@code view}.
   * <p>
   * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
   * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
   * on a different thread). If you want to cache or delay reading the items emitted then you must
   * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
   * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnHoverListener} to observe
   * touches. Only one observable can be used for a view at a time.
   *
   * @param handled Function invoked with each value to determine the return value of the
   * underlying {@link View.OnHoverListener}.
   */
  
//  public static Observable<MotionEvent> hovers( View view,
//       Func1<? super MotionEvent, Boolean> handled) {
//    return Observable.create(new ViewHoverOnSubscribe(view, handled));
//  }

  /**
   * Create an observable which emits on {@code view} layout changes. The emitted value is
   * unspecified and should only be used as notification.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
//  public static Observable<Void> layoutChanges( View view) {
//    return Observable.create(new ViewLayoutChangeOnSubscribe(view));
//  }

  /**
   * Create an observable of layout-change events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
//  public static Observable<ViewLayoutChangeEvent> layoutChangeEvents( View view) {
//    return Observable.create(new ViewLayoutChangeEventOnSubscribe(view));
//  }

  /**
   * Create an observable which emits on {@code view} long-click events. The emitted value is
   * unspecified and should only be used as notification.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnLongClickListener} to observe
   * long clicks. Only one observable can be used for a view at a time.
   */
  
//  public static Observable<Void> longClicks( View view) {
//    return Observable.create(new ViewLongClickOnSubscribe(view, Functions.FUNC0_ALWAYS_TRUE));
//  }

  /**
   * Create an observable which emits on {@code view} long-click events. The emitted value is
   * unspecified and should only be used as notification.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnLongClickListener} to observe
   * long clicks. Only one observable can be used for a view at a time.
   *
   * @param handled Function invoked each occurrence to determine the return value of the
   * underlying {@link View.OnLongClickListener}.
   */
  
//  public static Observable<Void> longClicks( View view,  Func0<Boolean> handled) {
//    return Observable.create(new ViewLongClickOnSubscribe(view, handled));
//  }

  /**
   * Create an observable for pre-draws on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link ViewTreeObserver#addOnPreDrawListener} to
   * observe pre-draws. Multiple observables can be used for a view at a time.
   */
  
//  public static Observable<Void> preDraws( View view,
//       Func0<Boolean> proceedDrawingPass) {
//    return Observable.create(new ViewTreeObserverPreDrawOnSubscribe(view, proceedDrawingPass));
//  }

  /**
   * Create an observable of scroll-change events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
//  public static Observable<ViewScrollChangeEvent> scrollChangeEvents( View view) {
//    return Observable.create(new ViewScrollChangeEventOnSubscribe(view));
//  }

  /**
   * Create an observable of integers representing a new system UI visibility for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses
   * {@link View#setOnSystemUiVisibilityChangeListener} to observe system UI visibility changes.
   * Only one observable can be used for a view at a time.
   */
  
  public static Observable<Integer> systemUiVisibilityChanges( View view) {
    return Observable.create(new ViewSystemUiVisibilityChangeOnSubscribe(view));
  }

  /**
   * Create an observable of touch events for {@code view}.
   * <p>
   * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
   * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
   * on a different thread). If you want to cache or delay reading the items emitted then you must
   * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
   * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnTouchListener} to observe
   * touches. Only one observable can be used for a view at a time.
   */
  
  public static Observable<MotionEvent> touches( View view) {
    return touches(view, Functions.FUNC1_ALWAYS_TRUE);
  }

  /**
   * Create an observable of touch events for {@code view}.
   * <p>
   * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
   * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
   * on a different thread). If you want to cache or delay reading the items emitted then you must
   * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
   * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link View#setOnTouchListener} to observe
   * touches. Only one observable can be used for a view at a time.
   *
   * @param handled Function invoked with each value to determine the return value of the
   * underlying {@link View.OnTouchListener}.
   */
  
  public static Observable<MotionEvent> touches( View view,
       Func1<? super MotionEvent, Boolean> handled) {
    return Observable.create(new ViewTouchOnSubscribe(view, handled));
  }

  /**
   * An action which sets the activated property of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Action1<? super Boolean> activated( final View view) {
    return new Action1<Boolean>() {
      @Override public void call(Boolean value) {
        view.setActivated(value);
      }
    };
  }

  /**
   * An action which sets the clickable property of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Action1<? super Boolean> clickable( final View view) {
    return new Action1<Boolean>() {
      @Override public void call(Boolean value) {
        view.setClickable(value);
      }
    };
  }

  /**
   * An action which sets the enabled property of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Action1<? super Boolean> enabled( final View view) {
    return new Action1<Boolean>() {
      @Override public void call(Boolean value) {
        view.setEnabled(value);
      }
    };
  }

  /**
   * An action which sets the pressed property of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Action1<? super Boolean> pressed( final View view) {
    return new Action1<Boolean>() {
      @Override public void call(Boolean value) {
        view.setPressed(value);
      }
    };
  }

  /**
   * An action which sets the selected property of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Action1<? super Boolean> selected( final View view) {
    return new Action1<Boolean>() {
      @Override public void call(Boolean value) {
        view.setSelected(value);
      }
    };
  }

  /**
   * An action which sets the visibility property of {@code view}. {@code false} values use
   * {@code View.GONE}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  
  public static Action1<? super Boolean> visibility( View view) {
    return visibility(view, View.GONE);
  }

  /**
   * An action which sets the visibility property of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   *
   * @param visibilityWhenFalse Visibility to set on a {@code false} value ({@code View.INVISIBLE}
   * or {@code View.GONE}).
   */
  
  public static Action1<? super Boolean> visibility( final View view,
      final int visibilityWhenFalse) {
    return new Action1<Boolean>() {
      @Override public void call(Boolean value) {
        view.setVisibility(value ? View.VISIBLE : visibilityWhenFalse);
      }
    };
  }

  private RxView() {
    throw new AssertionError("No instances.");
  }
}
