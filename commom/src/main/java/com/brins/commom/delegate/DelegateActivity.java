
package com.brins.commom.delegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import com.brins.commom.R;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.rxlifecycle.LifecycleProvider;
import com.brins.commom.rxlifecycle.LifecycleTransformer;
import com.brins.commom.rxlifecycle.android.ActivityEvent;
import com.brins.commom.utils.KGAssert;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import rx.Observable;

public class DelegateActivity extends AbsBaseActivity implements LifecycleProvider<ActivityEvent> {

    private TitleDelegate titleDelegate;

/*    private ListDelegate listDelegate;

    private RecyclerViewDelegate recyclerViewDelegate;

    private SwipeDelegate swipeDelegate;*/

    private RxActivityLifeDelegate rxLifeDelegate;

    /**
     * 打开标题功能
     * 
     *  标题功能事件处理，如果不需要重新实现某些点击事件，可传null
     */
    public void enableTitleDelegate() {
        titleDelegate = new TitleDelegate(this);
    }


    public TitleDelegate getTitleDelegate() {
        return titleDelegate;
    }

    /*public void enableListDelegate(OnListEventListener listener) {
        listDelegate = new ListDelegate(this, listener);
    }

    public void enableRecyclerViewDelegate(RecyclerViewDelegate.OnListEventListener listener) {
        recyclerViewDelegate = new RecyclerViewDelegate(this, listener);
    }

    public ListDelegate getListDelegate() {
        return listDelegate;
    }

    public RecyclerViewDelegate getRecyclerViewDelegate() {
        return recyclerViewDelegate;
    }

    public void enableKGPullListDelegate(OnListEventListener listener) {
        listDelegate = new KGPullListDelegate(this, listener);
    }

    public KGPullListDelegate getKGPullListDelegate() {
        return (KGPullListDelegate) listDelegate;
    }

    public void enableSwipeDelegate(SwipeDelegate.OnSwipeTabSelectedListener tabSelectedListener) {
        swipeDelegate = new SwipeDelegate(this, tabSelectedListener);
    }
*/
    public void enableRxLifeDelegate() {
        if (null == rxLifeDelegate)
            rxLifeDelegate = new RxActivityLifeDelegate(this);
    }

    //public SwipeDelegate getSwipeDelegate() {
    //    return swipeDelegate;
    //}

    @Override
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        int currentVolume = SystemUtils.getCurrVolume(this);
/*        if (KGSystemUtil.isSupportedKugouDecoder()) {
            if (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
//                if (null != getVolumnPopupWindow()) {
//                    showVolumnWindow();
//                    getVolumnPopupWindow().handleVolume(currentVolume - SystemUtils.getVolumeInterval());
//                }
//                return true;
                OneKeyIncreaseVolume.getInstance().closeOneKeyVolume(this);
                return false;
            } else if (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
//                if (null != getVolumnPopupWindow()) {
//                    showVolumnWindow();
//                    getVolumnPopupWindow().handleVolume(currentVolume + SystemUtils.getVolumeInterval());
//                }
//                return true;
                OneKeyIncreaseVolume.getInstance().checkShowOpenDialog(this);
                return false;
            }
        } else if (PlaybackServiceUtil.isUsingDLNAPlayer()) {
            if (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                PlaybackServiceUtil.setVolume(currentVolume - SystemUtils.getVolumeInterval());
            } else if (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                PlaybackServiceUtil.setVolume(currentVolume + SystemUtils.getVolumeInterval());
            }
        }*/
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    /**
     * 在initDelegates()之前要先调用所有聚合类的设置选项
     */
    public void initDelegates() {
        if (titleDelegate != null)
            titleDelegate.init();
        if (rxLifeDelegate != null)
            rxLifeDelegate.init();
        initBackBtn();
    }

    protected void initBackBtn() {

    }

    @Override
    protected void onSkinColorChanged() {
        super.onSkinColorChanged();
        if (titleDelegate != null)
            titleDelegate.onSkinColorChanged();
/*        if (listDelegate != null)
            listDelegate.onSkinColorChanged();
        if (recyclerViewDelegate != null)
            recyclerViewDelegate.onSkinColorChanged();*/
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        boolean isdialogActivity = false;
        try {
            Context ctx = createPackageContext(intent.getComponent().getPackageName(),
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Class clazz = ctx.getClassLoader().loadClass(intent.getComponent().getClassName());
            /*if (BaseDialogActivity.class.isAssignableFrom(clazz)) {
                isdialogActivity = true;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isdialogActivity) {
            if (DrLog.DEBUG) DrLog.d("PanBC-" + this.getClass().getName(), "override");
            getActivity().overridePendingTransition(R.anim.comm_activity_open_enter,
                    R.anim.comm_activity_open_exit);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        boolean isdialogActivity = false;
        try {
            Context ctx = createPackageContext(intent.getComponent().getPackageName(),
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Class clazz = ctx.getClassLoader().loadClass(intent.getComponent().getClassName());
            /*if (BaseDialogActivity.class.isAssignableFrom(clazz)) {
                isdialogActivity = true;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isdialogActivity) {
            getActivity().overridePendingTransition(R.anim.comm_activity_open_enter,
                    R.anim.comm_activity_open_exit);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(swipeDelegate!=null){
            swipeDelegate.setUserVisibleHint(true);
        }*/
        if (null != rxLifeDelegate)
            rxLifeDelegate.onResume();

        if (titleDelegate != null) {
            titleDelegate.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if(swipeDelegate!=null){
            swipeDelegate.setUserVisibleHint(false);
        }*/

        if (null != rxLifeDelegate)
            rxLifeDelegate.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.comm_activity_close_enter, R.anim.comm_activity_close_exit);
    }

    public void turnToEditMode() {

    }

    @Override
    @NonNull
    public Observable<ActivityEvent> lifecycle() {
        if (null != rxLifeDelegate)
            return rxLifeDelegate.lifecycle();
        else KGAssert.fail("lifecycle:rxLifeDelegate was not inited");
        return null;
    }

    @Override
    @NonNull
    public <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        if (null != rxLifeDelegate)
            return rxLifeDelegate.bindUntilEvent(event);
        else KGAssert.fail("lifecycle:rxLifeDelegate was not inited");
        return null;
    }

    @Override
    @NonNull
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        if (null != rxLifeDelegate)
            return rxLifeDelegate.bindToActivityLifecycle();
        else KGAssert.fail("lifecycle:rxLifeDelegate was not inited");
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != rxLifeDelegate)
            rxLifeDelegate.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != rxLifeDelegate)
            rxLifeDelegate.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != rxLifeDelegate)
            rxLifeDelegate.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != rxLifeDelegate)
            rxLifeDelegate.onDestroy();
        /*if (recyclerViewDelegate != null) {
            recyclerViewDelegate.onDestroy();
        }*/
    }
}
